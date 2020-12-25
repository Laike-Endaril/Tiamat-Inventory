package moe.plushie.rpg_framework.api.itemData;

import moe.plushie.rpg_framework.api.currency.ICost;

import java.util.ArrayList;

public interface IItemData {

    public ArrayList<String> getCategories();

    public ArrayList<String> getTags();

    public ICost getValue();
    
    public IItemData setCategories(ArrayList<String> categories);
    
    public IItemData setTags(ArrayList<String> tags);
    
    public IItemData setValue(ICost value);
    
    public boolean isDataMissing();
}
