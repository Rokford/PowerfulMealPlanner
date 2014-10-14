package com.example.powerfulmealplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientsForRecipeListAdapter extends BaseAdapter
{
    private ArrayList<ShoppingItem> shoppingItemsList;
    private ViewHolder holder;
    private LayoutInflater inflater;
    private String recipeName;
    private Context context;

    public IngredientsForRecipeListAdapter(Context context)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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

            convertView = inflater.inflate(R.layout.ingredients_list_view_item, null);

            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.quantityTextView = (TextView) convertView.findViewById(R.id.quantityTextView);
            holder.unitTextView = (TextView) convertView.findViewById(R.id.unitTextView);
            holder.deleteImageView = (ImageView) convertView.findViewById(R.id.deleteImageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ShoppingItem item = shoppingItemsList.get(position);

        holder.nameTextView.setText(item.getItem());
        holder.quantityTextView.setText(item.getQuantity());
        holder.unitTextView.setText(item.getUnit());

        holder.deleteImageView.setTag(holder.nameTextView.getText());

        holder.deleteImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (recipeName != null)
                {
                    //remove item from database
                    DatabaseManager manager = new DatabaseManager(context);
                    manager.open();
                    manager.deleteRecipeingredient(recipeName, v.getTag().toString());
                    manager.close();
                }
                    //remove item from the list
                    for (ShoppingItem item : shoppingItemsList)
                    {
                        if (v.getTag().toString().equals(item.getItem()))
                        {
                            shoppingItemsList.remove(item);
                            notifyDataSetChanged();
                            break;
                        }
                    }
            }
        });

        return convertView;
    }

    public static class ViewHolder
    {
        public TextView nameTextView;
        public TextView quantityTextView;
        public TextView unitTextView;
        public ImageView deleteImageView;
    }

    public ArrayList<ShoppingItem> getShoppingItemsList()
    {
        return shoppingItemsList;
    }

    public void setShoppingItemsList(ArrayList<ShoppingItem> shoppingItemsList)
    {

        this.shoppingItemsList = shoppingItemsList;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return false;
    }

    public void setRecipeName(String recipeName)
    {
        this.recipeName = recipeName;
    }
}
