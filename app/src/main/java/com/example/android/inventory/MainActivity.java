package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //id of the loader
    private static final int LOADER_ID = 0;
    //set the adapter of the ListView
    ProductCursorAdapter pAdapter;
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

        //find the ListView with id:entries and store it to productEntries
        ListView productEntries = findViewById(R.id.entries);

        //find the view with id:empty_view and show it when no items exist in the list
        View emptyView = findViewById(R.id.empty_view);
        productEntries.setEmptyView(emptyView);

        //setup the adapter for the list
        pAdapter = new ProductCursorAdapter(this, null);
        productEntries.setAdapter(pAdapter);

        //setup the itemClickListener
        productEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create the intent to open the EditorActivity
                Intent editorIntent = new Intent(MainActivity.this, EditorActivity.class);

                //create the URI for a specific product
                Uri currentItemUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);

                //set the URI on the intent
                editorIntent.setData(currentItemUri);
                startActivity(editorIntent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    //method to insert data at the product table of the database
    //this is for debugging only, and the real data of the database will NOT be entered this way
    private void insertProduct() {
        //get the database so it can be altered
        //set the database to write mode which allows the developer to alter it
        //SQLiteDatabase database = productDbHelper.getWritableDatabase();
        //create a ContentValues object
        ContentValues values = new ContentValues();
        //set the columns as the keys and their respective values as the the products table values
        values.put(ProductContract.ProductEntry.COLUMN_NAME, "Hammer");
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, 7.39);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, 19);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, "Black&Decker");
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 197858632);
        //get the Uri to have access in data
        Uri itemUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
    }


    //public method that creates the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu with the .xml file named main_menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //method to delete all entries in the database
    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + "rows deleted from the inventory database");
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
                return true;
            //if Delete all entries is clicked
            case R.id.delete_all_entries:
                deleteAllEntries();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //define a projection of the database columns
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY};

        //start a query for the specific entries in the database's background
        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //update the cursor with a new cursor with updated data
        pAdapter.swapCursor(data);

    }


    @Override
    public void onLoaderReset(Loader loader) {

        //callback when the data needs to be deleted
        pAdapter.swapCursor(null);

    }

}
