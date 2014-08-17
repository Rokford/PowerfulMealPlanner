package com.example.powerfulshoppinglist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager
{
	private SQLiteDatabase database;
	private DatabaseCreator databaseCreator;

	private String[] allColumns =
	{ databaseCreator.COLUMN_ID, databaseCreator.COLUMN_ITEM, databaseCreator.COLUMN_QUANTITY, databaseCreator.COLUMN_UNIT };
    private String[] allColumnsAndRecipies =
            { databaseCreator.COLUMN_ID, databaseCreator.COLUMN_RECIPE_NAME , databaseCreator.COLUMN_ITEM, databaseCreator.COLUMN_QUANTITY, databaseCreator.COLUMN_UNIT };
    private String[] allRecipeColumnsNames =
            { databaseCreator.COLUMN_RECIPE_NAME };

	public DatabaseManager(Context context)
	{
		databaseCreator = DatabaseCreator.getInstance(context);
	}

	public void open()
	{
		database = databaseCreator.getWritableDatabase();
	}

	public void close()
	{
		databaseCreator.close();
	}

	public void createShoppingItem(String itemName, String quantity, String unit)
	{
		ContentValues values = new ContentValues();
		values.put(databaseCreator.COLUMN_ITEM, itemName);
		values.put(databaseCreator.COLUMN_QUANTITY, quantity);
		values.put(databaseCreator.COLUMN_UNIT, unit);

		long insertId = database.insert(databaseCreator.TABLE_SHOPPING_ITEMS, null, values);

		// Cursor cursor = database.query(databaseCreator.TABLE_SHOPPING_ITEMS,
		// allColumns, databaseCreator.COLUMN_ID + " = " + insertId, null, null,
		// null, null);
		// cursor.moveToFirst();
		// ShoppingItem newShoppingItem = cursorToShoppingItem(cursor);
		// cursor.close();
	}

    public void update_byID(int id, String v1, String v2, String v3){
        ContentValues values = new ContentValues();
        values.put(databaseCreator.COLUMN_ITEM, v1);
        values.put(databaseCreator.COLUMN_QUANTITY, v2);
        values.put(databaseCreator.COLUMN_UNIT, v3);
        long insertId = database.update(databaseCreator.TABLE_SHOPPING_ITEMS, values,databaseCreator.COLUMN_ID + " = " + id, null);
    }

	public void deleteShoppingItem(ShoppingItem shoppingItem)
	{
		long id = shoppingItem.getId();

		database.delete(databaseCreator.TABLE_SHOPPING_ITEMS, databaseCreator.COLUMN_ID + " = " + id, null);
	}

	public ArrayList<ShoppingItem> getAllShoppingItems()
	{
		ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();

		Cursor cursor = database.query(databaseCreator.TABLE_SHOPPING_ITEMS, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ShoppingItem item = cursorToShoppingItem(cursor);
			shoppingItems.add(item);
			cursor.moveToNext();
		}

		// make sure to close the cursor
		cursor.close();
		return shoppingItems;
	}

	private ShoppingItem cursorToShoppingItem(Cursor cursor)
	{
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setId(cursor.getLong(0));
        shoppingItem.setItem(cursor.getString(1));
        shoppingItem.setQuantity(cursor.getString(2));
        shoppingItem.setUnit(cursor.getString(3));

        return shoppingItem;
	}

    private ShoppingItem cursorToRecipeItem(Cursor cursor)
    {
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setId(cursor.getLong(0));
        shoppingItem.setRecipeName(cursor.getString(1));
        shoppingItem.setItem(cursor.getString(2));
        shoppingItem.setQuantity(cursor.getString(3));
        shoppingItem.setUnit(cursor.getString(4));

        return shoppingItem;
    }

    private String cursorToRecipeName(Cursor cursor)
    {
        String recipeName = cursor.getString(0);

        return recipeName;
    }

    public void createRecipeItem(String recipeName, String itemName, String quantity, String unit)
    {
        ContentValues values = new ContentValues();
        values.put(databaseCreator.COLUMN_RECIPE_NAME, recipeName);
        values.put(databaseCreator.COLUMN_ITEM, itemName);
        values.put(databaseCreator.COLUMN_QUANTITY, quantity);
        values.put(databaseCreator.COLUMN_UNIT, unit);

        long insertId = database.insert(databaseCreator.TABLE_RECIPE_ITEMS, null, values);
    }

    public ArrayList<ShoppingItem> getAllRecipeItems()
    {
        ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();

        Cursor cursor = database.query(databaseCreator.TABLE_RECIPE_ITEMS, allColumnsAndRecipies, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ShoppingItem item = cursorToRecipeItem(cursor);
            shoppingItems.add(item);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return shoppingItems;
    }

    public ArrayList<String> getAllRecipeNames()
    {
        ArrayList<String> recipeNames = new ArrayList<String>();

        Cursor cursor = database.query(databaseCreator.TABLE_RECIPE_ITEMS, allRecipeColumnsNames, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            recipeNames.add(cursorToRecipeName(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        HashSet hs = new HashSet();
        hs.addAll(recipeNames);
        recipeNames.clear();
        recipeNames.addAll(hs);
        return recipeNames;
    }
}
