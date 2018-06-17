package com.example.android.inventory;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;
//this class is for adding a new product in the database
//or edit an existing one
public class EditorActivity extends AppCompatActivity {
    //EditText field for the product name
    private EditText NameEditText;
    //EditText field for the product price
    private EditText PriceEditText;
    //EditText field for the product quantity
    private EditText QuantityEditText;
    //EditText field for the product's supplier name
    private EditText SupplierNameEditText;
    //EditText field for the phone number of the products supplier
    private EditText SupplierPhoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //find the view with id:edit_product_name and store it to NameEditText variable
        NameEditText = findViewById(R.id.edit_product_name);
        //find the view with id:edit_product_price and store it to PriceEditText variable
        PriceEditText = findViewById(R.id.edit_product_price);
        //find the view with id:edit_product_quantity and store it to QuantityEditText variable
        QuantityEditText = findViewById(R.id.edit_product_quantity);
        //find the view with id:edit_product_supplier_name and store it to SupplierNameEditText variable
        SupplierNameEditText = findViewById(R.id.edit_product_supplier_name);
        //find the view with id:edit_product_supplier_phone_number and store it to SupplierPhoneNumberEditText
        SupplierPhoneNumberEditText = findViewById(R.id.edit_product_supplier_phone_number);
    }
    //method to create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the options menu with the .xml file named menu_editor
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }
    //let the user insert a new product in the database
    private void insertProduct(){
        //get the input from the EditText fields
        //use toString to print the output
        //and trim to avoid any unneeded whitespace
        String productName = NameEditText.getText().toString().trim();
        //because the column price is stored as an INTEGER
        //first get the value as a text
        //and then reform it to INTEGER so that it can be added to the database
        String productPrice = PriceEditText.getText().toString().trim();
        double price = Double.parseDouble(productPrice);
        //same as before, get the TEXT value of the column and reform it to INTEGER value
        String productQuantity = QuantityEditText.getText().toString().trim();
        int quantity = Integer.parseInt(productQuantity);
        String productSupplier = SupplierNameEditText.getText().toString().trim();
        //same as before, get the TEXT value of the column and reform it to INTEGER value
        String productSupplierPhoneNumber = SupplierPhoneNumberEditText.getText().toString().trim();
        long supplierPhoneNumber = Long.parseLong(productSupplierPhoneNumber);
        //create the database helper
        ProductDbHelper dbHelper = new ProductDbHelper(this);
        //get the database in write mode
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //create a ContentValues values object to get input data
        ContentValues values = new ContentValues();
        //make the columns as the keys in this object and the attributes as their values
        values.put(ProductContract.ProductEntry.COLUMN_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE,price);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY,quantity);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,productSupplier);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER,supplierPhoneNumber);
        //create a new row for the user input of a product
        long newRow = database.insert(ProductContract.ProductEntry.TABLE_NAME,null,values);
        //notify the user if their adding effort was successful
        if(newRow == -1) {
            Toast.makeText(this,"Error saving product",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "New row id of product : " + newRow, Toast.LENGTH_SHORT).show();
        }
    }
    //do something when the user clicks on the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //if the user clicks on save option,
            //save the product that was inputed, and close the activity
            case R.id.save_button:
                insertProduct();
                finish();
                return true;
                //this statement doesn't do anything for the time being,
            //but it should delete the entry if the user clicked in the delete option
            case R.id.delete_button:
                return true;
                //if the user clicks the home button
            //take the user to the home screen(main activity)
            case R.id.homeAsUp:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
