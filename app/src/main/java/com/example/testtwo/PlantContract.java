package com.example.testtwo;

import android.provider.BaseColumns;

public class PlantContract {
    private PlantContract() {}

    public static abstract class PlantsTable implements BaseColumns {
        public static final String TABLE_NAME = "planttable";
        public static final String COLUMN_COMMON = "common";
        public static final String COLUMN_BOTANICAL = "botanical";
        public static final String COLUMN_ZONE = "zone";
        public static final String COLUMN_LIGHT = "light";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_AVAILABILITY = "availability";
    }
}
