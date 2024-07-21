package com.example.ticketcard.Interceptor;

import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    // Field to store the authentication token.
    private String mAuthToken;

    public AuthInterceptor(String authToken) {
        // It initializes the mAuthToken with a user-provided token.
        mAuthToken = authToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        // Modifying the incoming request to include an Authorization header.
        Request request  = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + mAuthToken)
                .build();

        // Proceeding with the chain of interceptors by passing the modified request.
        return chain.proceed(request);
    }
}

