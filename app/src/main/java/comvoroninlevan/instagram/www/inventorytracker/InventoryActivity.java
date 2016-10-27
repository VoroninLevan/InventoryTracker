package comvoroninlevan.instagram.www.inventorytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ArrayList<GridMenu> menu = new ArrayList<GridMenu>();

        String items = getText(R.string.items).toString();
        String suppliers = getText(R.string.suppliers).toString();
        String customers = getText(R.string.customers).toString();
        String activityHistory = getText(R.string.activityHistory).toString();
        String purchaseOrder = getText(R.string.purchaseOrder).toString();
        String saleOrder = getText(R.string.saleOrder).toString();

        menu.add(new GridMenu(R.drawable.item, items));
        menu.add(new GridMenu(R.drawable.history, activityHistory));
        menu.add(new GridMenu(R.drawable.supplier, suppliers));
        menu.add(new GridMenu(R.drawable.customer, customers));
        menu.add(new GridMenu(R.drawable.purchase, purchaseOrder));
        menu.add(new GridMenu(R.drawable.sell, saleOrder));

        GridAdapter menuAdapter = new GridAdapter(this, menu);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(menuAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Intent intentItem = new Intent(InventoryActivity.this, ItemsActivity.class);
                        startActivity(intentItem);
                        break;
                    case 1:
                        Intent intentHistory = new Intent(InventoryActivity.this, HistoryActivity.class);
                        startActivity(intentHistory);
                        break;
                    case 2:
                        Intent intentSuppliers = new Intent(InventoryActivity.this, SuppliersActivity.class);
                        startActivity(intentSuppliers);
                        break;
                    case 3:
                        Intent intentCustomers = new Intent(InventoryActivity.this, CustomerActivity.class);
                        startActivity(intentCustomers);
                        break;
                    case 4:
                        Intent intentPurchase = new Intent(InventoryActivity.this, PurchaseActivity.class);
                        startActivity(intentPurchase);
                        break;
                    case 5:
                        Intent intentSale = new Intent(InventoryActivity.this, SaleActivity.class);
                        startActivity(intentSale);
                        break;
                }
            }
        });
    }
}
