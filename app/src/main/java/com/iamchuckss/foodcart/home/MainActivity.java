package com.iamchuckss.foodcart.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.login.LoginActivity;
import com.iamchuckss.foodcart.models.Ingredient;
import com.iamchuckss.foodcart.models.Recipe;
import com.iamchuckss.foodcart.utils.BottomNavigationViewHelper;
import com.iamchuckss.foodcart.utils.Days;
import com.iamchuckss.foodcart.utils.FirebaseMethods;
import com.iamchuckss.foodcart.utils.MainActivityRecyclerViewAdapter;
import com.iamchuckss.foodcart.utils.PlanActivityRecyclerViewAdapter;
import com.iamchuckss.foodcart.utils.WorkCounter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;

    // vars
    private Context mContext = MainActivity.this;
    private ArrayList<Ingredient> mIngredientsList = new ArrayList<>();
    private HashMap<String, Ingredient> mIngredientsMap = new HashMap<>();

    // widgets
    private ImageView mOptionButton;
    private RecyclerView mRecyclerView;
    private MainActivityRecyclerViewAdapter mAdapter;
    private TextView mEmptyTextView;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOptionButton = findViewById(R.id.option);
        mRecyclerView = findViewById(R.id.recyclerView);
        mEmptyTextView = findViewById(R.id.emptyTextView);

        mFirebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();

        setupBottomNavigationView();
        initOptionButton();

    }

    private void initRecyclerView() {
        mAdapter = new MainActivityRecyclerViewAdapter(mContext, mIngredientsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        initIngredientsList();
    }

    private void initIngredientsList() {
        Log.d(TAG, "populateIngredientList: preparing recipes.");

        mFirebaseMethods.retrievePlan(Days.MONDAY, new PlanRetrievedCallback());
        mFirebaseMethods.retrievePlan(Days.TUESDAY, new PlanRetrievedCallback());
        mFirebaseMethods.retrievePlan(Days.WEDNESDAY, new PlanRetrievedCallback());
        mFirebaseMethods.retrievePlan(Days.THURSDAY, new PlanRetrievedCallback());
        mFirebaseMethods.retrievePlan(Days.FRIDAY, new PlanRetrievedCallback());
        mFirebaseMethods.retrievePlan(Days.SATURDAY, new PlanRetrievedCallback());
        mFirebaseMethods.retrievePlan(Days.SUNDAY, new PlanRetrievedCallback());
    }

    class PlanRetrievedCallback implements FirebaseMethods.firebaseCallback<ArrayList<Recipe>> {

        @Override
        public void onCallback(ArrayList<Recipe> data) {

            if(!data.isEmpty()) {
                mEmptyTextView.setVisibility(View.GONE);
            }

            for(Recipe recipe : data) {
                // retrieve recipe ingredients
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

                            if(mIngredientsMap.containsKey(data.getIngredient_id())) {
                                Ingredient oldIngredient = mIngredientsMap.get(data.getIngredient_id());

                                data.setQuantity(data.getQuantity() + oldIngredient.getQuantity());

                                mIngredientsMap.put(data.getIngredient_id(), data);
                            } else {
                                mIngredientsMap.put(data.getIngredient_id(), data);
                            }

                            if(mIngredientsList.contains(data)) {
                                mIngredientsList.remove(data);
                                mIngredientsList.add(data);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                mIngredientsList.add(data);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }
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
                                // ask for confirmation
                                initConfirmDialog();
                                return true;
                            case R.id.sign_out:
                                mAuth.signOut();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                getMenuInflater().inflate(R.menu.main_option_menu, popup.getMenu());
                popup.show();
            }
        });
    }

    private void initConfirmDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);

        Button mCancelButton = (Button) mView.findViewById(R.id.btn_cancel);
        Button mConfirmButton = (Button) mView.findViewById(R.id.btn_confirm);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseMethods.clearUserPlan();
                dialog.dismiss();
            }
        });
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

    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // check if the user is logged in
                checkCurrentUser(user);

                if(user != null) {         initRecyclerView();
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out: ");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        checkCurrentUser(user);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
