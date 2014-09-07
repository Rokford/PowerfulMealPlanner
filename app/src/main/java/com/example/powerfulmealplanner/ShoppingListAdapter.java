package com.example.powerfulmealplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingListAdapter extends BaseAdapter
{
    private ArrayList<ShoppingItem> shoppingItemsList;
    private ViewHolder holder;
    private LayoutInflater inflater;
    // private SparseBooleanArray mSelectedItemsIds;

    public ShoppingListAdapter(Context context)
    {
        // mSelectedItemsIds = new SparseBooleanArray();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return shoppingItemsList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return shoppingItemsList.get(position);
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

            convertView = inflater.inflate(R.layout.shopping_list_view_item, null);

            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.quantityTextView = (TextView) convertView.findViewById(R.id.quantityTextView);
            holder.unitTextView = (TextView) convertView.findViewById(R.id.unitTextView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ShoppingItem item = shoppingItemsList.get(position);

        if(item.isChecked()) {
            holder.nameTextView.setTextColor(App.getContext().getResources().getColor(R.color.light_bronze));
            holder.quantityTextView.setTextColor(App.getContext().getResources().getColor(R.color.light_bronze));
            holder.unitTextView.setTextColor(App.getContext().getResources().getColor(R.color.light_bronze));
        } else {
            holder.nameTextView.setTextColor(App.getContext().getResources().getColor(R.color.caldroid_black));
            holder.quantityTextView.setTextColor(App.getContext().getResources().getColor(R.color.caldroid_black));
            holder.unitTextView.setTextColor(App.getContext().getResources().getColor(R.color.caldroid_black));
        }
        holder.nameTextView.setText(item.getItem());
        holder.quantityTextView.setText(item.getQuantity());
        holder.unitTextView.setText(item.getUnit());

        return convertView;
    }

//    public void toggleSelection(int position) {
//         selectView(position, !mSelectedItemsIds.get(position));
//    }
//
//    public void selectView(int position, boolean value) {
//        if (value)
//            mSelectedItemsIds.put(position, value);
//        else
//            mSelectedItemsIds.delete(position);
//        notifyDataSetChanged();
//    }

//    public SparseBooleanArray getSelectedIds() {
//        return mSelectedItemsIds;
//    }

    public static class ViewHolder
    {
        public TextView nameTextView;
        public TextView quantityTextView;
        public TextView unitTextView;

    }

    public ArrayList<ShoppingItem> getShoppingItemsList()
    {
        return shoppingItemsList;
    }

    public void setShoppingItemsList(ArrayList<ShoppingItem> shoppingItemsList)
    {
        ArrayList<ShoppingItem> shoppingItemsListSorted = new ArrayList<ShoppingItem>();

        for (ShoppingItem s : shoppingItemsList) {
            if (!s.isChecked()) {
                shoppingItemsListSorted.add(s);
            }
        }


        for (ShoppingItem s : shoppingItemsList) {
            if (s.isChecked()) {
                shoppingItemsListSorted.add(s);
            }
        }


        this.shoppingItemsList = shoppingItemsListSorted;

    }

}
