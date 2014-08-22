package com.example.powerfulmealplanner;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utilities
{
    public static String[] navigationItemsArray = {App.getContext().getResources().getString(R.string.shopping_list), App.getContext().getResources().getString(R.string.recipies_list), App.getContext().getResources().getString(R.string.callendar), App.getContext().getResources().getString(R.string.help)};

    public static String formatDateforDB(Date date)
    {
        return new SimpleDateFormat("ddMMyyyy").format(date);
    }

    public static ArrayList<Date> getDates(Date date1, Date date2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static ArrayList<Date> getAllDatesForMonth(int month)
    {
        final Calendar calendar = Calendar.getInstance();

        ArrayList<Date> dates = new ArrayList<Date>();

        int currentMonth;

        if (month > 0)
            currentMonth = month;
        else
            currentMonth = calendar.get(Calendar.MONTH) + 1;

        int currentYear = calendar.get(Calendar.YEAR);

        Log.e("month: ", Integer.valueOf(currentMonth).toString());
        Log.e("year: ", Integer.valueOf(currentYear).toString());

        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, currentMonth);


        int numDays = calendar.getActualMaximum(Calendar.DATE);

        for (int i = 1; i < numDays + 1; i++)
        {
            calendar.set(Calendar.DAY_OF_MONTH, i);

            Date dd = calendar.getTime();

            dates.add(dd);
        }

        return dates;
    }
}
