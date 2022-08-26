package com.deliyummy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        navigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(BaseActivity.this,
                        R.anim.fade_in, R.anim.fade_out).toBundle();
                int itemId = item.getItemId();
                if (itemId == R.id.home_screen) {
                    BaseActivity.this.startActivity(new Intent(BaseActivity.this, HomeActivity.class),bundle);

                } else if (itemId == R.id.search_screen) {
                    BaseActivity.this.startActivity(new Intent(BaseActivity.this, SearchActivity.class),bundle);

                } else if (itemId == R.id.cart_screen) {
                    BaseActivity.this.startActivity(new Intent(BaseActivity.this, CartActivity.class),bundle);

                } else if (itemId == R.id.account_screen) {
                    BaseActivity.this.startActivity(new Intent(BaseActivity.this, OrderActivity.class),bundle);

                }
                BaseActivity.this.finish();
            }
        }, 300);
        return true;
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(BaseActivity.this)
                .setMessage("Are you sure you want to remove?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

}
