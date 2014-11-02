package com.mobinautsoftware.powerfulmealplanner;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager
{
    private SQLiteDatabase database;
    private DatabaseCreator databaseCreator;

    private String[] allColumns = {databaseCreator.COLUMN_ID, databaseCreator.COLUMN_ITEM, databaseCreator.COLUMN_QUANTITY, databaseCreator.COLUMN_UNIT};
    private String[] allColumnsWithId = {databaseCreator.COLUMN_ID, databaseCreator.COLUMN_ITEM, databaseCreator.COLUMN_QUANTITY, databaseCreator.COLUMN_UNIT, databaseCreator.COLUMN_IS_CHECKED};
    private String[] allColumnsAndRecipies = {databaseCreator.COLUMN_ID, databaseCreator.COLUMN_RECIPE_NAME, databaseCreator.COLUMN_ITEM, databaseCreator.COLUMN_QUANTITY, databaseCreator.COLUMN_UNIT};
    private String[] allRecipeColumnsNames = {databaseCreator.COLUMN_RECIPE_NAME};
    private String[] allItems = {databaseCreator.COLUMN_ITEM};

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

    public void deleteAllShoppingItems () {
        database.execSQL("delete from "+ databaseCreator.TABLE_SHOPPING_ITEMS);
    }
    public void createShoppingItem(String itemName, String quantity, String unit, boolean checked)
    {
        ContentValues values = new ContentValues();
        values.put(databaseCreator.COLUMN_ITEM, itemName);
        values.put(databaseCreator.COLUMN_QUANTITY, quantity);
        values.put(databaseCreator.COLUMN_UNIT, unit);
        if (checked)
            values.put(databaseCreator.COLUMN_IS_CHECKED, "y" );
        else
            values.put(databaseCreator.COLUMN_IS_CHECKED, "n" );

        long insertId = database.insert(databaseCreator.TABLE_SHOPPING_ITEMS, null, values);
    }

//    public void createShoppingItemChecked(String itemName, String quantity, String unit)
//    {
//        ContentValues values = new ContentValues();
//        values.put(databaseCreator.COLUMN_ITEM, itemName);
//        values.put(databaseCreator.COLUMN_QUANTITY, quantity);
//        values.put(databaseCreator.COLUMN_UNIT, unit);
//        values.put(databaseCreator.COLUMN_IS_CHECKED, "y" );
//
//        long insertId = database.insert(databaseCreator.TABLE_SHOPPING_ITEMS, null, values);
//
//
//    }

    public void createItem(String item, boolean forUnit)
    {
        String tableName = forUnit ? databaseCreator.TABLE_UNITS : databaseCreator.TABLE_ITEMS;

        database.execSQL("INSERT INTO " + tableName +
                " (" + databaseCreator.COLUMN_ITEM + ") SELECT * FROM (SELECT '" + item + "') WHERE NOT EXISTS(SELECT " + databaseCreator.COLUMN_ITEM + " FROM " + tableName + " WHERE " + databaseCreator.COLUMN_ITEM + " = '" + item + "' )LIMIT 1; " );

    }

    public void deleteItem(String item, boolean forUnit)
    {
        String tableName = forUnit ? databaseCreator.TABLE_UNITS : databaseCreator.TABLE_ITEMS;

        database.delete(tableName, databaseCreator.COLUMN_ITEM + " = " + "'" + item + "'", null);
    }

    public ArrayList<String> getAllItems(boolean forUnit)
    {
        String tableName = forUnit ? databaseCreator.TABLE_UNITS : databaseCreator.TABLE_ITEMS;

        ArrayList<String> items = new ArrayList<String>();

        Cursor cursor = database.query(tableName, allItems, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            items.add(cursor.getString(0));
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return items;
    }


    public void update_byID(int id, String v1, String v2, String v3)
    {
        ContentValues values = new ContentValues();
        values.put(databaseCreator.COLUMN_ITEM, v1);
        values.put(databaseCreator.COLUMN_QUANTITY, v2);
        values.put(databaseCreator.COLUMN_UNIT, v3);
        long insertId = database.update(databaseCreator.TABLE_SHOPPING_ITEMS, values, databaseCreator.COLUMN_ID + " = " + id, null);
    }

    public void updateCheckedMode_byID(int id)
    {
        ContentValues values = new ContentValues();
        String v1;
        Cursor cursor = database.query(databaseCreator.TABLE_SHOPPING_ITEMS, allColumnsWithId, databaseCreator.COLUMN_ID + " = " + id, null, null, null, null);
        ShoppingItem item = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            item = cursorToShoppingItemWithId(cursor);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        if (item.isChecked())
        {
            v1 = "n";
        }
        else
        {
            v1 = "y";
        }
        values.put(databaseCreator.COLUMN_IS_CHECKED, v1);
        long insertId = database.update(databaseCreator.TABLE_SHOPPING_ITEMS, values, databaseCreator.COLUMN_ID + " = " + id, null);
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem)
    {
        long id = shoppingItem.getId();

        database.delete(databaseCreator.TABLE_SHOPPING_ITEMS, databaseCreator.COLUMN_ID + " = " + id, null);
    }

    public void deleteShoppingItemById(int id)
    {

        database.delete(databaseCreator.TABLE_SHOPPING_ITEMS, databaseCreator.COLUMN_ID + " = " + id, null);
    }

    public void deleteRecipeItem(String recipeName)
    {
        database.delete(databaseCreator.TABLE_RECIPE_ITEMS, databaseCreator.COLUMN_RECIPE_NAME + " = '" + recipeName + "'", null);
    }

    public void deleteRecipeingredient(String recipeName, String ingredientname)
    {
        database.delete(databaseCreator.TABLE_RECIPE_ITEMS, databaseCreator.COLUMN_RECIPE_NAME + " = '" + recipeName + "'" + "and " + databaseCreator.COLUMN_ITEM + " = '" + ingredientname + "'", null);
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

    public ArrayList<ShoppingItem> getAllShoppingItemsWithId()
    {
        ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();

        Cursor cursor = database.query(databaseCreator.TABLE_SHOPPING_ITEMS, allColumnsWithId, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            ShoppingItem item = cursorToShoppingItemWithId(cursor);
            shoppingItems.add(item);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return shoppingItems;
    }

    private ShoppingItem cursorToShoppingItemWithId(Cursor cursor)
    {
        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setId(cursor.getLong(0));
        shoppingItem.setItem(cursor.getString(1));
        shoppingItem.setQuantity(cursor.getString(2));
        shoppingItem.setUnit(cursor.getString(3));
        if (cursor.getString(4).equals("y" ))
        {
            shoppingItem.setChecked(true);
        }
        else
        {
            shoppingItem.setChecked(false);
        }
        return shoppingItem;
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

    public void addRecipeToDateTable(String recipeName, String date)
    {
        ContentValues values = new ContentValues();
        values.put(databaseCreator.COLUMN_RECIPE_NAME, recipeName);
        values.put(databaseCreator.COLUMN_RECIPE_DATE, date);

        long insertId = database.insert(databaseCreator.TABLE_RECIPE_DATES, null, values);
    }

    public ArrayList<String> getRecipeNamesForDate(String date)
    {
        ArrayList<String> recipes = new ArrayList<String>();

        Cursor cursor = database.query(databaseCreator.TABLE_RECIPE_DATES, allRecipeColumnsNames, DatabaseCreator.COLUMN_RECIPE_DATE + " = '" + date + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            recipes.add(cursor.getString(0));
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();

        return recipes;
    }

    public void deleteRecipeFromDate(String recipeName, String date)
    {
        database.delete(databaseCreator.TABLE_RECIPE_DATES, databaseCreator.COLUMN_RECIPE_DATE + " = " + date + " and " + databaseCreator.COLUMN_RECIPE_NAME + " = " + "'" + recipeName + "'", null);
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

    public ArrayList<ShoppingItem> getAllShoppingItemsForRecipe(String recipeName)
    {
        ArrayList<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();

        Cursor cursor = database.query(databaseCreator.TABLE_RECIPE_ITEMS, allColumnsAndRecipies, databaseCreator.COLUMN_RECIPE_NAME + " = '" + recipeName + "'", null, null, null, null);

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

    public ShoppingItem getShoppingItemById (int id) {

        Cursor cursor = database.query(databaseCreator.TABLE_SHOPPING_ITEMS, allColumnsWithId, databaseCreator.COLUMN_ID + " = " + id, null, null, null, null);
        ShoppingItem item = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            item = cursorToShoppingItemWithId(cursor);
            cursor.moveToNext();
        }

        cursor.close();
        return item;

    }

}
