package com.mobinautsoftware.powerfulmealplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddShoppingItemActivity extends ActionBarActivity
{
    private AutoCompleteTextView nameEditText;
    private EditText quantityEditText;
    private AutoCompleteTextView unitEditText;
    private Bundle extras;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_item);

        getSupportActionBar().setTitle(getResources().getString(R.string.add_item));
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        nameEditText = (AutoCompleteTextView) findViewById(R.id.nameEditText);
        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        unitEditText = (AutoCompleteTextView) findViewById(R.id.unitEditText);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        extras = getIntent().getExtras();

        final DatabaseManager manager = new DatabaseManager(this);
        manager.open();

        if (extras != null && extras.getString("for_recipe") == null)
        {
            ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItems();
            int id = extras.getInt("id");
            ShoppingItem item = null;
            for (ShoppingItem s : shoppingItemsList)
            {
                if (s.getId() == id)
                {
                    item = s;
                }
            }

            //ShoppingItem item = shoppingItemsList.get(extras.getInt("id" ));
            nameEditText.setText(item.getItem());
            quantityEditText.setText(item.getQuantity());
            unitEditText.setText(item.getUnit());

            String categoryName = item.getCategory();

            radioGroup.check(getIdOfCategory(categoryName));
        }
        else
            radioGroup.check(getIdOfCategory(Utilities.CATEGORY_OTHER));

        ArrayList<String> allItems = manager.getAllItems(false);
        ArrayList<String> allUnits = manager.getAllItems(true);

        for (int i = 0; i < allUnits.size(); i++)
        {
            Collections.sort(allUnits, new Comparator<String>()
            {
                @Override
                public int compare(String lhs, String rhs)
                {
                    if (manager.getCountForUnit(lhs) > manager.getCountForUnit(rhs)) return -1;
                    else return 1;
                }
            });
        }

        final ArrayList<String> unitsForCommonUnitsList = new ArrayList<String>();

        for (int i = 0; (allUnits.size() > 5 ? i < 5 : i < allUnits.size()); i++)
        {
            unitsForCommonUnitsList.add(allUnits.get(i));
        }

        ArrayAdapter<String> commonUnitsArrayAdapter = new ArrayAdapter<String>(this, R.layout.common_units_item, R.id.nameTextView, unitsForCommonUnitsList);

        ListView commonUnitsListView = (ListView) findViewById(R.id.commonUnitsListView);

        commonUnitsListView.setAdapter(commonUnitsArrayAdapter);

        commonUnitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                unitEditText.setText(unitsForCommonUnitsList.get(position));
            }
        });

        AutocompleteItemAdapter itemsAdapter = new AutocompleteItemAdapter(this, R.layout.autocomplete_textview_item, allItems);
        AutocompleteItemAdapter unitsAdapter = new AutocompleteItemAdapter(this, R.layout.autocomplete_textview_item, allUnits);

        itemsAdapter.setItems(allItems);
        itemsAdapter.setForUnit(false);

        unitsAdapter.setItems(allUnits);
        unitsAdapter.setForUnit(true);

        nameEditText.setAdapter(itemsAdapter);
        unitEditText.setAdapter(unitsAdapter);

        manager.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_item_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Bundle extras = getIntent().getExtras();
        if (item.getItemId() == R.id.menu_save)
        {
            String name = nameEditText.getText().toString();
            String quantity = quantityEditText.getText().toString();
            String unit = unitEditText.getText().toString();

            String category = Utilities.CATEGORY_OTHER;

            for (String categoryName : Utilities.shoppingListTabsItemsArray)
            {
                if (getIdOfCategory(categoryName) == radioGroup.getCheckedRadioButtonId())
                {
                    category = categoryName;
                    break;
                }
            }

            if (name.length() == 0 || quantity.length() == 0 || unit.length() == 0)
            {
                Toast.makeText(this, getResources().getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show();
            }
            else
            {
                DatabaseManager manager = new DatabaseManager(this);
                manager.open();
                if (extras != null)
                {
                    if (extras.getString("for_recipe") != null)
                    {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("name", name);
                        returnIntent.putExtra("quantity", quantity);
                        returnIntent.putExtra("unit", unit);
                        returnIntent.putExtra("category", category);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                    else
                    {
                        int id = extras.getInt("id");
                        //id++;
                        manager.update_byID(id, name, quantity, unit, category);
                    }
                }
                else
                {
                    manager.createShoppingItem(name, quantity, unit, false, category);
                }

                manager.createItem(name, false);
                manager.createItem(unit, true);

                manager.addToUnitCounter(unit);

                manager.close();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onPause();
    }

    protected int getIdOfCategory(String categoryName)
    {
        int categoryId = R.id.otherRadioButton;

        if (categoryName.equals(Utilities.CATEGORY_CEREALS))
            categoryId = R.id.cerealsRadioButton;
        if (categoryName.equals(Utilities.CATEGORY_DIARY))
            categoryId = R.id.dairyRadioButton;
        if (categoryName.equals(Utilities.CATEGORY_FRUITS))
            categoryId = R.id.fruitsRadioButton;
        if (categoryName.equals(Utilities.CATEGORY_MEAT))
            categoryId = R.id.meatRadioButton;
        if (categoryName.equals(Utilities.CATEGORY_TINNED))
            categoryId = R.id.tinnedRadioButton;

        return categoryId;
    }

}
