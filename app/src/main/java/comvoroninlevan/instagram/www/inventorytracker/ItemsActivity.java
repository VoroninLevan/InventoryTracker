package comvoroninlevan.instagram.www.inventorytracker;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import comvoroninlevan.instagram.www.inventorytracker.data.Contract;

/**
 * Created by Леван on 17.10.2016.
 */
public class ItemsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;

    ItemCursorAdapter mItemCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        ListView listView = (ListView)findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mItemCursorAdapter = new ItemCursorAdapter(this, null);
        listView.setAdapter(mItemCursorAdapter);

        getLoaderManager().initLoader(LOADER, null, this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ItemsActivity.this, ItemDetails.class);

                Uri currentUri = ContentUris.withAppendedId(Contract.ItemEntry.CONTENT_URI, id);

                intent.setData(currentUri);

                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {Contract.ItemEntry._ID,
                Contract.ItemEntry.COLUMN_NAME,
                Contract.ItemEntry.COLUMN_QUANTITY,
                Contract.ItemEntry.COLUMN_PRICE,
                Contract.ItemEntry.COLUMN_PHOTO};

        return new CursorLoader(this, Contract.ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mItemCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mItemCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_item:
                Intent intent = new Intent(ItemsActivity.this, ItemEditor.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
