package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductCursorAdapter extends CursorAdapter {

    @BindView(R.id.product_name) TextView productName;
    @BindView(R.id.product_price) TextView productPrice;
    @BindView(R.id.product_quantity) TextView productQuantity;
    @BindView(R.id.sale_button) Button saleButton;

    //constructor of the class
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    //make a new empty view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.home_screen_list, parent, false);
    }

    //bind the data to the appropriate view
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        ButterKnife.bind(this,view);

        int productIdColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        //find and store the column COLUMN_NAME of the database to productNameColumnIndex
        int productNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
        //find and store the column COLUMN_PRICE of the database to productPriceColumnIndex
        int productPriceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        //find and store the column COLUMN_QUANTITY of the database to productQuantityColumnIndex
        final int productQuantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);


        //read and get the data from the Cursor
        final int productIdInv = cursor.getInt(productIdColumnIndex);
        String productNameInv = cursor.getString(productNameColumnIndex);
        double productPriceInv = cursor.getDouble(productPriceColumnIndex);
        final int productQuantityInv = cursor.getInt(productQuantityColumnIndex);


        //set and update the view with the data taken from the cursor
        productName.setText(productNameInv);
        productPrice.setText(Double.toString(productPriceInv) + "$");
        productQuantity.setText(Integer.toString(productQuantityInv) + " pcs ");


        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //how much the value of quantity will decrease in every button click
                int quantity = productQuantityInv - 1;
                ContentValues values = new ContentValues();
                //put the new value of quantity into the database
                values.put(ProductContract.ProductEntry.COLUMN_QUANTITY,quantity);
                String selection = ProductContract.ProductEntry._ID + "=?";
                //update the product uri for the specific id
                Uri updateUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,productIdInv);
                //decrease the quantity value only when it's bigger than 0
                if(quantity >= 0) {
                    context.getContentResolver().update(updateUri, values, selection, null);
                }
            }

        });

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
