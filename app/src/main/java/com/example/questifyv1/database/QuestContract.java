package com.example.questifyv1.database;

import android.provider.BaseColumns;

public final class QuestContract {

    private QuestContract() {

    }
    /* Inner class that defines the table contents */
    public static class QuestEntry implements BaseColumns{
        public static final String TABLE_NAME = "quest";
        public static final String COLUMN_NAME_TITLE= "title";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";

        public static final String COLUMN_NAME_REWARD = "reward";
    }
}
