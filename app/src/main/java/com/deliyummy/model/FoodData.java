
package com.deliyummy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodData {

    @SerializedName("category")
    @Expose
    private List<Category> category = null;
    @SerializedName("recommended")
    @Expose
    private List<Recommended> recommended = null;
    @SerializedName("allmenu")
    @Expose
    private List<Allmenu> allmenu = null;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<Recommended> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Recommended> recommended) {
        this.recommended = recommended;
    }

    public List<Allmenu> getAllmenu() {
        return allmenu;
    }

    public void setAllmenu(List<Allmenu> allmenu) {
        this.allmenu = allmenu;
    }

}
