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
 * The HomeActivity class contains the functionality of the home page
 */

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sharedPrefs_file_key), Context.MODE_PRIVATE);
        String savedString = sharedPref.getString(getString(R.string.sharedPrefs_username_key), "");
        TextView savedName = findViewById(R.id.welcomeUser);
        savedName.setText(getString(R.string.welcome) + " " + savedString + "!");

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

            startActivity(imageViewer);
        });

        Intent savedImage = new Intent(this, SavedImageActivity.class);
        Button viewSavedImgBtn = findViewById(R.id.viewSavedImgBtn);
        viewSavedImgBtn.setOnClickListener(click -> { startActivity(savedImage);
        });
    }
}