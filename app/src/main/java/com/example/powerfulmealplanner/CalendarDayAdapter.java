package com.example.powerfulmealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarDayAdapter extends BaseAdapter
{
    private ArrayList<String> recipeNamesList = new ArrayList<String>();
    private ViewHolder holder;
    private LayoutInflater inflater;
    private Context context;
    private String date;

    private static int TYPE_RECIPE_ITEM = 0;
    private static int TYPE_OPTIONS = 1;

    public CalendarDayAdapter(Context context)
    {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position + 1 > recipeNamesList.size())
            return 1;
        else
            return 0;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getCount()
    {
        return recipeNamesList.size() + 1;
    }

    @Override
    public String getItem(int position)
    {
        if (getItemViewType(position) == TYPE_RECIPE_ITEM)
            return recipeNamesList.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        holder = null;

        if (convertView == null)
        {
            holder = new ViewHolder();

            if (getItemViewType(position) == TYPE_RECIPE_ITEM)
            {

                convertView = inflater.inflate(R.layout.calendar_list_view_item, null);

                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.deleteTextView = (ImageView) convertView.findViewById(R.id.deleteImageView);
            }
            else
            {
                convertView = inflater.inflate(R.layout.calendar_day_option_item, null);

                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            }

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItemViewType(position) == TYPE_RECIPE_ITEM)
        {
            holder.nameTextView.setText(recipeNamesList.get(position));
            holder.deleteTextView.setTag(recipeNamesList.get(position));

            holder.deleteTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DatabaseManager manager = new DatabaseManager(context);
                    manager.open();

                    manager.deleteRecipeFromDate(v.getTag().toString(), date);

                    String recipeToRemove = null;

                    for (String s : recipeNamesList)
                    {
                        if (s.equals(v.getTag().toString()))
                        {
                            recipeToRemove = s;
                            break;
                        }
                    }

                    recipeNamesList.remove(recipeToRemove);
                    notifyDataSetChanged();
                }
            });
        }
        else
        {
            holder.nameTextView.setText(App.getContext().getString(R.string.add_a_recipe_from_list));
        }

        return convertView;
    }

    public static class ViewHolder
    {
        public TextView nameTextView;
        public ImageView deleteTextView;
    }

    public ArrayList<String> getRecipeNamesListList()
    {
        return recipeNamesList;
    }

    public void setRecipeItemsList(ArrayList<String> recipeItemsList)
    {
        this.recipeNamesList = recipeItemsList;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}
