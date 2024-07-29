package com.example.ticketcard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // Method to get the current timestamp
    public static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date());
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
}
