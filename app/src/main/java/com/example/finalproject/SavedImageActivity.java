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
import android.view.Menu;
import android.view.MenuItem;
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

/**
 * The SavedImageActivity class contains the functionality of the saved image list page.
 *
 * @author Adriana de Lazzari
 */

public class SavedImageActivity extends AppCompatActivity {

    /**
     * Instance variables used in the class.
     */

    private MyListAdapter myAdapter;
    private ImageDatabaseOpenHelper imageDatabaseOpenHelper;
    private ArrayList<ImageDetails> imageDetailsList = new ArrayList<>();
    private ImageDetails imageDetailsDeleted;
    private NavigationManager navigationManager;

    /**
     * The onCreate method creates the saved image list page and adds functionality.
     * The ListView items are displayed to the user with their saved images.
     * Clicking on a ListView item goes to ImageViewerActivity where the user can see the image details.
     * The AlertDialog allows the user to delete an item from the list.
     * A snackbar is displayed once the image is deleted with and Undo option to undo the action.
     * The loadDataFromDatabase method is called to populate the ListView.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_image);

        this.navigationManager = new NavigationManager(this, R.id.toolbarSavedImage, R.id.drawerLayoutSavedImage, R.id.navViewSavedImage, R.string.helpMessageSavedImage, R.string.savedImageActivityName);

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
                    .setNegativeButton(R.string.no, (click, arg) -> {
                    })
                    .create().show();
            return true;
        });

        loadDataFromDatabase();
    }

    /**
     * The onResume method calls the loadDataFromDatabase method to refresh the data.
     */

    @Override
    protected void onResume() {
        super.onResume();

        loadDataFromDatabase();
    }

    /**
     * The loadDataFromDatabase method retrieves image data from database.
     */

    private void loadDataFromDatabase() {
        imageDetailsList.clear();

        //getting database connection
        SQLiteDatabase db = imageDatabaseOpenHelper.getReadableDatabase();

        String[] allColumns = {ImageDatabaseOpenHelper.COL_ID, ImageDatabaseOpenHelper.COL_TITLE, ImageDatabaseOpenHelper.COL_URL, ImageDatabaseOpenHelper.COL_DATE};
        String orderBy = ImageDatabaseOpenHelper.COL_DATE + " DESC";

        //querying all the results from the database
        Cursor results = db.query(false, ImageDatabaseOpenHelper.TABLE_NAME, allColumns, null, null, null, null, orderBy, null);

        results.moveToFirst();
        while (!results.isAfterLast()) {
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

    /**
     * The insertIntoDatabase method inserts image data into the database.
     *
     * @param imageDetails object of the ImageDetails class.
     */

    private void insertIntoDatabase(ImageDetails imageDetails) {
        SQLiteDatabase db = imageDatabaseOpenHelper.getWritableDatabase();
        ContentValues newRowValues = new ContentValues();
        newRowValues.put(ImageDatabaseOpenHelper.COL_TITLE, imageDetails.getTitle());
        newRowValues.put(ImageDatabaseOpenHelper.COL_URL, imageDetails.getUrl());
        newRowValues.put(ImageDatabaseOpenHelper.COL_DATE, imageDetails.getDate());
        db.insert(ImageDatabaseOpenHelper.TABLE_NAME, "NullColumnName", newRowValues);
    }

    /**
     * The deleteDataFromDatabase method deletes image data from the database.
     *
     * @param id the deletion is done by id.
     */

    private void deleteDataFromDatabase(long id) {
        SQLiteDatabase db = imageDatabaseOpenHelper.getWritableDatabase();
        db.delete(ImageDatabaseOpenHelper.TABLE_NAME, ImageDatabaseOpenHelper.COL_ID + "=?", new String[]{Long.toString(id)});
    }

    /**
     * The ImageDetails class contains image information.
     */

    private class ImageDetails {

        /**
         * Instance variables that represent the image details.
         */

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

    /**
     * The MyListAdapter class adapts the data to be displayed in the ListView.
     */

    private class MyListAdapter extends BaseAdapter {

        /**
         * The getCount method returns the size of the ArrayList.
         *
         * @return number of items.
         */

        @Override
        public int getCount() {
            return imageDetailsList.size();
        }

        /**
         * The getItem method returns the object displayed at row position in the list.
         *
         * @param position item position in the list.
         * @return what to show at row position.
         */

        @Override
        public Object getItem(int position) {
            return imageDetailsList.get(position);
        }

        /**
         * The getItemId method returns the database id of the element at the given index of position.
         *
         * @param position item position in the list.
         * @return database id of the item at a given position.
         */

        @Override
        public long getItemId(int position) {
            return imageDetailsList.get(position).getId();
        }

        /**
         * The getView method specifies how each row looks.
         *
         * @param position    item position in the list.
         * @param convertView the old view to reuse, if possible.
         * @param parent      the parent that this view will eventually be attached to.
         * @return View object to go in a row of the ListView.
         */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageDetails imageDetails = (ImageDetails) getItem(position);

            View newView = convertView;
            LayoutInflater inflater = getLayoutInflater();

            if (newView == null) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return this.navigationManager.createMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return this.navigationManager.onItemClicked(item);
    }
}
