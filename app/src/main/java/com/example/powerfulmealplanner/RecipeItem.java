package com.example.powerfulmealplanner;

import java.util.ArrayList;

/**
 * Created by Gucia on 2014-08-17.
 */
public class RecipeItem {
    private long id;
    private String name;

    public RecipeItem(String name, ArrayList<ShoppingItem> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<ShoppingItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<ShoppingItem> ingredients) {
        this.ingredients = ingredients;
    }

    private ArrayList<ShoppingItem> ingredients;

}
