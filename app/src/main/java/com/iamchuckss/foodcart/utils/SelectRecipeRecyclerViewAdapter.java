package com.iamchuckss.foodcart.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Ingredient;
import com.iamchuckss.foodcart.models.Recipe;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectRecipeRecyclerViewAdapter extends RecyclerView.Adapter<SelectRecipeRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "SelectRecipeRecyclerVie";

    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Ingredient>> mRecipeIngredientsMap = new HashMap<>();
    private Context mContext;

    public SelectRecipeRecyclerViewAdapter(ArrayList<Recipe> mRecipeList, HashMap<Integer, ArrayList<Ingredient>> mRecipeIngredientsMap,
                                           Context mContext) {
        this.mRecipeList = mRecipeList;
        this.mRecipeIngredientsMap = mRecipeIngredientsMap;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // widgets
        RelativeLayout parentLayout;
        TextView recipeTitle, recipeWebsite, recipeIngredients;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            recipeTitle = (TextView) itemView.findViewById(R.id.recipe_title);
            recipeWebsite = (TextView) itemView.findViewById(R.id.recipe_website);
            recipeIngredients = (TextView) itemView.findViewById(R.id.recipe_ingredients);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem_select_recipe, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: A new item is added to the list.");

        viewHolder.recipeTitle.setText(mRecipeList.get(i).getTitle()); // set recipe title
        viewHolder.recipeWebsite.setText(mRecipeList.get(i).getWebsite()); // set recipe website

        // retrieve recipe ingredients
        ArrayList<Ingredient> recipeIngredients = mRecipeIngredientsMap.get(i);
        Log.d(TAG, "onBindViewHolder: " + mRecipeIngredientsMap);
        ArrayList<String> recipeIngredientsTitles = new ArrayList<>();
        for(Ingredient ingredient : recipeIngredients) {
            // get all ingredients' title
            recipeIngredientsTitles.add(ingredient.getTitle());
        }

        // set recipe ingredients
        viewHolder.recipeIngredients.setText(StringManipulation.arrayToString(recipeIngredientsTitles));

        // set checkBox
        viewHolder.checkBox.setChecked(mRecipeList.get(i).isChecked());

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on checkbox: " + mRecipeList.get(i));

                // get clicked checkbox
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox);

                if(checkBox.isChecked()) { // checkBox is checked
                    // set recipe to checked
                    mRecipeList.get(i).setChecked(true);

                } else { // checkBox is unchecked
                    // set recipe to unchecked
                    mRecipeList.get(i).setChecked(false);
                }

                Log.d(TAG, "onClick: " + mRecipeList.get(i).getTitle() + " is checked: " + mRecipeList.get(i).isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }
}
