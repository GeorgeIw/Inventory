package com.example.android.inventory.data;

import android.provider.BaseColumns;

public final class ProductContract {

    //empty constructor
    private ProductContract(){}

    //inner class with constants for the products database table
    public static final class ProductEntry implements BaseColumns {
        //constant value for the Product table
        public final static String TABLE_NAME = "product";
        //constant value for the id of each entry in the database
        public final static String _ID = BaseColumns._ID;
        //constant value for the "name" column in database
        public final static String COLUMN_NAME = "name";
        //constant value for the "price" column in database
        public final static String COLUMN_PRICE = "price";
        //constant value for the "quantity" column in database
        public final static String COLUMN_QUANTITY = "quantity";
        //constant value for the "supplier name" in database
        public final static String COLUMN_SUPPLIER_NAME = "supplier name";
        //constant value for the "supplier phone number" in database
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "phone number";
    }
}
