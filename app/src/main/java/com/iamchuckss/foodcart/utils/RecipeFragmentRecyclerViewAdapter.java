package com.iamchuckss.foodcart.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Ingredient;
import com.iamchuckss.foodcart.models.Recipe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RecipeFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecipeFragmentRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecipeFragmentRvAdapter";

    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Ingredient>> mRecipeIngredientsMap = new HashMap<>();
    private Context mContext;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    public RecipeFragmentRecyclerViewAdapter(ArrayList<Recipe> mRecipeList, HashMap<Integer, ArrayList<Ingredient>> mRecipeIngredientsMap,
                                             Context mContext) {
        this.mRecipeList = mRecipeList;
        this.mRecipeIngredientsMap = mRecipeIngredientsMap;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // widgets
        RelativeLayout parentLayout;
        TextView recipeTitle, recipeWebsite, recipeIngredients;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            recipeTitle = (TextView) itemView.findViewById(R.id.recipe_title);
            recipeWebsite = (TextView) itemView.findViewById(R.id.recipe_website);
            recipeIngredients = (TextView) itemView.findViewById(R.id.recipe_ingredients);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem_recipe, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: A new item is added to the list:" + mRecipeList.get(i).getTitle());

        viewHolder.recipeTitle.setText(mRecipeList.get(i).getTitle()); // set recipe title
        viewHolder.recipeWebsite.setText(mRecipeList.get(i).getWebsite()); // set recipe website
        Log.d(TAG, "onBindViewHolder: mRecipeIngredientsMap: " + mRecipeIngredientsMap);
        ArrayList<Ingredient> recipeIngredients = mRecipeIngredientsMap.get(i);

        ArrayList<String> recipeIngredientsTitles = new ArrayList<>();

        for(Ingredient ingredient : recipeIngredients) {
            // get all ingredients' title
            recipeIngredientsTitles.add(ingredient.getTitle());
        }

        // set recipe ingredients
        viewHolder.recipeIngredients.setText(StringManipulation.arrayToString(recipeIngredientsTitles));

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mRecipeList.get(i));

                Toast.makeText(mContext, mRecipeList.get(i).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

}
