package comvoroninlevan.instagram.www.inventorytracker;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import comvoroninlevan.instagram.www.inventorytracker.data.Contract;
import comvoroninlevan.instagram.www.inventorytracker.data.DbHelper;
import comvoroninlevan.instagram.www.inventorytracker.data.Provider;

/**
 * Created by Леван on 19.10.2016.
 */
public class ItemCursorAdapter extends CursorAdapter{

    public ItemCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);


    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        int itemId = cursor.getColumnIndex(Contract.ItemEntry._ID);
        final long id = cursor.getLong(itemId);

        TextView name = (TextView)view.findViewById(R.id.name);
        TextView quantity = (TextView)view.findViewById(R.id.quantity);
        TextView price = (TextView)view.findViewById(R.id.price);
        ImageView photo = (ImageView)view.findViewById(R.id.photo);

        int columnName = cursor.getColumnIndex(Contract.ItemEntry.COLUMN_NAME);
        int columnQuantity = cursor.getColumnIndex(Contract.ItemEntry.COLUMN_QUANTITY);
        int columnPrice = cursor.getColumnIndex(Contract.ItemEntry.COLUMN_PRICE);
        int columnPhoto = cursor.getColumnIndex(Contract.ItemEntry.COLUMN_PHOTO);

        String itemName = cursor.getString(columnName);
        String itemQuantity = cursor.getString(columnQuantity);
        String itemPrice = cursor.getString(columnPrice);
        final byte[] itemImage = cursor.getBlob(columnPhoto);
        Bitmap image;
        if(itemImage != null) {
            image = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
            photo.setImageBitmap(image);
        }

        name.setText(itemName);
        quantity.setText(itemQuantity);
        price.setText(itemPrice);


        Button sellButton = (Button)view.findViewById(R.id.sell_button);
        final int itemIntQuantity = cursor.getInt(columnQuantity);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newQuantity = itemIntQuantity - 1;
                if(newQuantity <= 5 & newQuantity > 0){
                    Toast.makeText(context, "Low item quantity: " + newQuantity + " left.", Toast.LENGTH_SHORT).show();
                }
                if(newQuantity < 0){
                    newQuantity = 0;
                    Toast.makeText(context, "No more items", Toast.LENGTH_SHORT).show();
                }

                ContentValues values = new ContentValues();
                values.put(Contract.ItemEntry.COLUMN_QUANTITY, newQuantity);

                Uri currentUri = ContentUris.withAppendedId(Contract.ItemEntry.CONTENT_URI, id);
                context.getContentResolver().update(currentUri, values, null, null);
            }
        });
    }

}
