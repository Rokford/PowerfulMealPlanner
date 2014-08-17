package com.example.powerfulshoppinglist;

public class ShoppingItem
{
	private long id;
	private String item;
	private String quantity;
	private String unit;
    private String recipeName;

    public ShoppingItem()
    {

    }

    public ShoppingItem(String item, String quantity, String unit)
    {
        this.item = item;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getItem()
	{
		return item;
	}

	public void setItem(String item)
	{
		this.item = item;
	}

	public String getQuantity()
	{
		return quantity;
	}

	public void setQuantity(String quantity)
	{
		this.quantity = quantity;
	}

	public String getRecipeName()
	{
		return recipeName;
	}

	public void setRecipeName(String recipeName)
	{
		this.recipeName = recipeName;
	}

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }
}
