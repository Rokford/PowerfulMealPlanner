package com.mobinautsoftware.powerfulmealplanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by plgrizw on 03.02.15.
 */
public class ShoppingListSwipeAdapter extends FragmentPagerAdapter
{
    private ArrayList<ShoppingItem> allShoppingItems;

    public ShoppingListSwipeAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        ShoppingItemSwipeFragment fragment = ShoppingItemSwipeFragment.newInstance(Utilities.shoppingListTabsItemsArray[i]);

        ArrayList<ShoppingItem> shoppingItemsForCategory = new ArrayList<ShoppingItem>();

        for (ShoppingItem item : allShoppingItems)
        {
            if (item.getCategory().equals(Utilities.shoppingListTabsItemsArray[i]))
            {
                shoppingItemsForCategory.add(item);
            }
        }

        fragment.setShoppingItems(shoppingItemsForCategory);

        return fragment;
    }

    @Override
    public int getCount()
    {
        return Utilities.shoppingListTabsItemsArray.length;
    }

    public ArrayList<ShoppingItem> getAllShoppingItems()
    {
        return allShoppingItems;
    }

    public void setAllShoppingItems(ArrayList<ShoppingItem> allShoppingItems)
    {
        this.allShoppingItems = allShoppingItems;
    }
}
