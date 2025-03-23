package com.example.questifyv1.database;

import android.provider.BaseColumns;

public class UserContract {
    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_WALLET = "wallet";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TOTP_SECRET = "totp_secret";
    }
}
