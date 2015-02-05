package com.mobinautsoftware.powerfulmealplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShoppingListActivity extends ActionBarActivity implements ShoppingItemSwipeFragment.OnFragmentInteractionListener
{

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private ListView drawerList;

    private ListView shoppingListView;

    private ViewPager viewPager;

    private ShoppingListAdapter adapter;

    private boolean shoppingModeOn;

    SparseBooleanArray selected;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new NavigationDrawerAdapter(this));

        getSupportActionBar().setTitle(getResources().getString(R.string.shopping_list));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.yes, R.string.no)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                String value = (String) adapter.getItemAtPosition(position);
                if (value == getResources().getString(R.string.recipies_list))
                {
                    Intent intent = new Intent(ShoppingListActivity.this, RecipeListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.shopping_list))
                {
                    Intent intent = new Intent(ShoppingListActivity.this, ShoppingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.callendar))
                {
                    Intent intent = new Intent(ShoppingListActivity.this, CalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.help))
                {
                    Intent intent = new Intent(ShoppingListActivity.this, HelpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);

        shoppingListView = (ListView) findViewById(R.id.shoppingListView);
        shoppingListView.setAdapter(adapter);
        shoppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter2, View v, int position, long arg3)
            {
                //String value = (String) adapter.getItem(position);
//                ShoppingListAdapter shoppingAdapter = (ShoppingListAdapter) adapter2.getAdapter();
//                ShoppingItem item = (ShoppingItem) shoppingAdapter.getItem(position);

                ShoppingItem item = (ShoppingItem) adapter.getItem(position);
                long id = item.getId();
                if (!shoppingModeOn)
                {
                    Intent intent = new Intent(ShoppingListActivity.this, AddShoppingItemActivity.class);
                    intent.putExtra("id", (int) id);
                    startActivity(intent);
                }
                else
                {
                    DatabaseManager manager = new DatabaseManager(ShoppingListActivity.this);
                    int idForDatabase = (int) id;
                    manager.open();
                    manager.updateCheckedMode_byID(idForDatabase);
                    // ShoppingItem item = manager.getShoppingItemById(id);
                    // manager.deleteShoppingItemById(id);
                    // manager.createShoppingItemChecked(item.getItem(), item.getQuantity(), item.getUnit());

                    ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItemsWithId();
                    manager.close();

                    adapter.setShoppingItemsList(shoppingItemsList);

                    adapter.notifyDataSetChanged();
                }
            }
        });

        shoppingListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        shoppingListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                final int checkedCount = shoppingListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                //                selected = new SparseBooleanArray();
                //                if (!selected.get(position))
                //                    selected.put(position, !selected.get(position));
                //                else
                //                    selected.delete(position);
                adapter.notifyDataSetChanged();
                //selectView(position, !selected.get(position), selected);
                //adapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.menu_delete:
                        deleteSelectedItems();
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                onResume();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        shoppingModeOn = true;
    }

    public void toggleSelection(int position, SparseBooleanArray mSelectedItemsIds)
    {
        selectView(position, !mSelectedItemsIds.get(position), mSelectedItemsIds);
    }

    public void selectView(int position, boolean value, SparseBooleanArray mSelectedItemsIds)
    {
        if (value) mSelectedItemsIds.put(position, value);
        else mSelectedItemsIds.delete(position);
        adapter.notifyDataSetChanged();
    }

    private void deleteSelectedItems()
    {
        DatabaseManager manager = new DatabaseManager(this);
        manager.open();
        for (int i = 0; i < adapter.getCount(); i++)
        {
            if (shoppingListView.isItemChecked(i))
            {
                ShoppingItem selectedItem = (ShoppingItem) adapter.getItem(i);
                manager.deleteShoppingItem(selectedItem);
            }
            adapter.notifyDataSetChanged();
        }
        manager.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        if (shoppingModeOn)
        {
            menu.getItem(1).setTitle(getString(R.string.change_mode_off));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.shopping_mode_on_toast), Toast.LENGTH_SHORT).show();

        }
        else
        {
            menu.getItem(1).setTitle(getString(R.string.change_mode));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.shopping_mode_off_toast), Toast.LENGTH_SHORT).show();

        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.closeDrawers();
            else drawerLayout.openDrawer(Gravity.LEFT);
        }
        else if (item.getItemId() == R.id.menu_add)
        {
            Intent intent = new Intent(this, AddShoppingItemActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_change_mode)
        {

            invalidateOptionsMenu();
            if (shoppingModeOn == true)
            {
                shoppingModeOn = false;

                shoppingListView.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);

                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
            else
            {
                shoppingModeOn = true;
                DatabaseManager manager = new DatabaseManager(this);
                manager.open();
                ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItemsWithId();
                manager.close();

                adapter.setShoppingItemsList(shoppingItemsList);

                adapter.notifyDataSetChanged();

                shoppingListView.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);

                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            }
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume()
    {
        if (!shoppingModeOn)
        {
            DatabaseManager manager = new DatabaseManager(this);

            manager.open();

            ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItems();

            manager.deleteAllShoppingItems();
            shoppingItemsList = Utilities.removeDuplicatesFormShoppingItemsList(shoppingItemsList);

            for (ShoppingItem si : shoppingItemsList)
            {
                manager.createShoppingItem(si.getItem(), si.getQuantity(), si.getUnit(), false, si.getCategory());
            }

            adapter = new ShoppingListAdapter(this);
            adapter.setShoppingItemsList(manager.getAllShoppingItems());

            shoppingListView.setAdapter(adapter);

            manager.close();

            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            shoppingListView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
        else
        {
            DatabaseManager manager = new DatabaseManager(ShoppingListActivity.this);

            manager.open();

            ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItemsWithId();

            manager.deleteAllShoppingItems();
            shoppingItemsList = Utilities.removeDuplicatesFormShoppingItemsList(shoppingItemsList);

            for (ShoppingItem si : shoppingItemsList)
            {
                manager.createShoppingItem(si.getItem(), si.getQuantity(), si.getUnit(), si.isChecked() ? true : false, si.getCategory());
            }

            shoppingItemsList = manager.getAllShoppingItemsWithId();

            manager.close();

            ShoppingListSwipeAdapter shoppingListSwipeAdapter = new ShoppingListSwipeAdapter(getSupportFragmentManager());

            viewPager.setAdapter(shoppingListSwipeAdapter);

            adapter = new ShoppingListAdapter(this);

            adapter.setShoppingItemsList(shoppingItemsList);
            shoppingListView.setAdapter(adapter);

            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
                {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
                {

                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
                {

                }
            };

            getSupportActionBar().removeAllTabs();

            for (int i = 0; i < Utilities.shoppingListTabsItemsArray.length; i++)
            {
                getSupportActionBar().addTab(getSupportActionBar().newTab().setIcon(getResources().getDrawable(Utilities.iconForTabString(Utilities.shoppingListTabsItemsArray[i]))).setTabListener(tabListener));
            }

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {
                @Override
                public void onPageScrolled(int i, float v, int i2)
                {

                }

                @Override
                public void onPageSelected(int i)
                {
                    getSupportActionBar().setSelectedNavigationItem(i);
                }

                @Override
                public void onPageScrollStateChanged(int i)
                {

                }
            });

            shoppingListView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }


        super.onResume();
    }

    @Override
    public void onFragmentInteraction(String id)
    {

    }
}
