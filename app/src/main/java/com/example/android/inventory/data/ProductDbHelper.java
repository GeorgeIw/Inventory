package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {

    //name of the database fle
    private static final String DATABASE_NAME = "inventory.db";
    //initial version of the database
    private static final int DATABASE_VERSION = 1;
    //public constructor of ProductDbHelper class
    public ProductDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create a String that contains the SQL statement to create the product table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + "TEXT NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "INTEGER NOT NULL);";
        //execute the SQL statement created above
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);

    }
    //method called when the database need to update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //in this part of the Inventory App there is no need for upgrading the databse
        //so this method does nothing for now
    }
}
