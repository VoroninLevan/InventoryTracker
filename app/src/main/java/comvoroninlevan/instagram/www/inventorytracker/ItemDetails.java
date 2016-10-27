package comvoroninlevan.instagram.www.inventorytracker;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import comvoroninlevan.instagram.www.inventorytracker.data.Contract;

/**
 * Created by Леван on 19.10.2016.
 */
public class ItemDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER = 0;

    private Uri mItemUri;
    private TextView mName;
    private TextView mDescription;
    private TextView mPrice;
    private TextView mQuantity;
    private ImageView mPhoto;
    int itemQuantity;
    EditText quantityChooser;
    long id;
    int quantity;
    String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detailed);

        Intent intent = getIntent();
        mItemUri = intent.getData();

        getLoaderManager().initLoader(LOADER, null, this);

        mName = (TextView)findViewById(R.id.item_detailed_name);
        mPhoto = (ImageView)findViewById(R.id.item_detailed_image);
        mDescription = (TextView)findViewById(R.id.item_detailed_description);
        mPrice = (TextView)findViewById(R.id.item_detailed_price);
        mQuantity = (TextView)findViewById(R.id.item_detailed_quantity);

        quantityChooser = (EditText)findViewById(R.id.quantityChooser);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {Contract.ItemEntry._ID,
                Contract.ItemEntry.COLUMN_NAME,
                Contract.ItemEntry.COLUMN_QUANTITY,
                Contract.ItemEntry.COLUMN_PRICE,
                Contract.ItemEntry.COLUMN_DESCRIPTION,
                Contract.ItemEntry.COLUMN_PHOTO};

        return new CursorLoader(this, mItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if(data.moveToFirst()) {

            //double check blob and bitmap

            int columnName = data.getColumnIndex(Contract.ItemEntry.COLUMN_NAME);
            int columnQuantity = data.getColumnIndex(Contract.ItemEntry.COLUMN_QUANTITY);
            int columnPrice = data.getColumnIndex(Contract.ItemEntry.COLUMN_PRICE);
            int columnDescription = data.getColumnIndex(Contract.ItemEntry.COLUMN_DESCRIPTION);
            int columnPhoto = data.getColumnIndex(Contract.ItemEntry.COLUMN_PHOTO);

            itemName = data.getString(columnName);
            itemQuantity = data.getInt(columnQuantity);
            int itemPrice = data.getInt(columnPrice);
            String itemDescription = data.getString(columnDescription);
            byte[] itemImage = data.getBlob(columnPhoto);
            Bitmap image;
            if(itemImage != null) {
                image = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
                mPhoto.setImageBitmap(image);
            }


            mName.setText(itemName);
            //
            mDescription.setText(itemDescription);
            mQuantity.setText(Integer.toString(itemQuantity));
            mPrice.setText(Integer.toString(itemPrice));

            int itemId = data.getColumnIndex(Contract.ItemEntry._ID);
            id = data.getLong(itemId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
/*
        mName.setText("");
        mQuantity.setText("");
        mPrice.setText("");
        mDescription.setText("");
        mPhoto.setImageBitmap(null);
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_item:
                Intent intent = new Intent(ItemDetails.this, ItemEditor.class);
                Uri currentUri = mItemUri;
                intent.setData(currentUri);
                startActivity(intent);
                return true;
            case R.id.delete_item:
                deleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteConfirmationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogQuestion);
        builder.setPositiveButton(R.string.dialogDelete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(){

        getContentResolver().delete(mItemUri, null, null);

        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();

        finish();
    }

    public void sellItem(View view){

        if(quantityChooser.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Please set quantity to sell.", Toast.LENGTH_SHORT).show();
        }else {
            quantity = Integer.parseInt(quantityChooser.getText().toString());

            int newQuantity = itemQuantity - quantity;
            if (newQuantity <= 5 & newQuantity > 0) {
                Toast.makeText(this, "Low item quantity: " + newQuantity + " left.", Toast.LENGTH_SHORT).show();
            }
            if (newQuantity < 0) {
                newQuantity = 0;
                Toast.makeText(this, "No more items", Toast.LENGTH_SHORT).show();
            }

            ContentValues values = new ContentValues();
            values.put(Contract.ItemEntry.COLUMN_QUANTITY, newQuantity);

            Uri currentUri = ContentUris.withAppendedId(Contract.ItemEntry.CONTENT_URI, id);
            getContentResolver().update(currentUri, values, null, null);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, itemName + " was send");
            intent.putExtra(Intent.EXTRA_TEXT, "Dear sir/madam,\n" + quantity + " of " + itemName +
                    " was send to your address");

            startActivity(Intent.createChooser(intent, "Sell " + quantity + " of " + itemName));
        }
    }

    public void orderItem(View view){

        if(quantityChooser.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please set quantity to order.", Toast.LENGTH_SHORT).show();
        }else {
            quantity = Integer.parseInt(quantityChooser.getText().toString());

            int newQuantity = itemQuantity + quantity;

            ContentValues values = new ContentValues();
            values.put(Contract.ItemEntry.COLUMN_QUANTITY, newQuantity);

            Uri currentUri = ContentUris.withAppendedId(Contract.ItemEntry.CONTENT_URI, id);
            getContentResolver().update(currentUri, values, null, null);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "New order for " + itemName);
            intent.putExtra(Intent.EXTRA_TEXT, "Dear sir/madam,\n" +
                    "I would like to order " + quantity + " of " + itemName);

            startActivity(Intent.createChooser(intent, "Order " + quantity + " of " + itemName));
        }
    }
}
