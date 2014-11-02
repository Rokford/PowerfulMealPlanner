package com.mobinautsoftware.powerfulmealplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddShoppingItemActivity extends ActionBarActivity
{
    private AutoCompleteTextView nameEditText;
    private EditText quantityEditText;
    private AutoCompleteTextView unitEditText;
    private Bundle extras;

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

        extras = getIntent().getExtras();

        DatabaseManager manager = new DatabaseManager(this);
        manager.open();

        if (extras != null && extras.getString("for_recipe" ) == null)
        {
            ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItems();
            int id = extras.getInt("id" );
            ShoppingItem item = null;
            for (ShoppingItem s : shoppingItemsList) {
                if (s.getId() == id) {
                    item = s;
                }
            }

            //ShoppingItem item = shoppingItemsList.get(extras.getInt("id" ));
            nameEditText.setText(item.getItem());
            quantityEditText.setText(item.getQuantity());
            unitEditText.setText(item.getUnit());
        }

        ArrayList<String> allItems = manager.getAllItems(false);
        ArrayList<String> allUnits = manager.getAllItems(true);

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
                    if (extras.getString("for_recipe" ) != null)
                    {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("name", name);
                        returnIntent.putExtra("quantity", quantity);
                        returnIntent.putExtra("unit", unit);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                    else
                    {
                        int id = extras.getInt("id" );
                        //id++;
                        manager.update_byID(id, name, quantity, unit);
                    }

                }
                else
                {
                    manager.createShoppingItem(name, quantity, unit, false);
                }

                manager.createItem(name, false);
                manager.createItem(unit, true);

                manager.close();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
