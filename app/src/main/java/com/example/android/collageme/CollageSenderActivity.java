package com.example.android.collageme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class CollageSenderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_sender);
    }



    private void sendEmail(View view) {
        String to = "l3m4rk@yandex.ru";
        String subject = "Great collage from CollageMe!";
        String message = "Test message!";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client"));
    }




}
