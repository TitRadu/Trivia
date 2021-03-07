package com.example.triviaapp.game;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.ui.dashboard.DashboardFragment;
import com.example.triviaapp.game.ui.home.HomeFragment;
import com.example.triviaapp.game.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

public class GameActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private HomeFragment homeFragment;
    private DashboardFragment dashboardFragment;
    private NotificationsFragment notificationsFragment;
    private HelpFragment helpFragment;
    private Fragment activeFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private MenuItem profile, score, game, help, signOut;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private boolean navigationListenerSet = false;
    private int lastFragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializeViews();
        setToolbar();
        LoadFragment();

    }

    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.container);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigationDrawerOpen, R.string.navigationDrawerClose){
            public void onDrawerOpened(View drawerView){
                navigationView.bringToFront();

            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fragmentManager.addOnBackStackChangedListener(this);

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);

        }else {
            super.onBackPressed();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        chooseLanguage();

    }

    private void initializeViews(){
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.nav_view);
        navigationView = findViewById(R.id.nav_drawer_view);
        homeFragment = new HomeFragment();
        dashboardFragment = new DashboardFragment();
        notificationsFragment = new NotificationsFragment();
        helpFragment = new HelpFragment();
        activeFragment = homeFragment;
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        profile = bottomNavigationView.getMenu().findItem(R.id.navigation_profile);
        score = bottomNavigationView.getMenu().findItem(R.id.navigation_score);
        game = bottomNavigationView.getMenu().findItem(R.id.navigation_game);
        help = navigationView.getMenu().findItem(R.id.navigation_help);
        signOut = navigationView.getMenu().findItem(R.id.navigation_sign_out);
        chooseLanguage();

    }

    private void setMenuItemsForEnglishLanguage(){
        profile.setTitle(R.string.title_profileEn);
        score.setTitle(R.string.title_scoreEn);
        game.setTitle(R.string.title_gameEn);
        help.setTitle(R.string.helpMenuItemProfileEn);
        signOut.setTitle(R.string.signOutMenuItemProfileEn);

    }

    private void setMenuItemsForRomanianLanguage(){
        profile.setTitle(R.string.title_profileRou);
        score.setTitle(R.string.title_scoreRou);
        game.setTitle(R.string.title_gameRou);
        help.setTitle(R.string.helpMenuItemProfileRou);
        signOut.setTitle(R.string.signOutMenuItemProfileRou);

    }

    private void chooseLanguage(){
        switch (LoggedUserData.language){
            case "english":
                setMenuItemsForEnglishLanguage();
                break;
            case "romanian":
                setMenuItemsForRomanianLanguage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                fragmentManager.beginTransaction().remove(homeFragment).commitNow();
                fragmentManager.beginTransaction().remove(dashboardFragment).commitNow();
                fragmentManager.beginTransaction().remove(notificationsFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                lastFragment = 1;
                return true;

            case R.id.navigation_score:
                fragmentManager.beginTransaction().remove(homeFragment).commitNow();
                fragmentManager.beginTransaction().remove(dashboardFragment).commitNow();
                fragmentManager.beginTransaction().remove(notificationsFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, dashboardFragment, "2").hide(activeFragment).show(dashboardFragment).commit();
                activeFragment = dashboardFragment;
                lastFragment = 2;
                return true;

            case R.id.navigation_game:
                fragmentManager.beginTransaction().remove(homeFragment).commitNow();
                fragmentManager.beginTransaction().remove(dashboardFragment).commitNow();
                fragmentManager.beginTransaction().remove(notificationsFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, notificationsFragment, "3").hide(activeFragment).show(notificationsFragment).commit();
                activeFragment = notificationsFragment;
                lastFragment = 3;
                return true;
            case R.id.navigation_help:
                fragmentManager.beginTransaction().remove(helpFragment).commitNow();
                fragmentManager.beginTransaction().add(R.id.nav_host_fragment, helpFragment, "4").addToBackStack(null).hide(activeFragment).show(helpFragment).commit();
                bottomNavigationView.setVisibility(View.GONE);
                return true;
            case R.id.navigation_sign_out:
                signOut();

        }
        return false;

    }

    private void LoadFragment() {
        fragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment, "1").commit();

    }

    @Override
    public void onBackStackChanged() {
        displayToolbarButton();

    }

    private void displayToolbarButton(){
        boolean fragmentBackStackPopulated = fragmentManager.getBackStackEntryCount() > 0;

        if(fragmentBackStackPopulated){
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);

            if(!navigationListenerSet){
                toggle.setToolbarNavigationClickListener(v -> {
                    fragmentManager.popBackStackImmediate();
                    fragmentManager.beginTransaction().remove(activeFragment).commitNow();
                    fragmentManager.beginTransaction().add(R.id.nav_host_fragment, activeFragment, String.valueOf(lastFragment)).show(activeFragment).commit();
                    bottomNavigationView.setVisibility(View.VISIBLE);
                });
                navigationListenerSet = true;

            }

        }else{
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            navigationListenerSet = false;

        }

    }

    private void signOut() {
        LoggedUserData.onResumeFromAnotherActivity = true;
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        firebaseAuth.signOut();
        finishAndRemoveTask();

    }

}