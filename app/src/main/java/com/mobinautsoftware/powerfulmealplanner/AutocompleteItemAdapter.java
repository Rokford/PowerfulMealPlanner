package com.mobinautsoftware.powerfulmealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AutocompleteItemAdapter extends ArrayAdapter<String>
{
    private ViewHolder holder;
    private LayoutInflater inflater;
    private ArrayList<String> items;
    private ArrayList<String> itemsAll;
    private ArrayList<String> suggestions;

    private boolean forUnit = false;

    private Context context;

    public AutocompleteItemAdapter(Context context, int resource, ArrayList<String> items)
    {
        super(context, resource, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;

        this.items = items;
        this.itemsAll = (ArrayList<String>) items.clone();
        this.suggestions = new ArrayList<String>();
    }


    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public String getItem(int position)
    {
        return items.get(position);
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

            convertView = inflater.inflate(R.layout.autocomplete_textview_item, null);

            holder.itemTextView = (TextView) convertView.findViewById(R.id.itemTextView);
            holder.itemImageView = (ImageView) convertView.findViewById(R.id.itemImageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemTextView.setText(items.get(position));

        holder.itemImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatabaseManager manager = new DatabaseManager(context);

                manager.open();

                manager.deleteItem(getItem(position), forUnit);

                manager.close();

                items.remove(position);
                itemsAll.remove(position);

                notifyDataSetChanged();
                getFilter();
            }
        });


        return convertView;
    }

    public static class ViewHolder
    {
        public TextView itemTextView;
        public ImageView itemImageView;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            public String convertResultToString(Object resultValue)
            {
                return resultValue.toString();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                if (constraint != null)
                {
                    suggestions.clear();
                    for (String customer : itemsAll)
                    {
                        if (customer.toLowerCase().startsWith(constraint.toString().toLowerCase()))
                        {
                            suggestions.add(customer);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                }
                else
                {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                ArrayList<String> filteredList = (ArrayList<String>) results.values;
                if (results != null && results.count > 0)
                {
                    clear();
                    for (String c : filteredList)
                    {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }


    public ArrayList<String> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<String> items)
    {
        this.items = items;
    }

    public boolean isForUnit()
    {
        return forUnit;
    }

    public void setForUnit(boolean forUnit)
    {
        this.forUnit = forUnit;
    }
}
