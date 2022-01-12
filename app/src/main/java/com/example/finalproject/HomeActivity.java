package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The HomeActivity class contains the functionality of the home page.
 *
 * @author Adriana de Lazzari
 */

public class HomeActivity extends AppCompatActivity {

    /**
     * The onCreate method creates the home activity and adds functionality.
     * The username is retrieved to display a welcome message.
     * The DatePicker object provides a widget that allows the user to select a date to display the NASA image of the day.
     * The user also might see their saved images.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Displaying welcome message with the username
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPrefs_file_key), Context.MODE_PRIVATE);
        String savedString = sharedPref.getString(getString(R.string.sharedPrefs_username_key), "");
        TextView savedName = findViewById(R.id.welcomeUser);
        savedName.setText(getString(R.string.welcome) + " " + savedString + "!");

        //Selecting a date and clicking on the View button will display the NASA image of the day
        Intent imageViewer = new Intent(this, ImageViewerActivity.class);
        Button viewImgBtn = findViewById(R.id.viewImgBtn);
        DatePicker datePicker = findViewById(R.id.datePicker);
        viewImgBtn.setOnClickListener(click -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year =  datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(calendar.getTime());

            Bundle bundle = new Bundle();
            bundle.putString("date_key", dateString);
            imageViewer.putExtras(bundle);

            //Launching ImageViewerActivity
            startActivity(imageViewer);
        });

        //Clicking on the View Saved button will display the saved images
        Intent savedImage = new Intent(this, SavedImageActivity.class);
        Button viewSavedImgBtn = findViewById(R.id.viewSavedImgBtn);
        viewSavedImgBtn.setOnClickListener(click -> { startActivity(savedImage);
        });
    }
}