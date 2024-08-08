package com.example.ticketcard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketcard.Services.DarajaApiClient;
import com.example.ticketcard.databinding.ActivityPaymentBinding;
import com.example.ticketcard.model.AccessToken;
import com.example.ticketcard.model.MpesaRequest;
import com.example.ticketcard.model.STKCallbackResponse;
import com.example.ticketcard.model.STKPush;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    private String token,encodedPassword,timestamp;
    private String price;
    private String userEmail;
    private String userName;
    private String stadiumName;
    private String matchName, matchMonth, matchDate, matchTime;
    private String round;
    Map<String, SeatsAdapter.SeatDetail> seatDetailsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // When reserving seats we should store stadium name, match name, and user

        // Retrieve the user name and email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found
        userEmail = sharedPreferences.getString("userEmail", "email"); // "email" is the default value if "userEmail" is not found

        price = getIntent().getStringExtra("Price");
        String seatDetailsString = getIntent().getStringExtra("SeatDetails");
        stadiumName = getIntent().getStringExtra("stadiumName");
        matchName = getIntent().getStringExtra("matchName");
        matchDate = getIntent().getStringExtra("matchDate");
        matchMonth = getIntent().getStringExtra("matchMonth");
        matchTime = getIntent().getStringExtra("matchTime");
        round = getIntent().getStringExtra("round");
        Log.d("Payment", "payment Username is  " + userName);
        Log.d("Payment", "payment User email is " + userEmail);

        // Parse the seat details string
        seatDetailsMap = parseSeatDetails(seatDetailsString);


        // Parse the seat details string
//        Map<String, SeatsAdapter.SeatDetail> seatDetailsMap = new HashMap<>();
//        if (seatDetailsString != null && !seatDetailsString.isEmpty()) {
//            String[] seatDetailsArray = seatDetailsString.split(";");
//            for (String seatDetailString : seatDetailsArray) {
//                String[] seatDetailParts = seatDetailString.split(":");
//                if (seatDetailParts.length == 3) {
//                    String seatName = seatDetailParts[0];
//                    String seatType = seatDetailParts[1];
//                    int seatPrice = Integer.parseInt(seatDetailParts[2]);
//                    seatDetailsMap.put(seatName, new SeatsAdapter.SeatDetail(seatName, seatType, seatPrice));
//                }
//            }
//        }

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

                    // Store token for later use
                    token = response.body().accessToken;
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
        timestamp = getTimestamps();
        String toEncode = "174379" + "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919" + timestamp;

        // Encode password using Base64
        byte[] byteArray = toEncode.getBytes(StandardCharsets.UTF_8);
        //String encodedPassword;
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
                sanitizePhoneNumber(phone_number),  // PhoneNumber
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

                        // Start checking transaction status
                        checkTransactionStatus(response.body().getCheckoutRequestID());

                        // Display success message
                        Toast.makeText(Payment.this, "Request sent. Please complete payment on your phone.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (response.errorBody() != null) {

                            // Log error message if request fails
                            Timber.e("Response %s", response.errorBody().string());

                            // Display error message
                            Toast.makeText(Payment.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Timber.e(e, "Error processing response");
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {

                // Dismiss progress dialog if request fails
                mProgressDialog.dismiss();
                Timber.e(t, "Request failed");
            }
        });
    }

    public void checkTransactionStatus(String checkoutRequestID) {
        // Simulate checking transaction status with a delayed task
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call MpesaRequest to get transaction status
                MpesaRequest mpesaRequest = new MpesaRequest();

                //get access token from responce body

                mpesaRequest.sendRequest("174379", encodedPassword, timestamp, checkoutRequestID, token, new MpesaRequest.MpesaRequestCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Handle the response
                        Log.d("Transaction status response: %s", response);
                        // Parse the response and handle transaction status
                        //convert response data to dictionary
                        Gson gson = new Gson();
                        STKCallbackResponse stkCallbackResponse = gson.fromJson(response, STKCallbackResponse.class);

                        //if result code is 0, transaction is successful
                        if (stkCallbackResponse.getResultCode().equals("0")) {
                            // Handle successful transaction
                            System.out.println("Transaction successful");

                            saveReservedSeatsToFirebase(seatDetailsMap);

                            Looper.prepare();
                            Toast.makeText(Payment.this, "Transaction Successful",Toast.LENGTH_SHORT).show();
                            Looper.loop();


                        } else {
                            // Handle failed transaction
                            System.out.println("Transaction failed");

                            Looper.prepare();
                            Toast.makeText(Payment.this, stkCallbackResponse.getResultDesc(),Toast.LENGTH_SHORT).show();
                            Looper.loop();

                        }


                    }

                    @Override
                    public void onFailure(String error) {
                        Timber.e("Failed to get transaction status: %s", error);
                    }
                });
            }
        }, 10000); // Check after 5 seconds
    }

    private void saveReservedSeatsToFirebase(Map<String, SeatsAdapter.SeatDetail> seatDetailsMap) {
        Log.d("PaymentActivity", "Saving reserved seats to Firebase");

        String seatsKey = stadiumName + "/" + matchName + "/" + userName ;
        DatabaseReference seatsRef = FirebaseDatabase.getInstance().getReference("reservedSeats").child(seatsKey);

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

        if (userName == null || userName.isEmpty()) {
            Log.e("PaymentActivity", "UserName is null or empty, cannot save to Firebase");
            return;
        }

        // When reserving seats we should store stadium name, match name, and user

        seatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, SeatsAdapter.SeatDetail> existingSeatDetails = new HashMap<>();
                if(snapshot.exists()){
                    for(DataSnapshot seatSnap : snapshot.child("seats").getChildren()) {
                        SeatsAdapter.SeatDetail seatDetail = seatSnap.getValue(SeatsAdapter.SeatDetail.class);
                        if (seatDetail != null) {
                            existingSeatDetails.put(seatSnap.getKey(), seatDetail);
                        }
                        //Map<String, Map<String, Object>> updatedMap = new HashMap<>();
                    }

                }
                // Combine existing and new seat details
                existingSeatDetails.putAll(seatDetailsMap);

                // Create a map for reserved seats data
                Map<String, Object> reservedSeatsDataCombined = new HashMap<>();
                reservedSeatsDataCombined.put("userEmail", userEmail);
                reservedSeatsDataCombined.put("userName", userName);
                reservedSeatsDataCombined.put("matchMonth", matchMonth);
                reservedSeatsDataCombined.put("matchDate",matchDate);
                reservedSeatsDataCombined.put("matchTime", matchTime);
                reservedSeatsDataCombined.put("round", round);
                reservedSeatsDataCombined.put("ticketID", generateRandomString());
                reservedSeatsDataCombined.put("seats", existingSeatDetails);

                // Save combined seat details back to Firebase
                seatsRef.setValue(reservedSeatsDataCombined, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(Payment.this, "Seat details saved to Firebase", Toast.LENGTH_SHORT).show();
                            Log.d("PaymentActivity", "Seat details saved successfully");
                            Intent intent = new Intent(getApplicationContext(), TicketGeneration.class);
                            intent.putExtra("matchName", matchName);
                            intent.putExtra("stadiumName", stadiumName);

                            startActivity(intent);
                        } else {
                            Timber.e(databaseError.toException(), "Failed to save seat details to Firebase");
                            Toast.makeText(Payment.this, "Failed to save seat details to Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Method to parse seat details string
    private Map<String, SeatsAdapter.SeatDetail> parseSeatDetails(String seatDetailsString) {
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
        return seatDetailsMap;
    }


    // Method to get the current timestamp
    private String getTimestamps() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date()).toString();
    }

    // Method to sanitize the phone number
    public static String sanitizePhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return phoneNumber.replaceFirst("0", "254");
        } else if (phoneNumber.startsWith("+")) {
            return phoneNumber.replace("+", "");
        } else if (phoneNumber.startsWith("7")) {
            return "254" + phoneNumber;
        }
        return phoneNumber;
    }

        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        private static final SecureRandom RANDOM = new SecureRandom();

        public static String generateRandomString() {
            int length = 8 + RANDOM.nextInt(5); // Generate a random length between 8 and 12
            StringBuilder stringBuilder = new StringBuilder(length);

            for (int i = 0; i < length; i++) {
                stringBuilder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }

            return stringBuilder.toString();
        }

}