package com.example.finalproject;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * The NavigationManager class contains the logic for the toolbar, navigation drawer, and display help message.
 *
 * @author Adriana de Lazzari
 */

public class NavigationManager implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Instance variables used in the class to create toolbar, navigation drawer, and display help message.
     */

    private AppCompatActivity activity;
    private int toolbarId;
    private int drawerLayoutId;
    private int navViewId;
    private int helpMessageId;
    private int activityNameId;

    public NavigationManager(AppCompatActivity activity, int toolbarId, int drawerLayoutId, int navViewId, int helpMessageId, int activityNameId) {
        this.activity = activity;
        this.toolbarId = toolbarId;
        this.drawerLayoutId = drawerLayoutId;
        this.navViewId = navViewId;
        this.helpMessageId = helpMessageId;
        this.activityNameId = activityNameId;

        Toolbar toolbar = createToolbar(this.toolbarId);
        createNavigationDrawer(toolbar, drawerLayoutId);
    }

    /**
     * The createToolbar method creates the toolbar according to each toolbar id.
     *
     * @param toolbarId each page has a different toolbar id.
     *
     * @return toolbar, Toolbar object.
     */

    private Toolbar createToolbar(int toolbarId){
        Toolbar toolbar = activity.findViewById(toolbarId);
        activity.setSupportActionBar(toolbar);
        return toolbar;
    }

    /**
     * The createMenu method creates the menu.
     *
     * @param menu the options menu in which items are placed.
     *
     * @return true.
     */

    public boolean createMenu(Menu menu){
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * The onItemClicked method contains the logic to start an
     * appropriate activity when an item is clicked in the toolbar and navigation drawer.
     *
     * @param menuItem the menu item clicked.
     *
     * @return true.
     */

    public boolean onItemClicked(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.homePageToolbar:
            case R.id.homePage:
                Intent goToHomePage = new Intent(activity, HomeActivity.class);
                activity.startActivity(goToHomePage);
                break;
            case R.id.savedImagesPageToolbar:
            case R.id.savedImagesPage:
                Intent goToSavedImagesPage = new Intent(activity, SavedImageActivity.class);
                activity.startActivity(goToSavedImagesPage);
                break;
            case R.id.helpToolbar:
            case R.id.help:
            case R.id.overflowMenuToolbar:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(helpMessageId)
                        .setNeutralButton(R.string.ok, (click, arg) -> {})
                        .create().show();
                break;
        }
        return true;
    }

    /**
     * The createNavigationDrawer creates the navigation drawer.
     *
     * @param toolbar
     *
     * @param drawerLayoutId each page has a different drawer layout id.
     */

    private void createNavigationDrawer(Toolbar toolbar, int drawerLayoutId){

        DrawerLayout drawer = activity.findViewById(drawerLayoutId);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = activity.findViewById(navViewId);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        TextView activityName = navigationView.getHeaderView(0).findViewById(R.id.activityName);
        activityName.setText(activityNameId);
    }

    /**
     * The onNavigationItemSelected method calls the onItemClicked method and creates drawer layout according to the id.
     *
     * @param item the menu item that was selected.
     *
     * @return false.
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        onItemClicked(item);

        DrawerLayout drawerLayout = activity.findViewById(drawerLayoutId);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
}
