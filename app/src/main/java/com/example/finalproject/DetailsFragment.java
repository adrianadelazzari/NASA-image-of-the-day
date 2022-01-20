package com.example.finalproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The DetailsFragment class is a simple {@link Fragment} subclass.
 * It contains the fragment details logic.
 *
 * @author Adriana de Lazzari
 */

public class DetailsFragment extends Fragment {

    /**
     * Instance variables used in the class.
     */

    private String dateString;
    private String imageTitle;
    private String imageUrl;
    private ProgressBar progressBar;
    private TextView imageTitleTextView;
    private ImageView image;
    private Button saveImgBtn;
    private Button deleteImgBtn;
    private ImageDatabaseOpenHelper imageDatabaseOpenHelper;
    private Bitmap dailyImage;

    /**
     * SavedImageActivity object
     */

    private SavedImageActivity savedImageActivity;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * The onAttach method is called when the fragment is first attached to its context.
     *
     * @param context Context object
     */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        savedImageActivity = (SavedImageActivity) context;
    }

    /**
     * The onCreateView method is called to have the fragment instantiate its user interface view.
     *
     * @param inflater LayoutInflater object that can be used to inflate any views in the fragment.
     *
     * @param container parent view that the fragment's UI should be attached to.
     *
     * @param savedInstanceState if non-null, the fragment is being re-constructed from a previous saved state.
     *
     * @return result
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.imageDatabaseOpenHelper = new ImageDatabaseOpenHelper(this.getActivity());

        Bundle bundle = getArguments();

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_details, container, false);

        TextView date = result.findViewById(R.id.imageDate);
        this.progressBar = result.findViewById(R.id.progressBar);
        this.imageTitleTextView = result.findViewById(R.id.imageTitle);
        this.image = result.findViewById(R.id.image);
        dateString = bundle.getString("date_key", "");
        date.setText(dateString);

        //image information is saved to the database when clicking on the save button
        this.saveImgBtn = result.findViewById(R.id.saveImgBtn);
        saveImgBtn.setOnClickListener(click -> {
            SQLiteDatabase db = imageDatabaseOpenHelper.getWritableDatabase();
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(ImageDatabaseOpenHelper.COL_TITLE, imageTitle);
            newRowValues.put(ImageDatabaseOpenHelper.COL_URL, imageUrl);
            newRowValues.put(ImageDatabaseOpenHelper.COL_DATE, dateString);
            db.insert(ImageDatabaseOpenHelper.TABLE_NAME, "NullColumnName", newRowValues);
            saveImgBtn.setVisibility(View.INVISIBLE);
            deleteImgBtn.setVisibility(View.VISIBLE);
            savedImageActivity.loadDataFromDatabase();
            Toast.makeText(this.getActivity(), R.string.toastSavedImage, Toast.LENGTH_LONG).show();
        });

        //image deletion from the database can be done once the image is saved. The deletion is done according to the image's date
        this.deleteImgBtn = result.findViewById(R.id.deleteImgBtn);
        deleteImgBtn.setOnClickListener(click -> {
            SQLiteDatabase db = imageDatabaseOpenHelper.getWritableDatabase();
            db.delete(ImageDatabaseOpenHelper.TABLE_NAME, ImageDatabaseOpenHelper.COL_DATE + "=?", new String[]{dateString});
            saveImgBtn.setVisibility(View.VISIBLE);
            deleteImgBtn.setVisibility(View.INVISIBLE);
            savedImageActivity.loadDataFromDatabase();
            Toast.makeText(this.getActivity(), R.string.toastDeletedImage, Toast.LENGTH_LONG).show();
        });

        //button to hide fragment details
        Button hideBtn = result.findViewById(R.id.hideBtn);
        hideBtn.setOnClickListener( click -> {
            savedImageActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        checkDatabase();

        return result;
    }

    /**
     * The checkDatabase method checks if the image was already saved to the database.
     * Otherwise, it creates an AsyncTask to retrieve NASA information and downloads the image.
     */

    public void checkDatabase(){
        //Getting database connection
        SQLiteDatabase db = imageDatabaseOpenHelper.getReadableDatabase();

        String selection = ImageDatabaseOpenHelper.COL_DATE + " = ?";
        String[] selectionArgs = new String[] {dateString};

        String [] allColumns = {ImageDatabaseOpenHelper.COL_TITLE, ImageDatabaseOpenHelper.COL_URL, ImageDatabaseOpenHelper.COL_DATE};

        //Checking the database
        Cursor result = db.query(false, ImageDatabaseOpenHelper.TABLE_NAME, allColumns, selection, selectionArgs, null, null, null, null);
        if(result != null && result.moveToFirst()){
            this.imageTitle = result.getString((result.getColumnIndex(ImageDatabaseOpenHelper.COL_TITLE)));
            this.imageTitleTextView.setText(this.imageTitle);
            this.imageUrl = result.getString((result.getColumnIndex(ImageDatabaseOpenHelper.COL_URL)));
            if(fileExists(dateString + ".png")){
                FileInputStream fis = null;
                try {
                    fis = this.getActivity().openFileInput(dateString + ".png");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                dailyImage = BitmapFactory.decodeStream(fis);
                image.setImageBitmap(dailyImage);
                saveImgBtn.setVisibility(View.INVISIBLE);
                deleteImgBtn.setVisibility(View.VISIBLE);
            }else{
                DetailsFragment.ImageNasaQuery imageNasaQuery = new DetailsFragment.ImageNasaQuery(this.getActivity());
                imageNasaQuery.execute("https://api.nasa.gov/planetary/apod?api_key=sVsvLmBvGzGWgftw0biKMqwAna8hpi7FVJ1BlFLX&date=" + dateString);
            }
        }else{
            //Creating an AsyncTask object and retrieving NASA information
            DetailsFragment.ImageNasaQuery imageNasaQuery = new DetailsFragment.ImageNasaQuery(this.getActivity());
            imageNasaQuery.execute("https://api.nasa.gov/planetary/apod?api_key=sVsvLmBvGzGWgftw0biKMqwAna8hpi7FVJ1BlFLX&date=" + dateString);
            saveImgBtn.setVisibility(View.VISIBLE);
            deleteImgBtn.setVisibility(View.INVISIBLE);
        }
        result.close();
    }

    /**
     * The fileExists method checks if file exists.
     *
     * @param fname file name.
     *
     * @return true if file exists.
     */

    public boolean fileExists(String fname) {
        File file = this.getActivity().getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    /**
     * The ImageNasaQuery class consumes the NASA API and downloads the image of the day.
     */

    private class ImageNasaQuery extends AsyncTask<String, Integer, String> {

        private FragmentActivity fragmentActivity;

        public ImageNasaQuery(FragmentActivity fragmentActivity){
            this.fragmentActivity = fragmentActivity;
        }

        /**
         * The doInBackground method consumes the NASA API and downloads the image of the day.
         *
         * @param args task parameters.
         *
         * @return String when process is complete.
         */

        @Override
        protected String doInBackground(String... args) {

            //Loading from JSON

            try {
                publishProgress(0);

                //Creating a URL object of what server to contact:
                URL url = new URL(args[0]);

                //Opening the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //Waiting for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reading:
                //Building the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                publishProgress(10);

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string

                //Converting string to JSON:
                JSONObject imageDetails = new JSONObject(result);

                //Getting the double associated with "value"
                imageTitle = imageDetails.getString("title");
                imageUrl = imageDetails.getString("url");

                publishProgress(25);

                URL urlIcon = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) urlIcon.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    publishProgress(50);

                    dailyImage = BitmapFactory.decodeStream(connection.getInputStream());
                    FileOutputStream outputStream = fragmentActivity.openFileOutput(dateString + ".png", Context.MODE_PRIVATE);
                    dailyImage.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

                publishProgress(100);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return "Done";
        }

        /**
         * The onProgressUpdate method updates the progress.
         *
         * @param args progress parameters.
         */

        public void onProgressUpdate(Integer... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        /**
         * The onPostExecute method displays the image.
         *
         * @param fromDoInBackground return from the doInBackground method.
         */

        public void onPostExecute(String fromDoInBackground) {
            Log.i("HTTP", fromDoInBackground);
            imageTitleTextView.setText(imageTitle);
            image.setImageBitmap(dailyImage);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}