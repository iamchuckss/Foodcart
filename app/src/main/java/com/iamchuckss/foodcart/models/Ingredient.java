package com.iamchuckss.foodcart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.iamchuckss.foodcart.utils.FirebaseMethods;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class Ingredient implements Serializable{

    private String ingredient_id;
    private String title;
    private boolean checked;
    private Integer quantity;

    public Ingredient(String ingredient_id, String title) {
        this.ingredient_id = ingredient_id;
        this.title = title;
        this.checked = false;
        this.quantity = 0;
    }

    public Ingredient() {
    }

    public String getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(String ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredient_id='" + ingredient_id + '\'' +
                ", title='" + title + '\'' +
                ", checked=" + checked +
                '}';
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return checked == that.checked &&
                Objects.equals(ingredient_id, that.ingredient_id) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient_id, title, checked);
    }
}
