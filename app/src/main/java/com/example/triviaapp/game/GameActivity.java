package com.example.triviaapp.game;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;
import com.example.triviaapp.User;
import com.example.triviaapp.game.ui.dashboard.DashboardFragment;
import com.example.triviaapp.game.ui.home.HomeFragment;
import com.example.triviaapp.game.ui.notifications.NotificationsFragment;
import com.example.triviaapp.rank.Rank;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Comparator;

public class GameActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView navView;
    private HomeFragment homeFragment;
    private DashboardFragment dashboardFragment;
    private NotificationsFragment notificationsFragment;
    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializeViews();
        LoadFragment();

    }

    private void initializeViews(){
        navView = findViewById(R.id.nav_view);
        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationsFragment = new NotificationsFragment();
        activeFragment = homeFragment;
        navView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragmentManager.beginTransaction().remove(homeFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;

            case R.id.navigation_dashboard:
                fragmentManager.beginTransaction().remove(dashboardFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboardFragment, "2").hide(activeFragment).show(dashboardFragment).commit();
                activeFragment = dashboardFragment;
                return true;

            case R.id.navigation_notifications:
                fragmentManager.beginTransaction().remove(notificationsFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, notificationsFragment, "3").hide(activeFragment).show(notificationsFragment).commit();
                activeFragment = notificationsFragment;
                return true;
        }
        return false;

    }

    private void LoadFragment() {
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, notificationsFragment, "3").hide(notificationsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboardFragment, "2").hide(dashboardFragment).commit();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").commit();

    }

}