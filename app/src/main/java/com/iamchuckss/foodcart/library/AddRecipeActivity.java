package com.iamchuckss.foodcart.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import com.iamchuckss.foodcart.utils.AddRecipeIngredientsListRecyclerViewAdapter;
import com.iamchuckss.foodcart.utils.FirebaseMethods;
import com.iamchuckss.foodcart.utils.IngredientsListHandler;

import java.io.Serializable;
import java.util.ArrayList;

public class AddRecipeActivity extends AppCompatActivity {

    private static final String TAG = "AddRecipeActivity";

    Context mContext = AddRecipeActivity.this;

    private ArrayList<Ingredient> mSelectedIngredientsList = new ArrayList<>();
    
    // constants
    private static final int SELECT_INGREDIENTS_REQUEST_CODE = 10;

    // vars
    EditText mRecipeName;
    EditText mRecipeWebsite;
    RecyclerView mIngredientsList;
    ImageView mAddIngredientButton, mDoneButton, mBackButton;
    private String recipeTitle, recipeWebsite;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mFirebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();

        initWidgets();
        initAddIngredientButton();
        initBackButton();
        initDoneButton();
    }

    /**
     * Return to previous activity.
     */
    private void initBackButton() {
        // TODO: popup dialog to confirm if changes made
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Add and save recipe to database
     */
    private void initDoneButton() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recipeTitle = mRecipeName.getText().toString();
                recipeWebsite = mRecipeWebsite.getText().toString();

                if(checkInputs(recipeTitle, mSelectedIngredientsList)) {

                    Recipe newRecipe = new Recipe(recipeTitle, recipeWebsite,
                            IngredientsListHandler.convertIngredientsList(mSelectedIngredientsList));

                    Log.d(TAG, "onClick: adding recipe to database: "+ newRecipe);

                    if(mFirebaseMethods.addNewRecipe(newRecipe) != null) {
                        // return result
                        Log.d(TAG, "onClick: RESULT_OK");
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }


            }
        });
    }

    private boolean checkInputs(String recipeName, ArrayList<Ingredient> ingredientsList) {
        Log.d(TAG, "checkInputs: checking inputs for null values");
        if(recipeName.equals("") || (ingredientsList == null) || (ingredientsList.isEmpty())) {
            Toast.makeText(mContext, "Name and ingredients cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Initialize the activity widgets
     */
    private void initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets.");

        mRecipeName = (EditText) findViewById(R.id.recipe_name);
        mRecipeWebsite = (EditText) findViewById(R.id.recipe_website);
        mIngredientsList = (RecyclerView) findViewById(R.id.ingredientsList);
        mAddIngredientButton = (ImageView) findViewById(R.id.add_ingredient_button);
        mDoneButton = (ImageView) findViewById(R.id.btn_save);
        mBackButton = (ImageView) findViewById(R.id.backArrow);
    }

    private void initIngredientsList() {


    }

    private void initAddIngredientButton() {

        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to SelectIngredientsActivity");
                Intent intent = new Intent(mContext, SelectIngredientsActivity.class);
                // if mIngredientsList is not empty, attach in intent
                if(!mSelectedIngredientsList.isEmpty()) {
                    intent.putExtra("selectedIngredients", (Serializable) mSelectedIngredientsList);
                }
                startActivityForResult(intent, SELECT_INGREDIENTS_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == SELECT_INGREDIENTS_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: done selecting ingredients ");
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: result is valid");
                mSelectedIngredientsList = (ArrayList) (data.getSerializableExtra("selectedIngredients"));
                if(mSelectedIngredientsList == null) {
                    mSelectedIngredientsList = new ArrayList<>();
                }
                Log.d(TAG, "onActivityResult: Selected ingredients: " + mSelectedIngredientsList);
                // adapt ingredients onto mIngredientsList
                refreshIngredientsListRecyclerView();
            }
        }
    }

    private void refreshIngredientsListRecyclerView() {
        Log.d(TAG, "initIngredientsListRecyclerView: init recyclerView");

        AddRecipeIngredientsListRecyclerViewAdapter adapter = new AddRecipeIngredientsListRecyclerViewAdapter(
                mContext, mSelectedIngredientsList);

        mIngredientsList.setAdapter(adapter);
        mIngredientsList.setLayoutManager(new LinearLayoutManager(mContext));
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
