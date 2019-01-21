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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Recipe;
import com.iamchuckss.foodcart.plan.SelectRecipeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PlanActivityRecyclerViewAdapter extends RecyclerView.Adapter<PlanActivityRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "PlanActivityRecyclerVie";

    private static final int SELECT_RECIPE_REQUEST_CODE = 11;

    private Context mContext;
    private Activity mActivity;
    private TreeMap<Integer, ArrayList<Recipe>> mRecipeList;

    public PlanActivityRecyclerViewAdapter(Context mContext, Activity mActivity, TreeMap<Integer, ArrayList<Recipe>> mRecipeList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mRecipeList = mRecipeList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // widgets
        RelativeLayout mParentLayout;
        TextView mDayTitle;
        TextView mRecipeList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mParentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            mDayTitle = (TextView) itemView.findViewById(R.id.day_title);
            mRecipeList = (TextView) itemView.findViewById(R.id.recipe_list);
        }

        public void initRecipeList(ArrayList<Recipe> recipesList) {
            Log.d(TAG, "initRecipeList: initializing mRecipeList");

            mRecipeList.setText("");

            for(Recipe recipe : recipesList) {
                mRecipeList.append(recipe.getTitle());
                mRecipeList.append("\n");
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem_plan, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: A new item is added to the list.");

        // obtain day and list of selected recipes from map
        final int day = getTreeMapKeyFromIndex(i);
        final ArrayList<Recipe> selectedRecipes = mRecipeList.get(day);

        if(selectedRecipes != null) {
            viewHolder.initRecipeList(selectedRecipes);
        }

        viewHolder.mDayTitle.setText(Days.getString(day));

        viewHolder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + Days.getString(getTreeMapKeyFromIndex(i)));

                // start select recipes activity
                Intent intent = new Intent(mContext, SelectRecipeActivity.class);
                intent.putExtra("currentDay", day);
                intent.putExtra("currentSelectedRecipes", selectedRecipes);
                mActivity.startActivityForResult(intent, SELECT_RECIPE_REQUEST_CODE);

                Log.d(TAG, "onClick: navigating to SelectRecipeActivity");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    private int getTreeMapKeyFromIndex(int i) {
        return (int) mRecipeList.keySet().toArray()[i];
    }
}
