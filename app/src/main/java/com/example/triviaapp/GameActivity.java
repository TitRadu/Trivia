package com.example.triviaapp;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.triviaapp.ui.dashboard.DashboardFragment;
import com.example.triviaapp.ui.home.HomeFragment;
import com.example.triviaapp.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").hide(activeFragment).show(homeFragment).commitNow();
                activeFragment = homeFragment;
                return true;

            case R.id.navigation_dashboard:
                fragmentManager.beginTransaction().remove(dashboardFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboardFragment, "2").hide(activeFragment).show(dashboardFragment).commitNow();
                activeFragment = dashboardFragment;
                return true;

            case R.id.navigation_notifications:
                fragmentManager.beginTransaction().remove(notificationsFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, notificationsFragment, "3").hide(activeFragment).show(notificationsFragment).commitNow();
                activeFragment = notificationsFragment;
                return true;
        }
        return false;
    }

    private void LoadFragment() {
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, notificationsFragment, "3").hide(notificationsFragment).commitNow();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboardFragment, "2").hide(dashboardFragment).commitNow();
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").commitNow();
    }

}