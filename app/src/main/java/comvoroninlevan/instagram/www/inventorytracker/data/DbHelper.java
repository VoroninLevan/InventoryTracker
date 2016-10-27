package comvoroninlevan.instagram.www.inventorytracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Леван on 18.10.2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String TEXT_TYPE = " TEXT";
    public static final String BLOB_TYPE = " BLOB";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";
    public static final String PRIM_KEY = " PRIMARY KEY";
    public static final String AUTOINCR = " AUTOINCREMENT";
    public static final String NOT_NULL = " NOT NULL";
    public static final String DEFAULT = " DEFAULT ";
    public static final String CREATE_TABLE = "CREATE TABLE ";

    public static final String CREATE_SQL = CREATE_TABLE + Contract.ItemEntry.TABLE_NAME + "(" +
            Contract.ItemEntry._ID + INTEGER_TYPE + PRIM_KEY + AUTOINCR + COMMA_SEP +
            Contract.ItemEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
            Contract.ItemEntry.COLUMN_QUANTITY + INTEGER_TYPE + COMMA_SEP +
            Contract.ItemEntry.COLUMN_PRICE + INTEGER_TYPE + COMMA_SEP +
            Contract.ItemEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            Contract.ItemEntry.COLUMN_PHOTO + BLOB_TYPE + ");";

    public static final int DB_VER = 1;
    public static final String DB_NAME = "Inventory.db";

    public DbHelper(Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
