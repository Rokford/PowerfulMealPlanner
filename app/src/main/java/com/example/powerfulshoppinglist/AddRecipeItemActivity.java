package com.example.powerfulshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class AddRecipeItemActivity  extends ActionBarActivity
{
    private EditText nameEditText;
    private Button addIngredientButton;
    private Bundle extras;
    private ShoppingListAdapter recipeIngredientsAdapter;
    private ListView ingredientsListView;
    private ArrayList<ShoppingItem> ingredientItemsList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                String name = data.getStringExtra("name");
                String quantity = data.getStringExtra("quantity");
                String unit = data.getStringExtra("unit");
                ShoppingItem item = new ShoppingItem(name, quantity, unit);
                ingredientItemsList.add(item);
                recipeIngredientsAdapter.setShoppingItemsList(ingredientItemsList);
                recipeIngredientsAdapter.notifyDataSetChanged();
            }
//            if (resultCode == RESULT_CANCELED)
//            {
//                //Write your code if there's no result
//            }
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
        recipeIngredientsAdapter = new ShoppingListAdapter(this);
        recipeIngredientsAdapter.setShoppingItemsList(ingredientItemsList);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        addIngredientButton = (Button) findViewById(R.id.addIngredientButton);
        ingredientsListView = (ListView) findViewById(R.id.ingredient_list);
        ingredientsListView.setAdapter(recipeIngredientsAdapter);

        extras = getIntent().getExtras();

        addIngredientButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AddRecipeItemActivity.this, AddShoppingItemActivity.class);
                intent.putExtra("for_recipe","true");
                startActivityForResult(intent, 1, null);
            }
        });


        }

//        if (extras != null) {
//            DatabaseManager manager = new DatabaseManager(this);
//            manager.open();
//            ArrayList<ShoppingItem> shoppingItemsList = manager.getAllShoppingItems();
//            manager.close();
//            ShoppingItem item = shoppingItemsList.get(extras.getInt("id"));
//            nameEditText.setText(item.getItem());
//            quantityEditText.setText(item.getQuantity());
//            unitEditText.setText(item.getUnit());
//        }




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_item_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

}
