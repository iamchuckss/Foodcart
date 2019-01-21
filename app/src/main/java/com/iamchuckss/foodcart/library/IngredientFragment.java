package com.iamchuckss.foodcart.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.iamchuckss.foodcart.utils.FirebaseMethods;
import com.iamchuckss.foodcart.utils.IngredientFragmentRecyclerViewAdapter;
import com.iamchuckss.foodcart.utils.RecipeFragmentRecyclerViewAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class IngredientFragment extends Fragment {
    private static final String TAG = "IngredientFragment";

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    // vars
    private ArrayList<Ingredient> mIngredientsList = new ArrayList<>();

    Context mContext;

    // widgets
    RecyclerView recyclerView;
    CircleImageView addIngredientButton;
    TextView mEmptyTextView;

    IngredientFragmentRecyclerViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        Log.d(TAG, "onCreateView: started.");

        mContext = getActivity();
        recyclerView = view.findViewById(R.id.recyclerView);
        addIngredientButton = (CircleImageView) view.findViewById(R.id.btnAddIngredient);
        mEmptyTextView = view.findViewById(R.id.emptyTextView);
        mFirebaseMethods = new FirebaseMethods(mContext);

        setupFirebaseAuth();
        initAddIngredientButton();
        initIngredients();

        return view;
    }

    private void initIngredients() {
        Log.d(TAG, "populateIngredientList: retrieving data from firebase for: " + mAuth.getCurrentUser().getUid());

        initRecyclerView();

        mFirebaseMethods.retrieveUserIngredients( new FirebaseMethods.firebaseCallback<Ingredient>() {
                    @Override
                    public void onCallback(Ingredient ingredient) {
                        Log.d(TAG, "onClick: inserting ingredient into list: " + ingredient);
                        mIngredientsList.add(ingredient);
                        mAdapter.notifyItemInserted(mIngredientsList.size() - 1);

                        if(!mIngredientsList.isEmpty()) {
                            mEmptyTextView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        mAdapter = new IngredientFragmentRecyclerViewAdapter(
                mIngredientsList, mContext);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void initAddIngredientButton() {
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting AddIngredientDialog");
                initAddIngredientDialog();
            }
        });
    }

    private void initAddIngredientDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_ingredient, null);

        final EditText mIngredientTitle = (EditText) mView.findViewById(R.id.ingredient_title);
        Button mCancelButton = (Button) mView.findViewById(R.id.btn_cancel);
        Button mDoneButton = (Button) mView.findViewById(R.id.btn_done);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userInput = mIngredientTitle.getText().toString();

                // set inputQuantity to input
                if(!(userInput == null) && !userInput.equals("") && !userInput.equals(" ")) {

                    Log.d(TAG, "onClick: adding new ingredient to database.");
                    String ingredientId = mFirebaseMethods.addNewIngredient(userInput);

                    Log.d(TAG, "onClick: retrieving added ingredient from database.");
                    mFirebaseMethods.getIngredient(ingredientId
                            , new FirebaseMethods.firebaseCallback<Ingredient>() {
                                @Override
                                public void onCallback(Ingredient ingredient) {
                                    Log.d(TAG, "onCallback: inserting added ingredient into list: " + ingredient);
                                    mIngredientsList.add(ingredient);
                                    Log.d(TAG, "onCallback: " + mIngredientsList);
                                    mAdapter.notifyItemInserted(mIngredientsList.size() - 1);
                                }
                            });
                }
                dialog.dismiss();
            }
        });
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

                // retrieve user information from database
                // setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                // retrieve images for the user in question
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
