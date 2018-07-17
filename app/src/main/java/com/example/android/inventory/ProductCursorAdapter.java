package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventory.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {

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

        //find the view with id:product_name and store it to productName variable
        TextView productName = view.findViewById(R.id.product_name);
        //find the view with id:product_price and store it to productPrice variable
        TextView productPrice = view.findViewById(R.id.product_price);
        //find the view with id:product_quantity and store it to productQuantity variable
        final TextView productQuantity = view.findViewById(R.id.product_quantity);
        //find the view with id:sale_button and store it to saleButton variable
        final Button saleButton = view.findViewById(R.id.sale_button);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ProductCursorAdapter adapter = new ProductCursorAdapter(context, null);
                //for (int i = adapter.getItemId(position) ; i++)
                //adapter.getItemId(position);
                int zeroStoppingPoint = 0;
                int columnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
                int quantityIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
                String column = cursor.getString(columnIndex);
                String quantity = cursor.getString(quantityIndex);

                    if (Integer.parseInt(quantity) > zeroStoppingPoint) {
                        MainActivity mainActivity = (MainActivity) context;
                        mainActivity.decreaseQuantityValueByOne(Integer.valueOf(column), Integer.valueOf(quantity));
                    }
            }

        });


        //find and store the column COLUMN_NAME of the database to productNameColumnIndex
        int productNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
        //find and store the column COLUMN_PRICE of the database to productPriceColumnIndex
        int productPriceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        //find and store the column COLUMN_QUANTITY of the database to productQuantityColumnIndex
        final int productQuantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);

        //read and get the data from the Cursor
        String productNameInv = cursor.getString(productNameColumnIndex);
        int productPriceInv = cursor.getInt(productPriceColumnIndex);
        final int productQuantityInv = cursor.getInt(productQuantityColumnIndex);

        //set and update the view with the data taken from the cursor
        productName.setText(productNameInv);
        productPrice.setText(Integer.toString(productPriceInv) + "$");
        productQuantity.setText(Integer.toString(productQuantityInv) + " pcs ");

        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
