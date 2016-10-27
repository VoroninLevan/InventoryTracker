package comvoroninlevan.instagram.www.inventorytracker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Леван on 17.10.2016.
 */
public class GridAdapter extends ArrayAdapter<GridMenu> {

    public GridAdapter(Activity context, ArrayList<GridMenu> gridMenu){
        super(context, 0, gridMenu);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.menu_item, parent, false);
        }

        GridMenu currentMenu = getItem(position);

        ImageView menuIcon = (ImageView) listItemView.findViewById(R.id.menu_icon);
        menuIcon.setImageResource(currentMenu.getMenuIcon());

        TextView menuName = (TextView) listItemView.findViewById(R.id.menu_name);
        menuName.setText(currentMenu.getMenuName());

        return listItemView;
    }
}
