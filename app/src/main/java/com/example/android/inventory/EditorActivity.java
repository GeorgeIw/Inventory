package com.example.android.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

//this class is for adding a new product in the database
//or edit an existing one
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    //item loader
    private static final int ITEM_LOADER = 0;
    //content URI for the existing item-product
    private Uri itemCurrentUri;
    //EditText field for the product name
    private EditText nameEditText;
    //EditText field for the product price
    private EditText priceEditText;
    //EditText field for the product quantity
    private EditText quantityEditText;
    //EditText field for the product's supplier name
    private EditText supplierNameEditText;
    //EditText field for the phone number of the products supplier
    private EditText supplierPhoneNumberEditText;
    //flag to keep track weather the item has been modified
    private boolean pItemChanged = false;
    //minus button for quantity
    private ImageButton minusButton;
    //plus button for quantity
    private ImageButton plusButton;
    //Button field for call now ACTION
    private Button callNowButton;


    //onTouchListener that notifies when the view is altered
    private View.OnTouchListener pTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            pItemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //get the data of the intent for opening EditorActivty
        Intent intent = getIntent();
        itemCurrentUri = intent.getData();

        //if the intent does not create a new entry in products, we know that we are creating a new product entry
        if (itemCurrentUri == null) {
            //set the title of the appbar to : "Add Product"
            setTitle(getString(R.string.editor_add_product));
            //invalidate the options menu so the Delete option can be hidden
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_edit_product_header));
            TextView overviewTitle = findViewById(R.id.overview_title);
            overviewTitle.setText(R.string.edit_product);
            //initialize a loader to read the product data from the database
            getLoaderManager().initLoader(ITEM_LOADER, null, this);
        }


        //find the view with id:edit_product_name and store it to NameEditText variable for use
        nameEditText = findViewById(R.id.edit_product_name);
        //find the view with id:edit_product_price and store it to PriceEditText variable for use
        priceEditText = findViewById(R.id.edit_product_price);
        //find the Button with id:minus_button and store it to minusButton variable for use
        minusButton = findViewById(R.id.minus_button);
        //find the view with id:edit_product_quantity and store it to QuantityEditText variable for use
        quantityEditText = findViewById(R.id.edit_product_quantity);
        //find the Button with id:plus_button and store it to plusButton variable for use
        plusButton = findViewById(R.id.plus_button);
        //find the view with id:edit_product_supplier_name and store it to SupplierNameEditText variable for use
        supplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        //find the view with id:edit_product_supplier_phone_number and store it to SupplierPhoneNumberEditText for use
        supplierPhoneNumberEditText = findViewById(R.id.edit_product_supplier_phone_number);
        //find the view with id:call_now_button and store it to callNowButton variable for use
        callNowButton = findViewById(R.id.call_now_button);

        //set the TouchListener to determine if the field has been modified
        nameEditText.setOnTouchListener(pTouchListener);
        priceEditText.setOnTouchListener(pTouchListener);
        quantityEditText.setOnTouchListener(pTouchListener);
        supplierNameEditText.setOnTouchListener(pTouchListener);
        supplierPhoneNumberEditText.setOnTouchListener(pTouchListener);

        //set the ClickListener to minusButton
        minusButton.setOnClickListener(minusButtonClickListener);
        //set the clickListener to plusButton
        plusButton.setOnClickListener(plusButtonClickListener);
        //set the clickListener to callNowButon
        callNowButton.setOnClickListener(callNowButtonClickListener);
        //set the clickListener to uploadImageTextView

    }

    //OnClickListener method for minusButton
    private View.OnClickListener minusButtonClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int quantityEditTextField = Integer.parseInt(quantityEditText.getText().toString());
            quantityEditTextField = quantityEditTextField - 1;
            if (quantityEditTextField >= 0) {
                quantityEditText.setText(String.valueOf(quantityEditTextField));
            }
        }
    });

    //OnClickListener method for plusButton
    private View.OnClickListener plusButtonClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int quantityEditTextField = Integer.parseInt(quantityEditText.getText().toString());
            quantityEditTextField = quantityEditTextField + 1;
            quantityEditText.setText(String.valueOf(quantityEditTextField));
        }
    });

    //OnClickListener to open the dial on mobile with the stored phone number
    View.OnClickListener callNowButtonClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String dialPhoneNumber = supplierPhoneNumberEditText.getText().toString().trim();
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:" + dialPhoneNumber));
            startActivity(call);
        }
    });

    //method to set the action when the value of the field is null
    //if the value is null, do nothing
    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        } else {
            return Double.parseDouble(s);
        }
    }

    //method to set the action when the value of the field is null
    //if the value is null, do nothing
    private int parseInt(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    }

    //method to set the action when the value of the field is null
    //if the value is null, do nothing
    private long parseLong(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        } else {
            return Long.parseLong(s);
        }
    }

    //method to create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the options menu with the .xml file named menu_editor
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    //let the user insert a new product in the database
    private void insertProduct() {


        //get the input from the EditText fields
        //use toString to print the output
        //and trim to avoid any unneeded whitespace
        String productName = nameEditText.getText().toString().trim();
        //because the column price is stored as an INTEGER
        //first get the value as a text
        //and then reform it to INTEGER so that it can be added to the database
        String productPrice = priceEditText.getText().toString().trim();
        double price = parseDouble(productPrice);
        //same as before, get the TEXT value of the column and reform it to INTEGER value
        String productQuantity = quantityEditText.getText().toString().trim();
        int quantity = parseInt(productQuantity);
        String productSupplier = supplierNameEditText.getText().toString().trim();
        //same as before, get the TEXT value of the column and reform it to long value
        final String productSupplierPhoneNumber = supplierPhoneNumberEditText.getText().toString().trim();
        long supplierPhoneNumber = parseLong(productSupplierPhoneNumber);


        //check if this is a new product and if the fields of the editor are empty
        if (itemCurrentUri == null && TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productQuantity)
                || TextUtils.isEmpty(productSupplier) || TextUtils.isEmpty(productSupplierPhoneNumber)) {

            Toast.makeText(this, getString(R.string.do_nothing_when_field_isnull), Toast.LENGTH_LONG).show();
            return;
        }

        //create the database helper
        ProductDbHelper dbHelper = new ProductDbHelper(this);

        //get the database in write mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //create a ContentValues values object to get input data
        ContentValues values = new ContentValues();
        //make the columns as the keys in this object and the attributes as their values
        values.put(ProductContract.ProductEntry.COLUMN_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, price);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quantity);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        //check if this is a new product or an existing one
        if (itemCurrentUri == null) {
            //insert a new product returning the content Uri
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            //show a toast message if the insertion was successful
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_error_saving_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.new_product_added_to_database), Toast.LENGTH_SHORT).show();
            }
        } else {
            //else, this is an existing product so update the info for it
            int rowsAffected = getContentResolver().update(itemCurrentUri, values, null, null);
            //show a toast on weather the update was successful or not
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_failed_update), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_successful_update), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method to update the menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //if this is a new product, hide the "Delete" menu item
        if (itemCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_button);
            menuItem.setVisible(false);
        }

        return true;
    }

    //delete only if a product exists
    public void deleteProduct() {
        if (itemCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(itemCurrentUri, null, null);
            //show a toast message depending on weather the delete was successful
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        //close the activity
        finish();
    }

    private void deleteConfirmationMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void unsavedChangesMsg(
            //create an alertDialog
            DialogInterface.OnClickListener abortButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.abort_editing);
        builder.setPositiveButton(R.string.abort, abortButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        //create and show the alertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //do something when the user clicks on the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if the user clicks on save option,
            //save the product that was inputted, and close the activity
            case R.id.save_button:
                insertProduct();
                finish();
                return true;
            //this statement doesn't do anything for the time being,
            //but it should delete the entry if the user clicked in the delete option
            case R.id.delete_button:
                deleteConfirmationMsg();
                return true;
            //if the user clicks the home button
            //take the user to the home screen(main activity)
            case R.id.homeAsUp:

                if (!pItemChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener abortButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                unsavedChangesMsg(abortButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!pItemChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener abortButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        };

        unsavedChangesMsg(abortButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //define a projection with all the columns from the table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        //perform a query in the database table
        return new CursorLoader(this,
                itemCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // return early if there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }
        //go to the first row of the cursor and read data from it
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            //extract the value from the cursor
            String name = data.getString(nameColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            long supplierPhoneNumber = data.getLong(supplierPhoneNumberColumnIndex);

            //update the view with values from the new database
            nameEditText.setText(name);
            priceEditText.setText(Double.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneNumberEditText.setText(Long.toString(supplierPhoneNumber));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //clear the data if the loader is invalidated
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneNumberEditText.setText("");

    }

}
