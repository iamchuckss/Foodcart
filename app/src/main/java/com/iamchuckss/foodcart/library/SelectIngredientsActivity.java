package com.iamchuckss.foodcart.library;

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
import com.iamchuckss.foodcart.utils.FirebaseMethods;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectIngredientsActivity extends AppCompatActivity {

    private static final String TAG = "SelectIngredientsActivi";

    // vars
    ArrayList<Ingredient> mIngredientsList = new ArrayList<>();
    ArrayList<Ingredient> mSelectedIngredientsList = new ArrayList<>();
    String currentRecipeId;

    Context mContext = SelectIngredientsActivity.this;

    // widgets
    RecyclerView recyclerView;
    SelectIngredientRvAdapter mAdapter;
    ImageView mBackButton;
    ImageView mDoneButton;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");

        setContentView(R.layout.activity_select_ingredients);

        mFirebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBackButton = (ImageView) findViewById(R.id.backArrow);
        mDoneButton = (ImageView) findViewById(R.id.done_button);

        initializeSelectedIngredientsList();
        initBackButton();
        initDoneButton();
        populateIngredientList();
    }

    private void initializeSelectedIngredientsList() {
        Intent intent = getIntent();
        mSelectedIngredientsList = (ArrayList) intent.getSerializableExtra("selectedIngredients");

        if(mSelectedIngredientsList == null) {
            mSelectedIngredientsList = new ArrayList<>();
        }
    }

    private void populateIngredientList() {
        Log.d(TAG, "populateIngredientList: preparing recipes.");

        initRecyclerView();

        mFirebaseMethods.retrieveUserIngredients( new FirebaseMethods.firebaseCallback<Ingredient>() {
            @Override
            public void onCallback(Ingredient ingredient) {
                Log.d(TAG, "onClick: inserting ingredient into list: " + ingredient);

                // if ingredient is selected, change quantity
                if(!mSelectedIngredientsList.isEmpty()) {

                    for(Ingredient selectedIngredient : mSelectedIngredientsList) {
                        if(ingredient.getTitle().equals(selectedIngredient.getTitle())) {
                            ingredient.setQuantity(selectedIngredient.getQuantity());
                            ingredient.setChecked(true);
                        }
                    }
                }

                mIngredientsList.add(ingredient);
                mAdapter.notifyItemInserted(mIngredientsList.size() - 1);
            }
        });

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");

        mAdapter = new SelectIngredientRvAdapter(mContext, mIngredientsList);

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

                mSelectedIngredientsList.clear();

                // fetch ingredients with quantity
                for(int i = 0; i < mIngredientsList.size(); i++) {
                    if(isIngredientSelected(i)) {
                        mSelectedIngredientsList.add(mIngredientsList.get(i));
                    }
                }
                Log.d(TAG, "onClick: mSelectedIngredientList: " + mSelectedIngredientsList.toString());

                // return result
                Intent intent = new Intent();
                intent.putExtra("selectedIngredients", (Serializable) mSelectedIngredientsList);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    /**
     * Check if an ingredient is selected
     * @param position
     * @return
     */
    private boolean isIngredientSelected(int position) {

        return (mIngredientsList.get(position).getQuantity() != 0);

    }


    /**
     * Custom adapter for displaying an array of ingredients.
     **/
    private class SelectIngredientRvAdapter extends RecyclerView.Adapter<SelectIngredientRvAdapter.ViewHolder> {

        private static final String TAG = "SelectIngredientRvAdptr";

        private ArrayList<Ingredient> mIngredientList = new ArrayList<>();
        private Context mContext;

        // vars
        String inputQuantity;

        public SelectIngredientRvAdapter(Context mContext, ArrayList<Ingredient> ingredientList) {
            this.mIngredientList = ingredientList;
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
            SelectIngredientRvAdapter.ViewHolder holder = new SelectIngredientRvAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            Log.d(TAG, "onBindViewHolder: A new item is added to the list.");

            viewHolder.ingredientTitle.setText(mIngredientList.get(i).getTitle());
            viewHolder.ingredientQuantity.setText(String.valueOf(mIngredientList.get(i).getQuantity()));

            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: clicked on: " + mIngredientList.get(i));

                    // get TextView Quantity for clicked ingredient
                    TextView ingredientQuantity = (TextView) v.findViewById(R.id.ingredient_quantity);

                    // start fragment to ask for quantity
                    initQuantityDialog(ingredientQuantity, i);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mIngredientList.size();
        }

        private void initQuantityDialog(final TextView ingredientQuantity, final int i) {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            View mView = getLayoutInflater().inflate(R.layout.dialog_ingredient_quantity, null);

            final EditText mQuantity = (EditText) mView.findViewById(R.id.input_quantity);
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

                    inputQuantity = mQuantity.getText().toString();
                    // set inputQuantity to input
                    if(!(inputQuantity == null) && !inputQuantity.equals("") && !inputQuantity.equals("0")) {

                        // set TextView to inputQuantity
                        ingredientQuantity.setText(inputQuantity);
                        Log.d(TAG, "onClick: IngredientQuantity = " + ingredientQuantity.getText().toString());

                        // set ingredient's quantity to inputQuantity
                        mIngredientList.get(i).setQuantity(Integer.parseInt(inputQuantity));

                        // check ingredient
                        mIngredientList.get(i).setChecked(true);

                    } else {
                        ingredientQuantity.setText("0");

                        // set ingredient's quantity to 0
                        mIngredientList.get(i).setQuantity(0);

                        // uncheck ingredient
                        mIngredientList.get(i).setChecked(false);
                    }

                    Log.d(TAG, "onClick: " + mIngredientList.get(i));

                    dialog.dismiss();
                }
            });
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
