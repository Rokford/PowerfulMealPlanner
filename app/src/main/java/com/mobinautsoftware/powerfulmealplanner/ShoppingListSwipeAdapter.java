package com.mobinautsoftware.powerfulmealplanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by plgrizw on 03.02.15.
 */
public class ShoppingListSwipeAdapter extends FragmentPagerAdapter
{
    public ShoppingListSwipeAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        ShoppingItemSwipeFragment fragment = ShoppingItemSwipeFragment.newInstance(Utilities.shoppingListTabsItemsArray[i]);

        return fragment;
    }

    @Override
    public int getCount()
    {
        return Utilities.shoppingListTabsItemsArray.length;
    }
}
