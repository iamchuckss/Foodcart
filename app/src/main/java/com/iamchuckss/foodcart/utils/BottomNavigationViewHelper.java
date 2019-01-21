package com.iamchuckss.foodcart.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.iamchuckss.foodcart.R;
import com.iamchuckss.foodcart.home.MainActivity;
import com.iamchuckss.foodcart.plan.PlanActivity;
import com.iamchuckss.foodcart.library.LibraryActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHlp";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.ic_shopping_cart:
                        Intent intent1 = new Intent(context, MainActivity.class); // ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_meal:
                        Intent intent2 = new Intent(context, PlanActivity.class); // ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_library:
                        Intent intent3 = new Intent(context, LibraryActivity.class); // ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        break;
                }
                return false;
            }
        });
    }
}
