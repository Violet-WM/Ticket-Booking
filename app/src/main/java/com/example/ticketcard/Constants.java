package com.example.ticketcard;

public class Constants {
    // Connection timeout duration (in milliseconds)
    public static final int CONNECT_TIMEOUT = 60 * 1000;

    // Read timeout duration (in milliseconds)
    public static final int READ_TIMEOUT = 60 * 1000;

    // Write timeout duration (in milliseconds)
    public static final int WRITE_TIMEOUT = 60 * 1000;

    // Base URL for the API
    public static final String BASE_URL = "https://sandbox.safaricom.co.ke/";

    // Business short code
    public static final String BUSINESS_SHORT_CODE = "174379";

    // Passkey for authentication
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";

    // Type of transaction
    public static final String TRANSACTION_TYPE = "CustomermpesaOnline";

    // Party B (same as business short code above)
    public static final String PARTYB = "174379"; // same as business shortcode above

    // Callback URL for transaction updates
    public static final String CALLBACKURL = "https://mydomain.com/path";
}
