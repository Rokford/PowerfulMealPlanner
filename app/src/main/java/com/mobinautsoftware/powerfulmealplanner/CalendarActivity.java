package com.mobinautsoftware.powerfulmealplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends ActionBarActivity
{
    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private ListView drawerList;

    private CaldroidFragment caldroidFragment;

    private AlertDialog calendarDialog;

    private boolean inSelectionMode = false;

    private Date firstDate = null;
    private Date lastDate = null;

    int displayedMonth;
    int displayedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new NavigationDrawerAdapter(this));

        getSupportActionBar().setTitle(getResources().getString(R.string.callendar));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.yes, R.string.no)
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
                    Intent intent = new Intent(CalendarActivity.this, RecipeListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.shopping_list))
                {
                    Intent intent = new Intent(CalendarActivity.this, ShoppingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.callendar))
                {
                    Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.help))
                {
                    Intent intent = new Intent(CalendarActivity.this, HelpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        final Calendar cal = Calendar.getInstance();

        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);

        displayedMonth = cal.get(Calendar.MONTH) + 1;
        displayedYear = cal.get(Calendar.YEAR);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.calendar_outer_LL, caldroidFragment);
        t.commit();

        Date date = new Date();
        final java.text.DateFormat formatter = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        final CaldroidListener listener = new CaldroidListener()
        {

            @Override
            public void onSelectDate(final Date date, View view)
            {
                if (inSelectionMode)
                {

                    if ((firstDate == null && lastDate == null) || (firstDate != null && lastDate != null))
                    {
                        firstDate = date;
                        lastDate = null;

                        caldroidFragment.clearSelectedDates();
                        caldroidFragment.refreshView();

                        Toast.makeText(getApplicationContext(), getString(R.string.select_the_last_day), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (date.after(firstDate))
                        {
                            caldroidFragment.setSelectedDates(firstDate, date);
                            lastDate = date;
                        }
                        else if (date.before(firstDate))
                        {
                            caldroidFragment.setSelectedDates(date, firstDate);
                            lastDate = firstDate;
                            firstDate = date;
                        }
                        else
                        {
                            caldroidFragment.setSelectedDates(date, date);
                            lastDate = date;
                        }


                        caldroidFragment.refreshView();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                    // Get the layout inflater
                    LayoutInflater inflater = CalendarActivity.this.getLayoutInflater();

                    View dialogView = inflater.inflate(R.layout.calendar_dialog, null);

                    ListView listView = (ListView) dialogView.findViewById(R.id.calendarDialogListView);

                    final CalendarDayAdapter calendarAdapter = new CalendarDayAdapter(CalendarActivity.this);

                    DatabaseManager manager = new DatabaseManager(CalendarActivity.this);

                    manager.open();

                    ArrayList<String> recipes = manager.getRecipeNamesForDate(Utilities.formatDateforDB(date));

                    manager.close();

                    calendarAdapter.setRecipeItemsList(recipes);
                    calendarAdapter.setDate(Utilities.formatDateforDB(date));

                    listView.setAdapter(calendarAdapter);

                    builder.setView(dialogView);

                    calendarDialog = builder.create();

                    calendarDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            caldroidFragment.getCaldroidListener().onChangeMonth(displayedMonth, displayedYear);
                        }
                    });

                    calendarDialog.show();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            if (calendarAdapter.getItem(position) == null)
                            {
                                Intent i = new Intent(CalendarActivity.this, RecipeListActivity.class);
                                i.putExtra("forCalendar", "forCalendar");
                                i.putExtra("dateFromCalendar", Utilities.formatDateforDB(date));
                                startActivityForResult(i, 1);
                            }
                            else
                            {
                                // TODO: lol no idea what do do here
                            }
                        }
                    });
                }
            }

            @Override
            public void onChangeMonth(int month, int year)
            {
                displayedMonth = month;
                displayedYear = year;

                ArrayList<Date> dates = Utilities.getAllDatesForMonth(month - 1);

                DatabaseManager manager = new DatabaseManager(CalendarActivity.this);

                manager.open();

                for (Date date : dates)
                {
                    ArrayList<String> recipes = manager.getRecipeNamesForDate(Utilities.formatDateforDB(date));

                    if (recipes.size() > 0)
                    {
                        caldroidFragment.setBackgroundResourceForDate(R.color.light_blue, date);
                    }
                }

                manager.close();

                caldroidFragment.refreshView();

                //                                String text = "month: " + month + " year: " + year;
                //                                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view)
            {
                //                Toast.makeText(getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated()
            {

            }
        };

        caldroidFragment.setCaldroidListener(listener);

        //        caldroidFragment.setSelectedDates();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                //                String result = data.getStringExtra("result");
                if (caldroidFragment != null)
                {
                    if (calendarDialog != null) calendarDialog.dismiss();

                    caldroidFragment.getCaldroidListener().onChangeMonth(displayedMonth, displayedYear);

                    caldroidFragment.refreshView();
                }
            }
            if (resultCode == RESULT_CANCELED)
            {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendar, menu);

        if (inSelectionMode)
        {
            menu.getItem(0).setTitle(getString(R.string.create_list));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.closeDrawers();
            else drawerLayout.openDrawer(Gravity.LEFT);
        }
        else if (item.getItemId() == R.id.generate)
        {
            if (inSelectionMode)
            {
                // TODO: get firstDate and LastDate here, create Shopping List for every day between them
                ArrayList<Date> dates = new ArrayList<Date>();

                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(firstDate);

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(lastDate);

                while (!cal1.after(cal2))
                {
                    dates.add(cal1.getTime());
                    cal1.add(Calendar.DATE, 1);
                }

                final DatabaseManager manager = new DatabaseManager(CalendarActivity.this);
                manager.open();
                ArrayList<String> recipes = new ArrayList<String>();
                for (Date d : dates)
                {
                    recipes.addAll(manager.getRecipeNamesForDate(Utilities.formatDateforDB(d)));
                }

                ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();
                for (String r : recipes)
                {
                    shoppingItems.addAll(manager.getAllShoppingItemsForRecipe(r));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);

                builder.setMessage("Do you want to clear previously created shopping list?");
                final ArrayList<ShoppingItem> finalShoppingItems = shoppingItems;
                builder.setPositiveButton("Add to old list", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ArrayList<ShoppingItem> existingShoppingItems = manager.getAllShoppingItems();

                        finalShoppingItems.addAll(existingShoppingItems);

                        ArrayList<ShoppingItem> itemsWithoutDuplicates = Utilities.removeDuplicatesFormShoppingItemsList(finalShoppingItems);

                        manager.deleteAllShoppingItems();

                        for (ShoppingItem si : itemsWithoutDuplicates)
                        {
                            manager.createShoppingItem(si.getItem(), si.getQuantity(), si.getUnit(), false, si.getCategory());
                        }
                        manager.close();

                        Toast.makeText(CalendarActivity.this, "Shopping list generated successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                final ArrayList<ShoppingItem> finalShoppingItems1 = shoppingItems;
                builder.setNegativeButton("Create new one", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        manager.deleteAllShoppingItems();

                        ArrayList<ShoppingItem> itemsWithoutDuplicates = Utilities.removeDuplicatesFormShoppingItemsList(finalShoppingItems);

                        for (ShoppingItem si : itemsWithoutDuplicates)
                        {
                            manager.createShoppingItem(si.getItem(), si.getQuantity(), si.getUnit(), false, si.getCategory());
                        }
                        manager.close();

                        Toast.makeText(CalendarActivity.this, "Shopping list generated successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.create().show();
            }
            else
            {
                inSelectionMode = true;

                //                Toast.makeText(getApplicationContext(), getString(R.string.select_the_first_day), Toast.LENGTH_SHORT).show();

                invalidateOptionsMenu();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void leaveSelectionMode()
    {
        inSelectionMode = false;
        invalidateOptionsMenu();
        caldroidFragment.clearSelectedDates();
        caldroidFragment.refreshView();
    }

    @Override
    public void onBackPressed()
    {
        if (inSelectionMode) leaveSelectionMode();
        else super.onBackPressed();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
