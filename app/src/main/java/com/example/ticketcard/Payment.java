package com.example.ticketcard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketcard.Services.DarajaApiClient;
import com.example.ticketcard.databinding.ActivityPaymentBinding;
import com.example.ticketcard.model.AccessToken;
import com.example.ticketcard.model.STKPush;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Payment extends AppCompatActivity implements View.OnClickListener {

    // Declare variables
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private ActivityPaymentBinding binding;
    private String price;
    private String userEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        price = getIntent().getStringExtra("Price");
        userEmail = getIntent().getStringExtra("UserEmail");
        userName = getIntent().getStringExtra("UserName");
        String seatDetailsString = getIntent().getStringExtra("SeatDetails");
        Log.d("Payment", "payment Username is  " + userName);
        Log.d("Payment", "payment User email is " + userEmail);

        // Parse the seat details string
        Map<String, SeatsAdapter.SeatDetail> seatDetailsMap = new HashMap<>();
        if (seatDetailsString != null && !seatDetailsString.isEmpty()) {
            String[] seatDetailsArray = seatDetailsString.split(";");
            for (String seatDetailString : seatDetailsArray) {
                String[] seatDetailParts = seatDetailString.split(":");
                if (seatDetailParts.length == 3) {
                    String seatName = seatDetailParts[0];
                    String seatType = seatDetailParts[1];
                    int seatPrice = Integer.parseInt(seatDetailParts[2]);
                    seatDetailsMap.put(seatName, new SeatsAdapter.SeatDetail(seatName, seatType, seatPrice));
                }
            }
        }

        // Use the received data as needed
        binding.etAmount.setText(price);

        // Print seat details for verification
        for (Map.Entry<String, SeatsAdapter.SeatDetail> entry : seatDetailsMap.entrySet()) {
            SeatsAdapter.SeatDetail seatDetail = entry.getValue();
            Log.d("PaymentActivity", "Seat: " + seatDetail.seatName + ", Type: " + seatDetail.seatType + ", Price: " + seatDetail.seatPrice);
        }

        // Ensure binding is not null before accessing views
        if (binding == null || binding.getRoot() == null) {
            throw new RuntimeException("Error inflating binding or getting root view");
        }

        // Initialize ProgressDialog
        mProgressDialog = new ProgressDialog(this);

        // Initialize API client
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true);

        // Set click listener for the pay button
        binding.btnPay.setOnClickListener(this);

        // Get access token
        getAccessToken();
    }

    // Method to get access token from API
    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Set auth token if request is successful
                    mApiClient.setAuthToken(response.body().accessToken);
                } else {
                    Log.e("PaymentActivity", "Access token response not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Timber.e(t, "Failed to get access token");
            }
        });
    }

    // Click listener for pay button
    @Override
    public void onClick(View view) {
        if (view == binding.btnPay) {
            String phone_number = binding.etPhone.getText().toString().trim();
            String amount = String.valueOf(price);

            // Validate phone number and amount
            if (phone_number.isEmpty()) {
                Toast.makeText(this, "Phone number required", Toast.LENGTH_SHORT).show();
            } else if (amount.isEmpty()) {
                Toast.makeText(this, "You're a hacker!!!", Toast.LENGTH_SHORT).show();
            } else {
                // Perform STK push when both fields are filled
                performSTKPush(phone_number, amount);
            }
        }
    }

    // Method to perform STK push
    public void performSTKPush(String phone_number, String amount) {
        // Show progress dialog while processing request
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        // Generate encoded password for authentication
        String timestamp = com.example.ticketcard.Utils.getTimestamp();
        String toEncode = "174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + timestamp;

        // Encode password using Base64
        byte[] byteArray = toEncode.getBytes(StandardCharsets.UTF_8);
        String encodedPassword;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedPassword = Base64.getEncoder().encodeToString(byteArray);
        } else {
            encodedPassword = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
        }

        // Create STKPush object with required parameters
        STKPush stkPush = new STKPush(
                "174379",  // BusinessShortCode
                encodedPassword,  // Password
                timestamp,  // Timestamp
                "CustomerPayBillOnline",  // TransactionType
                Integer.parseInt(amount),  // Amount
                "254715798225",  // PartyA
                "174379",  // PartyB
                com.example.ticketcard.Utils.sanitizePhoneNumber(phone_number),  // PhoneNumber
                "https://mydomain.com/path",  // CallBackURL
                "CompanyXLTD",  // AccountReference
                "Payment of X"  // TransactionDesc
        );

        // Disable access token retrieval after first call
        mApiClient.setGetAccessToken(false);

        // Make API call to perform STK push
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                // Dismiss progress dialog after API call completes
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        // Log success message if request is successful
                        Timber.d("post submitted to API. %s", response.body());
                        Log.d("PaymentActivity", "STK Push response successful");

                        // Save seat details to Firebase
                        saveReservedSeatsToFirebase();
                    } else {
                        if (response.errorBody() != null) {
                            Timber.e("Response error: %s", response.errorBody().string());
                        }
                        Log.e("PaymentActivity", "STK Push response not successful");
                    }
                } catch (Exception e) {
                    Timber.e(e, "Error processing STK Push response");
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                // Dismiss progress dialog if request fails
                mProgressDialog.dismiss();
                Timber.e(t, "STK Push request failed");
            }
        });
    }

    private void saveReservedSeatsToFirebase() {
        Log.d("PaymentActivity", "Saving reserved seats to Firebase");

        // Get Firebase database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Retrieve user data and seat details
        //String userEmail = getIntent().getStringExtra("UserEmail");
        //String userName = getIntent().getStringExtra("UserName");
        String seatDetailsString = getIntent().getStringExtra("SeatDetails");

        // Log the retrieved intent data
        Log.d("PaymentActivity", "UserEmail: " + userEmail);
        Log.d("PaymentActivity", "UserName: " + userName);
        Log.d("PaymentActivity", "SeatDetailsString: " + seatDetailsString);

        // Parse the seat details string
        Map<String, Map<String, Object>> seatDetailsMap = new HashMap<>();
        if (seatDetailsString != null && !seatDetailsString.isEmpty()) {
            String[] seatDetailsArray = seatDetailsString.split(";");
            for (String seatDetailString : seatDetailsArray) {
                String[] seatDetailParts = seatDetailString.split(":");
                if (seatDetailParts.length == 3) {
                    String seatName = seatDetailParts[0];
                    String seatType = seatDetailParts[1];
                    int seatPrice = Integer.parseInt(seatDetailParts[2]);
                    Map<String, Object> seatDetailMap = new HashMap<>();
                    seatDetailMap.put("seatName", seatName);
                    seatDetailMap.put("seatType", seatType);
                    seatDetailMap.put("seatPrice", seatPrice);
                    seatDetailsMap.put(seatName, seatDetailMap);
                    // Log each parsed seat detail
                    Log.d("PaymentActivity", "Parsed seat detail - Name: " + seatName + ", Type: " + seatType + ", Price: " + seatPrice);
                } else {
                    Log.e("PaymentActivity", "Invalid seat detail format: " + seatDetailString);
                }
            }
        } else {
            Log.e("PaymentActivity", "Seat details string is null or empty");
        }

        // Create a map for reserved seats data
        Map<String, Object> reservedSeatsData = new HashMap<>();
        reservedSeatsData.put("userEmail", userEmail);
        reservedSeatsData.put("userName", userName);
        reservedSeatsData.put("seats", seatDetailsMap);

        // Log the reserved seats data map
        Log.d("PaymentActivity", "Reserved seats data: " + reservedSeatsData.toString());

        if (userName == null || userName.isEmpty()) {
            Log.e("PaymentActivity", "UserName is null or empty, cannot save to Firebase");
            return;
        }

        // Save the data under reservedSeats node in Firebase
        databaseReference.child("reservedSeats").child(userName).setValue(reservedSeatsData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PaymentActivity", "Seat details saved successfully");
                        Toast.makeText(Payment.this, "Seat details saved successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), TicketGeneration.class));
                    } else {
                        Log.e("PaymentActivity", "Failed to save seat details to Firebase");
                        Toast.makeText(Payment.this, "Failed to save seat details", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PaymentActivity", "Firebase saving failed", e);
                    Toast.makeText(Payment.this, "Failed to save seat details", Toast.LENGTH_SHORT).show();
                });
    }



}
