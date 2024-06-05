package com.example.serega;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RRR";
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Dialog infoDialog;
    private static final String[] adminMail = {
            "eab@gmail.com", // 111111
            "iir@gmail.com" // 111111
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started");
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawer, R.string.drawer_open, R.string.drawer_close);

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                if (drawer != null) {
                    drawer.addDrawerListener(toggle);
                }
                toggle.syncState();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                // User is signed out
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        };

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.info_item, null));
        infoDialog = builder.create();
        Log.e(TAG, "Dialog problem");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    public static String convertStringToNumberAndSubtractOne(String str) {
        str = str.replaceFirst("^0+", "");
        int num = Integer.parseInt(str);
        num -= 1;

        return Integer.toString(num);
    }

    public static boolean isAdmin(String candidate) {
        for (String s : adminMail) {
            if (s.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.accountFragment);
        } else if(id == R.id.nav_dev) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.developmentDivisionFragment);
        } else if(id == R.id.nav_testing) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.testingDevisionFragment);
        } else if(id == R.id.nav_user_rel) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.userRelationsDivisionFragment);
        } else if( id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.loginFragment);
        } else if(id == R.id.nav_info) {
            infoDialog.show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

