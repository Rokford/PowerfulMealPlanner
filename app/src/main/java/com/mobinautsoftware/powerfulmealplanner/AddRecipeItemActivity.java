package com.mobinautsoftware.powerfulmealplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddRecipeItemActivity extends ActionBarActivity
{
    private EditText nameEditText;
    private Button addIngredientButton;
    private Bundle extras;
    private IngredientsForRecipeListAdapter recipeIngredientsAdapter;
    private ListView ingredientsListView;
    private ArrayList<ShoppingItem> ingredientItemsList;
    private String recipeName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {

                String name = data.getStringExtra("name");
                String quantity = data.getStringExtra("quantity");
                String unit = data.getStringExtra("unit");
                String category = data.getStringExtra("category");
                ShoppingItem item = new ShoppingItem(name, quantity, unit, category);
                ingredientItemsList.add(item);
                recipeIngredientsAdapter.setShoppingItemsList(ingredientItemsList);
                recipeIngredientsAdapter.notifyDataSetChanged();
            }
            else if (requestCode == 2)
            {
                DatabaseManager manager = new DatabaseManager(this);
                manager.open();
                ingredientItemsList = manager.getAllShoppingItemsForRecipe(recipeName);
                manager.close();
                recipeIngredientsAdapter.setShoppingItemsList(ingredientItemsList);
                recipeIngredientsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe_item);

        getSupportActionBar().setTitle(getResources().getString(R.string.add_item));
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ingredientItemsList = new ArrayList<ShoppingItem>();
        recipeIngredientsAdapter = new IngredientsForRecipeListAdapter(this);
        recipeIngredientsAdapter.setShoppingItemsList(ingredientItemsList);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        addIngredientButton = (Button) findViewById(R.id.addIngredientButton);
        ingredientsListView = (ListView) findViewById(R.id.ingredient_list);
        ingredientsListView.setAdapter(recipeIngredientsAdapter);

        extras = getIntent().getExtras();
        if (extras != null)
        {
            //POPULATE WITH ITEM SELECTED TO EDIT
            recipeName = extras.getString("recipe_name");
            nameEditText.setText(recipeName);

            DatabaseManager manager = new DatabaseManager(this);
            manager.open();
            ingredientItemsList = manager.getAllShoppingItemsForRecipe(recipeName);
            manager.close();
            recipeIngredientsAdapter.setShoppingItemsList(ingredientItemsList);
            recipeIngredientsAdapter.notifyDataSetChanged();

            ingredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent intent = new Intent(AddRecipeItemActivity.this, AddShoppingItemActivity.class);
                    intent.putExtra("for_recipe_edit", "true");
                    intent.putExtra("id", (int) ingredientItemsList.get(position).getId());
                    intent.putExtra("recipe_name", recipeName);
                    startActivityForResult(intent, 2, null);
                }
            });
        }

        recipeIngredientsAdapter.setRecipeName(recipeName);

        addIngredientButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AddRecipeItemActivity.this, AddShoppingItemActivity.class);
                intent.putExtra("for_recipe", "true");
                startActivityForResult(intent, 1, null);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Bundle extras = getIntent().getExtras();
        if (item.getItemId() == R.id.menu_save)
        {
            String recipeNameTextBox = nameEditText.getText().toString();

            if (recipeNameTextBox.length() == 0 || ingredientItemsList.isEmpty())
            {
                Toast.makeText(this, getResources().getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show();
            }
            else
            {
                DatabaseManager manager = new DatabaseManager(this);
                manager.open();
                if (extras != null)
                {
                    //EDITING: DELETE OLD AND ADD NEW RECIPE
                    manager.deleteRecipeItem(recipeName);

                    for (int i = 0; i < ingredientItemsList.size(); i++)
                    {
                        ShoppingItem shoppingItem = ingredientItemsList.get(i);
                        manager.createRecipeItem(recipeNameTextBox, shoppingItem.getItem(), shoppingItem.getQuantity(), shoppingItem.getUnit(), shoppingItem.getCategory());
                    }
                    finish();
                }
                else
                {
                    for (int i = 0; i < ingredientItemsList.size(); i++)
                    {
                        ShoppingItem shoppingItem = ingredientItemsList.get(i);
                        manager.createRecipeItem(recipeNameTextBox, shoppingItem.getItem(), shoppingItem.getQuantity(), shoppingItem.getUnit(), shoppingItem.getCategory());
                    }
                }
                manager.close();
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_item_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
