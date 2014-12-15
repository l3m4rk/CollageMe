package com.example.android.collageme;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;


public class CollageSenderActivity extends Activity {

    //TODO maybe move to OPTIONS
    private static String EMAIL;

    private static final int ID_PREF = 101;
    private static final String LOG_TAG = CollageSenderActivity.class.getSimpleName();
    private String pathToCollage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_sender);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        Bundle data = getIntent().getExtras();
        pathToCollage = data.getString("collage_path");
        ImageView imageView = (ImageView) findViewById(R.id.previewImage);
        File collage = new File(pathToCollage);
        Picasso.with(this).load(collage).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collage_sender, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        EMAIL = settings.getString("email", "l3m4rk@ya.ru");
    }

    public void sendEmail(View view) {

        //TODO get path to collage from bundle

        Log.d(LOG_TAG, "Received path = " + pathToCollage);

        String subject = "Great collage from CollageMe!";
        String message = "Test message!";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        Log.d(LOG_TAG, "Path to collage file " + pathToCollage);
        email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pathToCollage));

        email.setType("message/rfc822");
        try {
            startActivity(Intent.createChooser(email, "Отправить коллаж..."));
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }




}
