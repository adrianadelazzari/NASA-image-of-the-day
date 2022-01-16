package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The ImageDatabaseOpenHelper class creates the schema and table used in the application.
 *
 * @author Adriana de Lazzari
 */

public class ImageDatabaseOpenHelper extends SQLiteOpenHelper {

    /**
     * Instance variables that represents the database information.
     */

    protected final static String DATABASE_NAME = "NasaImagesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "SAVED_IMAGES";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_URL = "URL";
    public final static String COL_DATE = "DATE";

    public ImageDatabaseOpenHelper(Context ctx) {

        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * The onCreate method creates the saved image table and columns (id, title, url, and date).
     *
     * @param db SQLiteDatabase object.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("+ COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_URL + " TEXT, "
                + COL_DATE + " TEXT);"
                );
    }

    /**
     * The onUpgrade method gets called if the database does exist on the device,
     * and the version in the constructor is newer than the version that exists on the device.
     *
     * @param db SQLiteDatabase object.
     *
     * @param oldVersion id of the old version.
     *
     * @param newVersion id of the new version.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    /**
     * The onDownGrade method gets called if the database does exist on the device,
     * and the version number in the constructor is lower than the version number that exists on the device.
     *
     * @param db SQLiteDatabase object.
     *
     * @param oldVersion id of the old version.
     *
     * @param newVersion id of the new version.
     */

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }
}
