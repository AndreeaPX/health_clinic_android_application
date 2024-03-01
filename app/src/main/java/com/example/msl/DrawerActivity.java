package com.example.msl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        FrameLayout container = drawerLayout.findViewById(R.id.FrameLayout_container);
        container.addView(view);
        super.setContentView(drawerLayout);
        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        NavigationView navigationView = drawerLayout.findViewById(R.id.navigationView_menu);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userText = (TextView) headerView.findViewById(R.id.user_text);
        userText.setText(getSharedPreferences(LoginActivity.SHAREDPREFERENCES,MODE_PRIVATE).getString("email",""));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {

            case R.id.home_menu: {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                break;
            }
            case R.id.profile_menu: {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;
            }
            case R.id.logOut_menu:{
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                overridePendingTransition(0,0);
                break;
            }
            case R.id.status_menu:{
                startActivity(new Intent(this, StatusActivity.class));
                overridePendingTransition(0,0);
                break;
            }
            case R.id.treatment_menu:{
                startActivity(new Intent(this,TreatmentActivity.class));
                overridePendingTransition(0,0);
                break;
            }

            case R.id.medicalJournal_menu:{
                startActivity(new Intent(this, FilesActivity.class));
                overridePendingTransition(0,0);
                break;
            }

            case R.id.history_menu:{
                startActivity(new Intent(this,HistoryActivity.class));
                overridePendingTransition(0,0);
                break;
            }
        }
        return false;
    }

    protected void generateActivityTitle(String activityName){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(activityName);
        }
    }
}