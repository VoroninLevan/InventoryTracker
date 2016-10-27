package comvoroninlevan.instagram.www.inventorytracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.sql.Blob;

import comvoroninlevan.instagram.www.inventorytracker.ItemEditor;

/**
 * Created by Леван on 18.10.2016.
 */
public class Provider extends ContentProvider{

    public static final String LOG_TAG = Provider.class.getSimpleName();
    private DbHelper mDbHelper;

    private static final int INVENTORY = 1;
    private static final int INVENTORY_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH, INVENTORY);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH + "/#", INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                cursor = database.query(Contract.ItemEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = Contract.ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Contract.ItemEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return Contract.ItemEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return Contract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values){

        //Checks for inputs
        String name = values.getAsString(Contract.ItemEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("Item requires a name");
        }

        Integer quantity = values.getAsInteger(Contract.ItemEntry.COLUMN_QUANTITY);
        if(quantity == null){
            throw new IllegalArgumentException("Item requires valid quantity");
        }

        Integer price = values.getAsInteger(Contract.ItemEntry.COLUMN_PRICE);
        if(price == null && price < 0){
            throw new IllegalArgumentException("Item requires valid price");
        }

        //byte[] photo = values.getAsByteArray(Contract.ItemEntry.COLUMN_PHOTO);
        //if(photo == null){
        //    throw new IllegalArgumentException("Item requires valid photo");
        //}

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(Contract.ItemEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int deletedRows;
        switch (match){
            case INVENTORY:
                deletedRows = database.delete(Contract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = Contract.ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(Contract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return updateItem(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = Contract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if(values.containsKey(Contract.ItemEntry.COLUMN_NAME)){
            String name = values.getAsString(Contract.ItemEntry.COLUMN_NAME);
            if(name == null){
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if(values.containsKey(Contract.ItemEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(Contract.ItemEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        if(values.containsKey(Contract.ItemEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(Contract.ItemEntry.COLUMN_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }

        if(values.containsKey(Contract.ItemEntry.COLUMN_PHOTO)){
            byte[] photo = values.getAsByteArray(Contract.ItemEntry.COLUMN_PHOTO);
            if(photo == null){
                throw new IllegalArgumentException("Item requires valid photo");
            }
        }

        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updatedRows = database.update(Contract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        if(updatedRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

}
