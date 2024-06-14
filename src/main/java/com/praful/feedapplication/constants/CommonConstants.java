package com.praful.feedapplication.constants;

public class CommonConstants {
    public enum TransactionType {
        BUY,
        SELL
    }

    public enum TransactionStatus {
        PROCESSING,
        SUCCESSFUL,
        PENDING,
        FAILED,
        REFUNDED
    }

    public static final double USER_INITIAL_GOLD_BALANCE = 0.0;
    public static final double USER_INITIAL_AMOUNT_BALANCE = 50000.0;
    public static final String METAL = "XAU";
    public static final String CURRENCY = "INR";
    public static final String WEIGHT_UNIT = "g";
    public static final String USER_ADDED = "User has been added successfully";
    public static final String USER_CREATED = "User has been created successfully";
    public static final String USER_DELETED = "User has been deleted successfully";
}
