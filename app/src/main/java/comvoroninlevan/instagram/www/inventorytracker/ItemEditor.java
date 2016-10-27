package comvoroninlevan.instagram.www.inventorytracker;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import comvoroninlevan.instagram.www.inventorytracker.data.Contract;

/**
 * Created by Леван on 17.10.2016.
 */
public class ItemEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER = 0;

    private Uri mItemUri;
    private EditText mName;
    private EditText mQuantity;
    private EditText mPrice;
    private EditText mDescription;
    private ImageView mPhoto;
    private byte[] mBytePhoto;

    private boolean mItemChanged = false;

    private View.OnTouchListener mTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);

        Intent intent = getIntent();
        mItemUri = intent.getData();

        if(mItemUri == null){
            setTitle(R.string.addItemHeader);
        }else{
            setTitle(R.string.editItemHeader);
            getLoaderManager().initLoader(LOADER, null, this);
        }

        mName = (EditText)findViewById(R.id.name);
        mQuantity = (EditText)findViewById(R.id.quantity);
        mPrice = (EditText)findViewById(R.id.price);
        mDescription = (EditText)findViewById(R.id.description);
        mPhoto = (ImageView)findViewById(R.id.photo);

        mName.setOnTouchListener(mTouch);
        mQuantity.setOnTouchListener(mTouch);
        mPrice.setOnTouchListener(mTouch);
        mDescription.setOnTouchListener(mTouch);
        mPhoto.setOnTouchListener(mTouch);
    }

    private void itemSave(){
        String nameString = mName.getText().toString().trim();
        String quantityString = mQuantity.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String descriptionString = mDescription.getText().toString().trim();



        if(mItemUri == null && (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(priceString))){
            return;
        }


        ContentValues values = new ContentValues();
        values.put(Contract.ItemEntry.COLUMN_NAME, nameString);
        values.put(Contract.ItemEntry.COLUMN_QUANTITY, quantityString);
        values.put(Contract.ItemEntry.COLUMN_PRICE, priceString);
        values.put(Contract.ItemEntry.COLUMN_DESCRIPTION, descriptionString);
        if(mBytePhoto != null) {
            values.put(Contract.ItemEntry.COLUMN_PHOTO, mBytePhoto);
        }



        if(mItemUri == null){
            Uri newUri = getContentResolver().insert(Contract.ItemEntry.CONTENT_URI, values);

            if(newUri == null){
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rows = getContentResolver().update(mItemUri, values, null, null);

            if(rows == 0){
                Toast.makeText(this, "Error with updating item", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
            }
        }
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

        if(data == null){
            return;
        }

        if(data.moveToFirst()){


            int columnName = data.getColumnIndex(Contract.ItemEntry.COLUMN_NAME);
            int columnQuantity = data.getColumnIndex(Contract.ItemEntry.COLUMN_QUANTITY);
            int columnPrice = data.getColumnIndex(Contract.ItemEntry.COLUMN_PRICE);
            int columnDescription = data.getColumnIndex(Contract.ItemEntry.COLUMN_DESCRIPTION);
            int columnPhoto = data.getColumnIndex(Contract.ItemEntry.COLUMN_PHOTO);

            String itemName = data.getString(columnName);
            int itemQuantity = data.getInt(columnQuantity);
            int itemPrice = data.getInt(columnPrice);
            String itemDescription = data.getString(columnDescription);
            byte[] itemImage = data.getBlob(columnPhoto);
            Bitmap image;
            if(itemImage != null) {
                image = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
                mPhoto.setImageBitmap(image);
            }

            mName.setText(itemName);
            mQuantity.setText(Integer.toString(itemQuantity));
            mPrice.setText(Integer.toString(itemPrice));
            mDescription.setText(itemDescription);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mName.setText("");
        mQuantity.setText("");
        mPrice.setText("");
        mDescription.setText("");
        mPhoto.setImageBitmap(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_item:
                itemSave();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri loadedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), loadedImage);

                mPhoto.setImageBitmap(bitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                mBytePhoto = outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ItemEditor.this, "Can not upload an image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
