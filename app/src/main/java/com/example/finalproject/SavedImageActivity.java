package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SavedImageActivity extends AppCompatActivity {

    private MyListAdapter myAdapter;
    private ImageDatabaseOpenHelper imageDatabaseOpenHelper;
    private ArrayList<ImageDetails> imageDetailsList = new ArrayList<>();
    private ImageDetails imageDetailsDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_image);

        this.imageDatabaseOpenHelper = new ImageDatabaseOpenHelper(this);

        ListView myList = findViewById(R.id.listView);
        myList.setAdapter(myAdapter = new MyListAdapter());
        myList.setOnItemClickListener((parent, view, position, id) -> {
            ImageDetails imageDetails = imageDetailsList.get(position);

            Intent imageViewer = new Intent(this, ImageViewerActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("date_key", imageDetails.getDate());
            imageViewer.putExtras(bundle);

            startActivity(imageViewer);
        });

        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            ImageDetails imageDetails = imageDetailsList.get(position);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.deleteMessage)
                    .setMessage(imageDetails.getDate())
                    .setPositiveButton(R.string.yes, (click, arg) -> {
                        this.deleteDataFromDatabase(id);
                        imageDetailsList.remove(position);
                        imageDetailsDeleted = imageDetails;
                        myAdapter.notifyDataSetChanged();

                        Snackbar.make(myList, R.string.imageDeletedMessage, Snackbar.LENGTH_LONG)
                                .setAction(R.string.imageDeletedUndoMessage, (c -> {
                                    insertIntoDatabase(imageDetailsDeleted);
                                    loadDataFromDatabase();
                                })).show();
                    })
                    .setNegativeButton(R.string.no, (click, arg) -> { })
                    .create().show();
            return true;
        });

        loadDataFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase(){
        imageDetailsList.clear();

        //getting database connection
        SQLiteDatabase db = imageDatabaseOpenHelper.getReadableDatabase();

        String [] allColumns = {ImageDatabaseOpenHelper.COL_ID, ImageDatabaseOpenHelper.COL_TITLE, ImageDatabaseOpenHelper.COL_URL, ImageDatabaseOpenHelper.COL_DATE};
        String orderBy = ImageDatabaseOpenHelper.COL_DATE + " DESC";

        //querying all the results from the database
        Cursor results = db.query(false, ImageDatabaseOpenHelper.TABLE_NAME, allColumns, null, null, null, null, orderBy, null);

        results.moveToFirst();
        while(!results.isAfterLast()){
            long id = results.getLong((results.getColumnIndex(ImageDatabaseOpenHelper.COL_ID)));
            String title = results.getString((results.getColumnIndex(ImageDatabaseOpenHelper.COL_TITLE)));
            String date = results.getString((results.getColumnIndex(ImageDatabaseOpenHelper.COL_DATE)));
            String url = results.getString((results.getColumnIndex(ImageDatabaseOpenHelper.COL_URL)));

            ImageDetails imageDetails = new ImageDetails(id, title, date, url);
            imageDetailsList.add(imageDetails);
            results.moveToNext();
        }
        myAdapter.notifyDataSetChanged();
    }

    private void insertIntoDatabase(ImageDetails imageDetails){
        SQLiteDatabase db = imageDatabaseOpenHelper.getWritableDatabase();
        ContentValues newRowValues = new ContentValues();
        newRowValues.put(ImageDatabaseOpenHelper.COL_TITLE, imageDetails.getTitle());
        newRowValues.put(ImageDatabaseOpenHelper.COL_URL, imageDetails.getUrl());
        newRowValues.put(ImageDatabaseOpenHelper.COL_DATE, imageDetails.getDate());
        db.insert(ImageDatabaseOpenHelper.TABLE_NAME, "NullColumnName", newRowValues);
    }

    private void deleteDataFromDatabase(long id){
        SQLiteDatabase db = imageDatabaseOpenHelper.getWritableDatabase();
        db.delete(ImageDatabaseOpenHelper.TABLE_NAME, ImageDatabaseOpenHelper.COL_ID + "=?", new String[]{Long.toString(id)});
    }

    private class ImageDetails{
        private long id;
        private String title;
        private String date;
        private String url;

        public ImageDetails(long id, String title, String date, String url) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.url = url;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getUrl() {
            return url;
        }
    }

    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageDetailsList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageDetailsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return imageDetailsList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageDetails imageDetails = (ImageDetails) getItem(position);

            View newView = convertView;
            LayoutInflater inflater = getLayoutInflater();

            if(newView == null) {
                newView = inflater.inflate(R.layout.image_details, parent, false);
            }

            TextView imgDateTxtView = newView.findViewById(R.id.imgDate);
            imgDateTxtView.setText(imageDetails.getDate());

            TextView imgTitleTxtView = newView.findViewById(R.id.imgTitle);
            imgTitleTxtView.setText(imageDetails.getTitle());

            ImageView imgView = newView.findViewById(R.id.imgView);
            FileInputStream fis = null;
            try {
                fis = openFileInput(imageDetails.getDate() + ".png");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap dailyImage = BitmapFactory.decodeStream(fis);
            imgView.setImageBitmap(dailyImage);

            return newView;
        }
    }
}