package com.example.powerfulmealplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseCreator extends SQLiteOpenHelper
{
	public static final String TABLE_SHOPPING_ITEMS = "shopping_items";
    public static final String TABLE_RECIPE_ITEMS = "recipes_ingredients";
    public static final String TABLE_RECIPE_DATES = "recipes_dates";
    public static final String TABLE_ITEMS = "items";
    public static final String TABLE_UNITS = "units";
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ITEM = "item";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_RECIPE_DATE = "date";
    public static final String COLUMN_IS_CHECKED = "is_checked";


    private static final String DATABASE_NAME = "powerful_shopping_list.db";
	private static final int DATABASE_VERSION = 11;

	private static final String DATABASE_CREATE = "create table " + TABLE_SHOPPING_ITEMS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_ITEM + " text not null," + COLUMN_QUANTITY + " text not null," + COLUMN_UNIT + " text not null," + COLUMN_IS_CHECKED +" text not null);";
    private static final String DATABASE_RECIPE_TABLE_NAME = "create table " + TABLE_RECIPE_ITEMS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_RECIPE_NAME + " text not null," + COLUMN_ITEM + " text not null," + COLUMN_QUANTITY + " text not null," + COLUMN_UNIT + " text not null);";
    private static final String DATABASE_RECIPE_TABLE_DATES = "create table " + TABLE_RECIPE_DATES + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_RECIPE_DATE + " text not null," + COLUMN_RECIPE_NAME + " text not null);";
    private static final String DATABASE_ITEMS = "create table " + TABLE_ITEMS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_ITEM + " text not null);";
    private static final String DATABASE_UNITS = "create table " + TABLE_UNITS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_ITEM + " text not null);";


	private static DatabaseCreator mInstance = null;

	private DatabaseCreator(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseCreator getInstance(Context ctx)
	{
		if (mInstance == null)
		{
			mInstance = new DatabaseCreator(ctx.getApplicationContext());
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_RECIPE_TABLE_NAME);
        db.execSQL(DATABASE_RECIPE_TABLE_DATES);
        db.execSQL(DATABASE_ITEMS);
        db.execSQL(DATABASE_UNITS);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.e(DatabaseCreator.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_DATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNITS);


        onCreate(db);
	}

}