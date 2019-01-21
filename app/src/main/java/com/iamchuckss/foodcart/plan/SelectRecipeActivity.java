package com.iamchuckss.foodcart.plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Ingredient;
import com.iamchuckss.foodcart.models.Recipe;
import com.iamchuckss.foodcart.utils.Days;
import com.iamchuckss.foodcart.utils.FirebaseMethods;
import com.iamchuckss.foodcart.utils.SelectRecipeRecyclerViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectRecipeActivity extends AppCompatActivity {

    private static final String TAG = "SelectRecipeActivity";

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    // vars
    ArrayList<Recipe> mRecipeList = new ArrayList<>();
    ArrayList<Recipe> mSelectedRecipeList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Ingredient>> mRecipeIngredientsMap = new HashMap<>();
    private static int mapCounter = 0;
    int currentDay;

    Context mContext = SelectRecipeActivity.this;

    // widgets
    RecyclerView recyclerView;
    SelectRecipeRecyclerViewAdapter mAdapter;
    ImageView mBackButton;
    ImageView mDoneButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");

        setContentView(R.layout.activity_select_recipe);

        mFirebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();

        // retrieve currentDay
        Intent intent = getIntent();
        currentDay = intent.getIntExtra("currentDay", 10);
        mSelectedRecipeList = (ArrayList) intent.getSerializableExtra("currentSelectedRecipes");
        Log.d(TAG, "onCreate: selected recipes retrieved: " +mSelectedRecipeList);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBackButton = (ImageView) findViewById(R.id.backArrow);
        mDoneButton = (ImageView) findViewById(R.id.done_button);

        initBackButton();
        initDoneButton();
        populateRecipeList();
    }

    private void populateRecipeList() {
        initRecyclerView();

        mapCounter = 0;

        mFirebaseMethods.retrieveUserRecipes( new FirebaseMethods.firebaseCallback<Recipe>() {
            @Override
            public void onCallback(final Recipe recipe) {
                Log.d(TAG, "onCallback: inserting recipe into list: " + recipe);

                // retrive recipe ingredients
                HashMap<String, Integer> recipeIngredientsMap = recipe.getIngredients();

                // convert map to ArrayList
                final ArrayList<Ingredient> recipeIngredients = new ArrayList<Ingredient>();
                for(final Map.Entry<String, Integer> item : recipeIngredientsMap.entrySet()) {
                    Log.d(TAG, "onCallback: retrieving ingredient with id: " + item.getKey());
                    // get ingredients from ingredient_id
                    mFirebaseMethods.getIngredient(item.getKey(), new FirebaseMethods.firebaseCallback<Ingredient>() {
                        @Override
                        public void onCallback(Ingredient data) {
                            Log.d(TAG, "onCallback2: retrieved ingredient: " + data);
                            data.setQuantity(item.getValue());
                            recipeIngredients.add(data);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
                mRecipeIngredientsMap.put(mapCounter, recipeIngredients);
                Log.d(TAG, "onCallback: " + mRecipeIngredientsMap);
                ++mapCounter;

                // if recipe is selected, check
                if(!mSelectedRecipeList.isEmpty()) {

                    for(Recipe selectedRecipe : mSelectedRecipeList) {
                        if(recipe.getTitle().equals(selectedRecipe.getTitle())) {
                            recipe.setChecked(true);
                        }
                    }
                }
                mRecipeList.add(recipe);
            }
        });

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

         mAdapter = new SelectRecipeRecyclerViewAdapter(
                mRecipeList,
                 mRecipeIngredientsMap,
                 mContext);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    /**
     * Return to previous activity.
     */
    private void initBackButton() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Confirm unsaved changes
                finish();
            }
        });
    }

    /**
     * Return list of selected ingredients to previous activity on button click
     */
    private void initDoneButton() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSelectedRecipeList = new ArrayList<>();

                // fetch checked recipes
                for(int i = 0; i < mRecipeList.size(); i++) {
                    if(isRecipeSelected(i)) {
                        mSelectedRecipeList.add(mRecipeList.get(i));
                    }
                }
                Log.d(TAG, "onClick: mSelectedRecipeList: " + mSelectedRecipeList.toString());

                Intent intent = new Intent();
                intent.putExtra("currentDay", currentDay);
                intent.putExtra("selectedRecipes", (Serializable) mSelectedRecipeList);
                setResult(RESULT_OK, intent);
                mFirebaseMethods.addPlan(currentDay, mSelectedRecipeList);

                finish();
            }
        });
    }

    /**
     * Check if a recipe is selected
     * @param position
     * @return
     */
    private boolean isRecipeSelected(int position) {

        return mRecipeList.get(position).isChecked();
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
