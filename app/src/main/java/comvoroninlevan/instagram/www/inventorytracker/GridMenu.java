package comvoroninlevan.instagram.www.inventorytracker;

/**
 * Created by Леван on 17.10.2016.
 */
public class GridMenu {

    private int mMenuIcon;
    private String mMenuName;

    public GridMenu(int menuIcon, String menuName){
        mMenuIcon = menuIcon;
        mMenuName = menuName;
    }

    public int getMenuIcon(){
        return mMenuIcon;
    }
    public String getMenuName(){
        return mMenuName;
    }
}
