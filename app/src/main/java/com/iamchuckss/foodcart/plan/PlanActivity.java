package com.iamchuckss.foodcart.plan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Recipe;
import com.iamchuckss.foodcart.utils.BottomNavigationViewHelper;
import com.iamchuckss.foodcart.utils.Days;
import com.iamchuckss.foodcart.utils.FirebaseMethods;
import com.iamchuckss.foodcart.utils.PlanActivityRecyclerViewAdapter;
import com.iamchuckss.foodcart.utils.WorkCounter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.TreeMap;

public class PlanActivity extends AppCompatActivity{

    private static final String TAG = "PlanActivity";
    private static final int ACTIVITY_NUM = 1;
    private static final int SELECT_RECIPE_REQUEST_CODE = 11;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    // vars
    TreeMap<Integer, ArrayList<Recipe>> mRecipeList = new TreeMap<>();
    Context mContext = PlanActivity.this;

    // widgets
    RecyclerView mRecyclerView;
    PlanActivityRecyclerViewAdapter mAdapter;
    Toolbar mToolbar;
    ImageView mOptionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        mFirebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();

        mToolbar = (Toolbar) findViewById(R.id.plan_toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mOptionButton = (ImageView) findViewById(R.id.option);

        initOptionButton();
        setupBottomNavigationView();
        initRecyclerView();

    }

    private void initRecyclerView() {
        initRecipeList();
        mAdapter = new PlanActivityRecyclerViewAdapter(mContext, this,
                mRecipeList);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void initRecipeList() {

        final WorkCounter workCounter = new WorkCounter(7, mContext);

        mFirebaseMethods.retrievePlan(Days.MONDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.MONDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        mFirebaseMethods.retrievePlan(Days.TUESDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.TUESDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        mFirebaseMethods.retrievePlan(Days.WEDNESDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.WEDNESDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        mFirebaseMethods.retrievePlan(Days.THURSDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.THURSDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        mFirebaseMethods.retrievePlan(Days.FRIDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.FRIDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        mFirebaseMethods.retrievePlan(Days.SATURDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.SATURDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        mFirebaseMethods.retrievePlan(Days.SUNDAY, new FirebaseMethods.firebaseCallback<ArrayList<Recipe>>() {
            @Override
            public void onCallback(ArrayList<Recipe> data) {
                mRecipeList.put(Days.SUNDAY, data);
                workCounter.taskFinished();
                Log.d(TAG, "onCallback: workCounter: " + workCounter.getRunningTasks());
            }
        });

        // if all task done, update UI
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this.mContext);
        mgr.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: broadcast received.");
                mAdapter.notifyDataSetChanged();
            }
        }, new IntentFilter(mContext.getString(R.string.task_finished_broadcast)));
    }

    private void initOptionButton() {

        mOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.clear_plan:
                                // TODO: add clear plan function
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                getMenuInflater().inflate(R.menu.plan_option_menu, popup.getMenu());
                popup.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_RECIPE_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: done selecting recipes");
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: result is valid");

                // get which day
                int currentDay = data.getIntExtra("currentDay", 10);
                ArrayList<Recipe> selectedRecipeList = (ArrayList) (data.getSerializableExtra("selectedRecipes"));

                if(selectedRecipeList == null) {
                    selectedRecipeList = new ArrayList<>();
                }

                // get selected recipes list
                if(currentDay >= 0 && currentDay <= 6) {
                    mRecipeList.put(currentDay, selectedRecipeList);

                    Log.d(TAG, "onActivityResult: mRecipeList: " + mRecipeList.get(currentDay));
                    Log.d(TAG, "onActivityResult: Selected recipes: " + selectedRecipeList);

                    // adapt recipes onto mRecipeList
                    updateRecipeList(currentDay, selectedRecipeList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void updateRecipeList(int pos, ArrayList<Recipe> selectedRecipes) {
        // get ViewHolder for selected day
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(pos);
        View view = viewHolder.itemView;
        TextView recipeList = (TextView) view.findViewById(R.id.recipe_list);

        Log.d(TAG, "updateRecipeList: selectedRecipes: " + selectedRecipes);

        recipeList.setText("");

        // get recipes' title from list
        for(Recipe recipe : selectedRecipes) {
            recipeList.append(recipe.getTitle());
            recipeList.append("\n");
        }

    }

    /**
     * BottomNavigationView Setup
     *
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
/*
    -----------------------------------Firebase-----------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out: ");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
