package com.example.powerfulshoppinglist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RecipeListActivity extends ActionBarActivity
{

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private ListView drawerList;

    private ListView recipeListView;

    private RecipeListAdapter adapter;

    private boolean forCalendar = false;

    //SparseBooleanArray selected;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("forCalendar") != null)
            forCalendar = true;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.navigation_drawer_item, Utilities.navigationItemsArray));

        getSupportActionBar().setTitle(getResources().getString(R.string.recipies_list));

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
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String) adapter.getItemAtPosition(position);
                if (value == getResources().getString(R.string.recipies_list))
                {
                    Intent intent = new Intent(RecipeListActivity.this, RecipeListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.shopping_list))
                {
                    Intent intent = new Intent(RecipeListActivity.this, ShoppingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.callendar))
                {
                    Intent intent = new Intent(RecipeListActivity.this, CalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        recipeListView = (ListView) findViewById(R.id.recipeListView);
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter2, View v, int position,
                                    long arg3)
            {
                //String value = (String) adapter.getItem(position);
                Intent intent = new Intent(RecipeListActivity.this, AddRecipeItemActivity.class);
                String selectedItem = (String) (adapter2.getAdapter()).getItem(position);
                intent.putExtra("recipe_name", selectedItem);
                startActivity(intent);
            }
        });

        //DELETING with multiple choice listener------------------------------------------------------------
        recipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked)
            {
                final int checkedCount = recipeListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.notifyDataSetChanged();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.recipe_list_menu, menu);

        if (!forCalendar)
        {
            menu.getItem(1).setVisible(false);
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
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        }
        else if (item.getItemId() == R.id.menu_new)
        {
            Intent intent = new Intent(this, AddRecipeItemActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_add_checked)
        {
            Boolean[] checkedRecipes = adapter.getCheckedRecipes();

            DatabaseManager manager = new DatabaseManager(RecipeListActivity.this);

            manager.open();

            for (int i = 0; i < checkedRecipes.length; i++)
            {
                if (checkedRecipes[i])
                {
                    manager.addRecipeToDateTable(adapter.getItem(i), getIntent().getStringExtra("dateFromCalendar"));
                }
            }

            manager.close();

            Intent returnIntent = new Intent();
//            returnIntent.putExtra("result",result);
            setResult(RESULT_OK, returnIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        DatabaseManager manager = new DatabaseManager(this);

        manager.open();

        ArrayList<String> recipeNamesList = manager.getAllRecipeNames();

        if (forCalendar)
            adapter = new RecipeListAdapter(this, true);
        else
            adapter = new RecipeListAdapter(this, false);

        adapter.setRecipeItemsList(recipeNamesList);

        recipeListView.setAdapter(adapter);

        manager.close();

        super.onResume();
    }

    private void deleteSelectedItems()
    {
        DatabaseManager manager = new DatabaseManager(this);
        manager.open();
        for (int i = 0; i < adapter.getCount(); i++)
        {
            if (recipeListView.isItemChecked(i))
            {
                String selectedItem = (String) adapter.getItem(i);
                manager.deleteRecipeItem(selectedItem);
            }
            adapter.notifyDataSetChanged();
        }
        manager.close();
    }
}
