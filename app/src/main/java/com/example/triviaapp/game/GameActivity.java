package com.example.triviaapp.game;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.ui.dashboard.DashboardFragment;
import com.example.triviaapp.game.ui.home.HomeFragment;
import com.example.triviaapp.game.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

public class GameActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView navView;
    private HomeFragment homeFragment;
    private DashboardFragment dashboardFragment;
    private NotificationsFragment notificationsFragment;
    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    MenuItem profile, score, game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializeViews();
        LoadFragment();
        LoggedUserData.language.observeForever(s -> { chooseLanguage(); });

    }

    private void initializeViews(){
        navView = findViewById(R.id.nav_view);
        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationsFragment = new NotificationsFragment();
        activeFragment = homeFragment;
        navView.setOnNavigationItemSelectedListener(this);
        profile = navView.getMenu().findItem(R.id.navigation_profile);
        score = navView.getMenu().findItem(R.id.navigation_score);
        game = navView.getMenu().findItem(R.id.navigation_game);
        chooseLanguage();

    }

    private void setMenuItemsForEnglishLanguage(){
        profile.setTitle(R.string.title_profileEn);
        score.setTitle(R.string.title_scoreEn);
        game.setTitle(R.string.title_gameEn);

    }

    private void setMenuItemsForRomanianLanguage(){
        profile.setTitle(R.string.title_profileRou);
        score.setTitle(R.string.title_scoreRou);
        game.setTitle(R.string.title_gameRou);

    }

    private void chooseLanguage(){
        switch (LoggedUserData.language.getValue()){
            case "english":
                setMenuItemsForEnglishLanguage();
                break;
            case "romanian":
                setMenuItemsForRomanianLanguage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language.getValue());
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                fragmentManager.beginTransaction().remove(homeFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;

            case R.id.navigation_score:
                fragmentManager.beginTransaction().remove(dashboardFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboardFragment, "2").hide(activeFragment).show(dashboardFragment).commit();
                activeFragment = dashboardFragment;
                return true;

            case R.id.navigation_game:
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