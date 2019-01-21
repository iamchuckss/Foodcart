package com.iamchuckss.foodcart.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.library.SelectIngredientsActivity;
import com.iamchuckss.foodcart.models.Ingredient;

import java.util.ArrayList;

public class AddRecipeIngredientsListRecyclerViewAdapter extends RecyclerView.Adapter<AddRecipeIngredientsListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "AddRecipeIngredientsLis";

    ArrayList<Ingredient> mSelectedIngredientsList = new ArrayList<>();
    private Context mContext;

    public AddRecipeIngredientsListRecyclerViewAdapter(Context mContext, ArrayList<Ingredient> ingredientList) {
        this.mSelectedIngredientsList = ingredientList;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // widgets
        RelativeLayout parentLayout;
        TextView ingredientTitle;
        TextView ingredientQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            ingredientTitle = (TextView) itemView.findViewById(R.id.ingredient_title);
            ingredientQuantity = (TextView) itemView.findViewById(R.id.ingredient_quantity);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem_add_recipe_ingredient, viewGroup, false);
        AddRecipeIngredientsListRecyclerViewAdapter.ViewHolder holder = new AddRecipeIngredientsListRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: A new item is added to the list.");

        viewHolder.ingredientTitle.setText(mSelectedIngredientsList.get(i).getTitle());
        viewHolder.ingredientQuantity.setText(String.valueOf(mSelectedIngredientsList.get(i).getQuantity()));

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start EditIngredientListActivity

            }
        });
    }

    @Override
    public int getItemCount() {
        return mSelectedIngredientsList.size();
    }
}
