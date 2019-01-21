package com.iamchuckss.foodcart.utils;

import com.iamchuckss.foodcart.models.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;

public class IngredientsListHandler {

    public static HashMap<String, Integer> convertIngredientsList(ArrayList<Ingredient> list) {

        HashMap<String, Integer> ingredientsMap = new HashMap<>();

        for(Ingredient ingredient : list) {
            ingredientsMap.put(ingredient.getIngredient_id(), ingredient.getQuantity());
        }

        return ingredientsMap;
    }
}
