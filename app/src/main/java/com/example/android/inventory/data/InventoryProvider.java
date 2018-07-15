package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InventoryProvider extends ContentProvider {
    //Uri matcher code for the Uri of the products table
    private static final int PRODUCTS = 0;
    //Uri matcher code for the Uri of a specific product of the table
    private static final int PRODUCTS_ID = 1;

    public static final UriMatcher inventoryUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private ProductDbHelper pDbHelper;
    //static initializer, this is run the first time anything is called from this class
    static {
        //provide access to multiple rows
        inventoryUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PRODUCTS_PATH,PRODUCTS);
        //create a Uri that replaces the "#" symbol with a number
        inventoryUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PRODUCTS_PATH + "/#",PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        pDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //get the readable database
        SQLiteDatabase database = pDbHelper.getReadableDatabase();
        //hold the result of the query
        Cursor cursor;
        //determine if the Uri matcher can match the Uri to a specific code
        int match = inventoryUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default :
                throw new IllegalArgumentException("This URI" + uri + "is unknown, and thus failed to query it");
        }
        //show the notification for the Uri on the Cursor to show what the Uri was created for
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = inventoryUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri + "with match " +match);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values){
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("This product requires a name!");
        }

        Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
        if (price == null || price < 0){
            throw new IllegalArgumentException("This product requires a valid price");
        }

        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
        if (quantity == null){
            throw new IllegalArgumentException("This product requires a quantity value");
        }

        String supplierName = values.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null){
            throw new IllegalArgumentException("This product requires a Supplier Name");
        }

        Integer supplierNumber = values.getAsInteger(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierNumber == null){
            throw new IllegalArgumentException("This product requires a Phone Number of it's Supplier");
        }

        SQLiteDatabase database = pDbHelper.getWritableDatabase();

        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);
        if (id == -1){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = inventoryUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return insertProduct(uri,values);
                default:
                    throw new IllegalArgumentException("Pick a different insertion method for" +uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //get writable database
        SQLiteDatabase database = pDbHelper.getWritableDatabase();
        //variable for the rows that were deleted
        int deletedRows;

        final int match = inventoryUriMatcher.match(uri);

        switch (match){
            case PRODUCTS:
                deletedRows = database.delete(ProductContract.ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(ProductContract.ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete " +uri);
        }
        //if 1 or more rows were deleted, notify the listeners that the data was changed
        if (deletedRows != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deletedRows;
    }

    //update the product with the given content values
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        //check that the name value is not null
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_NAME);

            if (name == null){
                throw new IllegalArgumentException("This product MUST have a name");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRICE)){
            Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);

            if (price == null || price < 0){
                throw new IllegalArgumentException("This product MUST have a price");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_QUANTITY)){
            Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);

            if (quantity == null){
                throw new IllegalArgumentException("This product MUST have a quantity value");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)){
            String supplierName = values.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);

            if (supplierName == null){
                throw new IllegalArgumentException("This product MUST have a supplier name");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)){
            Integer supplierNumber = values.getAsInteger(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            if (supplierNumber == null){
                throw new IllegalArgumentException("This product MUST have a supplier number");
            }
        }

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = pDbHelper.getWritableDatabase();

        int affectedRows = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (affectedRows != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return affectedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = inventoryUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductContract.ProductEntry._ID + "/?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update " + uri);
        }
    }
}
