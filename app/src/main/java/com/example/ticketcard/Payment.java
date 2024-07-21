package com.example.ticketcard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketcard.Services.DarajaApiClient;
import com.example.ticketcard.databinding.ActivityPaymentBinding;
import com.example.ticketcard.model.AccessToken;
import com.example.ticketcard.model.STKPush;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Payment extends AppCompatActivity implements View.OnClickListener {

    // Declare variables
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Timber.e(t, "Failed to get access token");
            }
        });
    }

    //Click listener for pay button
    @Override
    public void onClick(View view) {
        if (view == binding.btnPay) {
            String phone_number = binding.etPhone.getText().toString().trim();
            String amount = binding.etAmount.getText().toString().trim();

            // Validate phone number and amount
            if (phone_number.isEmpty()) {
                Toast.makeText(this, "Phone number required", Toast.LENGTH_SHORT).show();
            } else if (amount.isEmpty()) {
                Toast.makeText(this, "Amount required", Toast.LENGTH_SHORT).show();
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
        String timestamp = Utils.getTimestamp();
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
                Utils.sanitizePhoneNumber(phone_number),  // PhoneNumber
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
                    } else {
                        if (response.errorBody() != null) {

                            // Log error message if request fails
                            Timber.e("Response %s", response.errorBody().string());
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
}
