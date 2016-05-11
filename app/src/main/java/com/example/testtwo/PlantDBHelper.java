package com.example.testtwo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlantDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PlantsDB.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlantContract.PlantsTable.TABLE_NAME + " (" +
                    PlantContract.PlantsTable._ID + " INTEGER PRIMARY KEY, " +
                    PlantContract.PlantsTable.COLUMN_COMMON + TEXT_TYPE + COMMA +
                    PlantContract.PlantsTable.COLUMN_BOTANICAL + TEXT_TYPE + COMMA +
                    PlantContract.PlantsTable.COLUMN_ZONE + TEXT_TYPE + COMMA +
                    PlantContract.PlantsTable.COLUMN_LIGHT + TEXT_TYPE + COMMA +
                    PlantContract.PlantsTable.COLUMN_PRICE + TEXT_TYPE + COMMA +
                    PlantContract.PlantsTable.COLUMN_AVAILABILITY + TEXT_TYPE + " )";

    private  static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PlantContract.PlantsTable.TABLE_NAME;

    public PlantDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
