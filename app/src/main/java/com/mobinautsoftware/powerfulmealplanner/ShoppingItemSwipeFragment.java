package com.mobinautsoftware.powerfulmealplanner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobinautsoftware.powerfulmealplanner.R;

import com.mobinautsoftware.powerfulmealplanner.dummy.DummyContent;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ShoppingItemSwipeFragment extends ListFragment
{
    private ArrayList<ShoppingItem> shoppingItems;

    ShoppingListAdapter adapter;

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static ShoppingItemSwipeFragment newInstance(String name)
    {
        ShoppingItemSwipeFragment fragment = new ShoppingItemSwipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingItemSwipeFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        adapter = new ShoppingListAdapter(getActivity());

        adapter.setShoppingItemsList(shoppingItems);

        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        if (null != mListener)
        {
            ShoppingItem item = (ShoppingItem) adapter.getItem(position);
            long itemId = item.getId();

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(mParam1, itemId);
        }
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String categoryName, long tappedItemId);
    }

    public ArrayList<ShoppingItem> getShoppingItems()
    {
        return shoppingItems;
    }

    public void setShoppingItems(ArrayList<ShoppingItem> shoppingItems)
    {
        this.shoppingItems = shoppingItems;
    }
}
