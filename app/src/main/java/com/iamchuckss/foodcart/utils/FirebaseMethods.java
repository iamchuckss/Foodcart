package com.iamchuckss.foodcart.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.models.Ingredient;
import com.iamchuckss.foodcart.models.Recipe;
import com.iamchuckss.foodcart.models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;

    private Context mContext;

    public FirebaseMethods(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        if(mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    /**
     * Register a new email and password to Firebase authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // send verification email
                            sendVerificationEmail();

                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = user.getUid();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "Couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Add infromation to the users node
     *
     * @param email
     * @param username
     */
    public void addNewUser(String email, String username) {
        User user = new User(userID, email, username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);
    }

    /**
     * Add new ingredient to the user_ingredients node
     *
      * @param title
     * @return ingredient_id of added ingredient
     */
    public String addNewIngredient(String title) {

        Log.d(TAG, "addNewIngredient: adding new ingredient: " + title);

        // push to create new entry
        DatabaseReference newRef = myRef.child(mContext.getString(R.string.db_name_user_ingredients))
                .child(userID)
                .push();

        // insert new ingredient
        newRef.setValue(new Ingredient(newRef.getKey(), title));

        return newRef.getKey();
    }

    /**
     * Retrieve ingredient from database by id
     *
     * @param id
     */
    public void getIngredient(String id, final firebaseCallback<Ingredient> callback) {

        Log.d(TAG, "getIngredient: querying database..");
        
        final Query query = myRef.child(mContext.getString(R.string.db_name_user_ingredients))
                .child(userID)
                .orderByKey()
                .equalTo(id);

        // return a datasnapshot only if a match is found
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                        callback.onCallback(singleSnapshot.getValue(Ingredient.class));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Retrieve user's ingredients from database
     *
     */
    public void retrieveUserIngredients(final firebaseCallback<Ingredient> callback) {

        final Query query = myRef.child(mContext.getString(R.string.db_name_user_ingredients))
                .child(userID);

        // return a datasnapshot only if a match is found
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                        callback.onCallback(singleSnapshot.getValue(Ingredient.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Add new recipe to the user_recipes node
     *
      * @param recipe
     * @return recipe_id of added recipe
     */
    public String addNewRecipe(Recipe recipe) {

        Log.d(TAG, "addNewRecipe: adding new recipe: " + recipe.getTitle());

        // push to create new entry
        DatabaseReference newRef = myRef.child(mContext.getString(R.string.dbname_user_recipes))
                .child(userID)
                .push();

        // set recipe_id
        recipe.setRecipe_id(newRef.getKey());

        // insert new recipe
        newRef.setValue(recipe);

        return recipe.getRecipe_id();
    }

    /**
     * Retrieve user's recipes from database
     *
     */
    public void retrieveUserRecipes(final firebaseCallback<Recipe> callback) {

        final Query query = myRef.child(mContext.getString(R.string.dbname_user_recipes))
                .child(userID);

        // return a datasnapshot only if a match is found
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                        callback.onCallback(singleSnapshot.getValue(Recipe.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Add new plan to user_plan node
     * @param day
     * @param recipeList
     */
    public void addPlan(int day, ArrayList<Recipe> recipeList) {

        Log.d(TAG, "savePlan: Adding new plan to: " + Days.getString(day) + ": " + recipeList);

        String dayField = Days.getDatabaseField(day);

        myRef.child(mContext.getString(R.string.db_name_user_plan))
                .child(userID)
                .child(dayField)
                .setValue(recipeList);

        Log.d(TAG, "addPlan: plan added for: " + Days.getString(day));
    }

    public void retrievePlan(int day, final firebaseCallback<ArrayList<Recipe>> callback) {

        final ArrayList<Recipe> recipeList = new ArrayList<>();
        Log.d(TAG, "retrievePlan: getting plan for: " + Days.getString(day));

        final String dayField = Days.getDatabaseField(day);

        Query query = myRef.child(mContext.getString(R.string.db_name_user_plan))
                .child(userID)
                .child(dayField);

        // return a datasnapshot only if a match is found
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                        recipeList.add(singleSnapshot.getValue(Recipe.class));
                    }
                    Log.d(TAG, "onDataChange: Plan retrieved for: " + dayField + ": " + recipeList);
                }
                callback.onCallback(recipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    public void clearUserPlan(){
        Log.d(TAG, "clearUserPlan: clearing plan for: " + userID);

        myRef.child("user_plan")
                .child(userID)
                .removeValue();

        Log.d(TAG, "clearUserPlan: plan cleared for: " + userID);
    }


    public interface firebaseCallback<T> {
        public void onCallback(T data);
    }
}
