package com.example.powerfulshoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeListAdapter extends BaseAdapter
{
    private ArrayList<String> recipeNamesList;
    private ViewHolder holder;
    private LayoutInflater inflater;
    // private SparseBooleanArray mSelectedItemsIds;

    private boolean withCheckboxes = false;

    public RecipeListAdapter(Context context, boolean withCheckboxes)
    {
        // mSelectedItemsIds = new SparseBooleanArray();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (withCheckboxes)
            this.withCheckboxes = true;
    }

    @Override
    public int getCount()
    {
        return recipeNamesList.size();
    }

    @Override
    public String getItem(int position)
    {
        return recipeNamesList.get(position);
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

            convertView = inflater.inflate(R.layout.recipe_list_view_item, null);

            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText(recipeNamesList.get(position));

        if (withCheckboxes)
            holder.checkBox.setVisibility(View.VISIBLE);
        else
            holder.checkBox.setVisibility(View.GONE);

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
        public CheckBox checkBox;
    }

    public ArrayList<String> getRecipeNamesListList()
    {
        return recipeNamesList;
    }

    public void setRecipeItemsList(ArrayList<String> recipeItemsList)
    {
        this.recipeNamesList = recipeItemsList;
    }
}
