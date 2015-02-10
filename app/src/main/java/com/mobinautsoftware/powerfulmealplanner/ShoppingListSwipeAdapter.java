package com.mobinautsoftware.powerfulmealplanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by plgrizw on 03.02.15.
 */
public class ShoppingListSwipeAdapter extends FragmentPagerAdapter
{
    private ArrayList<ShoppingItem> allShoppingItems;

    private HashMap<Integer, ShoppingItemSwipeFragment> mapWithFragments;

    public ShoppingListSwipeAdapter(FragmentManager fm)
    {
        super(fm);

        mapWithFragments = new HashMap<Integer, ShoppingItemSwipeFragment>();
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

        mapWithFragments.put(i, fragment);

        return fragment;
    }

    @Override
    public int getCount()
    {
        return Utilities.shoppingListTabsItemsArray.length;
    }

    public void setAllShoppingItems(ArrayList<ShoppingItem> allShoppingItems)
    {
        this.allShoppingItems = allShoppingItems;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    public HashMap<Integer, ShoppingItemSwipeFragment> getMapWithFragments()
    {
        return mapWithFragments;
    }

}
