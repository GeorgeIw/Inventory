package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity {
    //value for accessing ProductDbHelper class
    private ProductDbHelper productDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting up the Floating Action Button and it's functionality
        //store the view with id:floating_button to "floatingButton" variable
        FloatingActionButton floatingButton = findViewById(R.id.floating_button);
        //provide an OnClickListener to the Floating Action Button to mark it's functionality
        floatingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });
        //make an istance of ProductDbHelper class with name productDbHelper
        //and pass the current activity as it's context
        productDbHelper = new ProductDbHelper(this);
    }

    //helper method to provide information about the database in the TextView of the Home screen
    private void displayDatabaseInfo() {
        //CREATE or .open the database to read it's contents
        SQLiteDatabase database = productDbHelper.getReadableDatabase();
        //String for the columns that need to be used from a table of the Database
        String[] dbResults = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        // query the products table
        Cursor cursor = database.query(
                ProductContract.ProductEntry.TABLE_NAME,
                dbResults,
                null,
                null,
                null,
                null,
                null);
        //find the view with id:entries and store it to showComponents variable
        //to use it for displaying the results of the query
        TextView showComponents = findViewById(R.id.entries);

        try {
            //set the text at showComponents variable (which use the TextView with id:entries)
            //this will be the header of the table that will be shown after the query
            showComponents.setText("The table has : " + cursor.getCount() + " products.\n\n");
            //decide the order of the displaying information from the products table in inventory.db
            showComponents.append(ProductContract.ProductEntry._ID + " - " +
                    ProductContract.ProductEntry.COLUMN_NAME + " - " +
                    ProductContract.ProductEntry.COLUMN_PRICE + " - " +
                    ProductContract.ProductEntry.COLUMN_QUANTITY + " - " +
                    ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");
            //get the true index value of each column in products table
            int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            //iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                //use the index value of the columns
                //show the value at each index
                int currentId = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuality = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierPhoneNumber = cursor.getInt(supplierPhoneNumberColumnIndex);
                //display the values extracted from the table using the columns index value
                //display the values with this specific order
                showComponents.append(("\n" + currentId + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuality + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber));

            }
        } finally {
            //close cursor when not needed
            cursor.close();
        }

    }

    //method to insert data at the product table of the database
    //this is for debugging only, and the real data of the database will NOT be entered this way
    private void insertProduct() {
        //get the database so it can be altered
        //set the database to write mode which allows the developer to alter it
        SQLiteDatabase database = productDbHelper.getWritableDatabase();
        //create a ContentValues object
        ContentValues values = new ContentValues();
        //set the columns as the keys and their respective values as the the products table values
        values.put(ProductContract.ProductEntry.COLUMN_NAME, "Hammer");
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, 7.39);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, 19);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, "Black&Decker");
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 197858632);
        //print the values created with the "values" object to the screen
        long newRow = database.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    //public method that creates the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //infalte the menu with the .xml file named main_menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //what happens when the user clicks on the menu options
        switch (item.getItemId()) {
            //if Insert Dummy Data is clicked
            //let the user to insert some of his/her data
            //and display the results on screen
            case R.id.insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
                //if Delete all entries is clicked
            //this does nothing for now, but should be able to delete all the entries of the database - at some point -
            case R.id.delete_all_entries:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
