package com.iamchuckss.foodcart.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.iamchuckss.foodcart.utils.FirebaseMethods;
import com.iamchuckss.foodcart.utils.RecipeFragmentRecyclerViewAdapter;
import com.iamchuckss.foodcart.utils.WorkCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class RecipeFragment extends Fragment {
    private static final String TAG = "RecipeFragment";

    // vars
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Ingredient>> mRecipeIngredientsMap = new HashMap<>();
    private static int mapCounter = 0;
    final private static int ADD_RECIPE_REQUEST_CODE = 11;

    Context mContext = getActivity();

    // widgets
    RecyclerView recyclerView;
    RecipeFragmentRecyclerViewAdapter mAdapter;
    CircleImageView addRecipeButton;
    TextView mEmptyTextView;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        Log.d(TAG, "onCreateView: started.");

        mContext = getActivity();

        mFirebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();

        recyclerView = view.findViewById(R.id.recyclerView);
        addRecipeButton = (CircleImageView) view.findViewById(R.id.btnAddRecipe);
        mEmptyTextView = view.findViewById(R.id.emptyTextView);

        initAddRecipeButton();
        initRecyclerView();
        initRecipes();

        return view;
    }

    private void initRecipes() {
        Log.d(TAG, "initRecipes: preparing recipes.");

        mapCounter = 0;

        mFirebaseMethods.retrieveUserRecipes( new FirebaseMethods.firebaseCallback<Recipe>() {
            @Override
            public void onCallback(final Recipe recipe) {

                if(!mRecipeList.isEmpty()) {
                    mEmptyTextView.setVisibility(View.GONE);
                }

                Log.d(TAG, "onCallback: inserting recipe into list: " + recipe);

                // retrive recipe ingredients
                HashMap<String, Integer> recipeIngredientsMap = recipe.getIngredients();

                final ArrayList<Ingredient> recipeIngredients = new ArrayList<Ingredient>();
                // convert map to ArrayList
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
                mRecipeList.add(recipe);

            }
        });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        mAdapter = new RecipeFragmentRecyclerViewAdapter(mRecipeList,
                mRecipeIngredientsMap,
                mContext);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void initAddRecipeButton() {
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to AddRecipeActivity.");
                Intent intent = new Intent(mContext, AddRecipeActivity.class);
                startActivityForResult(intent, ADD_RECIPE_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_RECIPE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: RESULT_OK, recipe added.");
                mRecipeIngredientsMap.clear();
                mRecipeList.clear();
                mapCounter = 0;
                initRecipes();
            }
        }
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
