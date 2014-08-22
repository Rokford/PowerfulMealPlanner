package com.example.powerfulmealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter
{
    private List<String> navigationDrawerTitles = Arrays.asList(Utilities.navigationItemsArray);
    private ViewHolder holder;
    private LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context)
    {
        // mSelectedItemsIds = new SparseBooleanArray();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return navigationDrawerTitles.size();
    }

    @Override
    public String getItem(int position)
    {
        return navigationDrawerTitles.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        holder = null;

        if (convertView == null)
        {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.navigation_drawer_item, null);

            holder.nameTextView = (TextView) convertView.findViewById(R.id.textView1);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText(navigationDrawerTitles.get(position));

        switch (position)
        {
            case 0:
                holder.iconImageView.setImageResource(R.drawable.shopping_list);
                break;
            case 1:
                holder.iconImageView.setImageResource(R.drawable.recipes);
                break;
            case 2:
                holder.iconImageView.setImageResource(R.drawable.calendar);
                break;
            case 3:
                holder.iconImageView.setImageResource(R.drawable.help);
                break;
        }

        return convertView;
    }

    public static class ViewHolder
    {
        public TextView nameTextView;
        public ImageView iconImageView;
    }
}
