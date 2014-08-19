package com.example.powerfulshoppinglist;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities
{
    public static String[] navigationItemsArray = {App.getContext().getResources().getString(R.string.shopping_list), App.getContext().getResources().getString(R.string.recipies_list), App.getContext().getResources().getString(R.string.callendar)};

    public static String formatDateforDB(Date date)
    {
        return new SimpleDateFormat("ddMMyyyy").format(date);
    }
}
