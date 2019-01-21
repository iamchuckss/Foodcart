package com.iamchuckss.foodcart.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Recipe implements Serializable{

    String recipe_id;
    String title;
    String website;
    HashMap<String, Integer> ingredients;
    boolean checked;

    public Recipe(String title, String website, HashMap<String, Integer> ingredients) {
        this.title = title;
        this.website = website;
        this.ingredients = ingredients;
        checked = false;
    }

    public Recipe() {
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public HashMap<String, Integer> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "title='" + title + '\'' +
                ", website='" + website + '\'' +
                ", ingredients=" + ingredients +
                ", checked=" + checked +
                '}';
    }
}
