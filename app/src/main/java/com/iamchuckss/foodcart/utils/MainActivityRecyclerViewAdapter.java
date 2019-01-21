package com.iamchuckss.foodcart.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Ingredient;
import com.iamchuckss.foodcart.models.Recipe;
import com.iamchuckss.foodcart.plan.SelectRecipeActivity;

import java.util.ArrayList;
import java.util.TreeMap;

public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MainActivityRecyclerVie";

    // vars
    private Context mContext;
    private ArrayList<Ingredient> mIngredientsList;

    public MainActivityRecyclerViewAdapter(Context mContext, ArrayList<Ingredient> mIngredientsList) {
        this.mContext = mContext;
        this.mIngredientsList = mIngredientsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // widgets
        RelativeLayout mParentLayout;
        TextView mIngredientTitle;
        TextView mIngredientQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mParentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            mIngredientTitle = (TextView) itemView.findViewById(R.id.ingredient_title);
            mIngredientQuantity = (TextView) itemView.findViewById(R.id.ingredient_quantity);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem_main_ingredient, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: A new item is added to the list.");

        viewHolder.mIngredientTitle.setText(mIngredientsList.get(i).getTitle());
        viewHolder.mIngredientQuantity.setText(String.valueOf(mIngredientsList.get(i).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }

}
