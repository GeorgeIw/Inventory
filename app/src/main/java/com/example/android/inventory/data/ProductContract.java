package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    //empty constructor
    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PRODUCTS_PATH = "products";



    //inner class with constants for the products database table
    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PRODUCTS_PATH);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY  + "/" + PRODUCTS_PATH;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCTS_PATH;

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
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        //constant value for the "supplier phone number" in database
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
