package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The WelcomeActivity class contains the functionality of the welcome page.
 *
 * @author Adriana de Lazzari
 */

public class WelcomeActivity extends AppCompatActivity {

    /**
     * The onCreate method creates the welcome activity and adds functionality.
     * EditText and Button allow the user to enter and save their name.
     * The username is saved so that, the next time the user opens the application, the home activity is launched directly.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Bundle bundle = getIntent().getExtras();
        boolean editUsername = bundle != null ? bundle.getBoolean("edit_username_key", false) : false;

        //Clicking on the save button will launch the home activity
        Intent homePage = new Intent(this, HomeActivity.class);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPrefs_file_key), Context.MODE_PRIVATE);

        String savedString = sharedPref.getString(getString(R.string.sharedPrefs_username_key), "");

        //The user name needs to be entered and saved to start the home activity
        if (!editUsername && !savedString.isEmpty()) {
            startActivity(homePage);
            finish();
        } else {
            EditText userNameEditText = findViewById(R.id.userName);
            if(!savedString.isEmpty()){
                userNameEditText.setText(savedString);
            }

            Button savedUserName = findViewById(R.id.saveUserNameBtn);
            savedUserName.setOnClickListener(click -> {
                String userName = userNameEditText.getText().toString();

                if (!userName.isEmpty()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.sharedPrefs_username_key), userName);
                    editor.commit();

                    startActivity(homePage);
                    finish();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.attention)
                            .setMessage(getResources().getString(R.string.userNameEmpty))
                            .setNeutralButton(R.string.ok, (dialog, which) -> {
                            })
                            .create()
                            .show();
                }
            });
        }
    }
}