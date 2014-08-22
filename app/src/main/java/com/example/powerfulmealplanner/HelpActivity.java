package com.example.powerfulmealplanner;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

@SuppressLint("NewApi")
public class HelpActivity extends ActionBarActivity
{

    public static final String HELP = "com.ppdr.fiba.beta.HELP";

    private List<Drawable> bitmaps = new ArrayList<Drawable>();

    private List<String> descriptions = new ArrayList<String>();

    private List<ImageView> paging = new ArrayList<ImageView>();

    private LinearLayout pagingRow;

    private ViewFlipper flipper;

    private TextView descriptionText;

    private float downX = 0;

    private float downY = 0;

    private boolean down = false;

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_help);

        flipper = (ViewFlipper) findViewById(R.id.view_flipper);
        pagingRow = (LinearLayout) findViewById(R.id.paging);
        descriptionText = (TextView) findViewById(R.id.description);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

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

        drawerLayout.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int drawerZone = (int)(((double)v.getWidth()/(double)10));

                if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                    return false;
                else if (event.getX() < drawerZone)
                {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    return true;
                }
                else
                {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    {
                        downX = event.getX();
                        downY = event.getY();
                        down = true;
                    }
                    else if (event.getActionMasked() == MotionEvent.ACTION_MOVE)
                    {
                        if (down && event.getX() < downX - 100)
                        {
                            down = false;
                            next();
                        }
                        if (down && event.getX() > downX + 100)
                        {
                            down = false;
                            prev();
                        }
                    }

                    return true;

                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerList.setAdapter(new NavigationDrawerAdapter(this));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                String value = (String) adapter.getItemAtPosition(position);
                if (value == getResources().getString(R.string.recipies_list))
                {
                    Intent intent = new Intent(HelpActivity.this, RecipeListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.shopping_list))
                {
                    Intent intent = new Intent(HelpActivity.this, ShoppingListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.callendar))
                {
                    Intent intent = new Intent(HelpActivity.this, CalendarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if (value == getResources().getString(R.string.help))
                {
                    Intent intent = new Intent(HelpActivity.this, HelpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

        initScreens();
        pageSet(0);
    }

    private void initScreens()
    {
        addPage(getResources().getDrawable(R.drawable.help2), getString(R.string.help_1));
        addPage(getResources().getDrawable(R.drawable.help3), getString(R.string.help_2));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            // finish();
            if (drawerLayout.isDrawerOpen(Gravity.LEFT))
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(Gravity.LEFT);
        }

        return super.onOptionsItemSelected(item);
    }

    private void next()
    {
        int current = flipper.indexOfChild(flipper.getCurrentView());
        if (current < bitmaps.size() - 1)
        {
            flipper.setInAnimation(this, R.animator.page_left_in);
            flipper.setOutAnimation(this, R.animator.page_left_out);
            flipper.showNext();
            pageSet(current + 1);
        }
    }

    private void prev()
    {
        int current = flipper.indexOfChild(flipper.getCurrentView());
        if (current > 0)
        {
            flipper.setInAnimation(this, R.animator.page_right_in);
            flipper.setOutAnimation(this, R.animator.page_right_out);
            flipper.showPrevious();
            pageSet(current - 1);
        }
    }

    public void pageSet(int page)
    {
        for (ImageView view : paging)
        {
            view.setImageResource(R.drawable.circle_help_unselected);
        }
        paging.get(page).setImageResource(R.drawable.circle_help_selected);
        descriptionText.setText(descriptions.get(page));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {

            if (!drawerLayout.isDrawerOpen(Gravity.LEFT))
            {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            else
                drawerLayout.closeDrawers();

            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    private void addPage(Drawable drawable, String description)
    {
        bitmaps.add(drawable);
        descriptions.add(description);
        ImageView view = new ImageView(this);
        view.setImageResource(R.drawable.circle_help_unselected);
        paging.add(view);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

        LayoutParams lp = new LayoutParams(height, height);
        lp.leftMargin = 8;
        lp.rightMargin = 8;
        view.setLayoutParams(lp);

        pagingRow.addView(view);

        ImageView page = new ImageView(this);
        page.setImageDrawable(drawable);
        flipper.addView(page);
    }

}
