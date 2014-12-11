package com.example.android.collageme;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    private static final String CLIENT_ID = "3c1e20ba1ae246e0b7bc2c602b72d6ee";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RetriveDataTask task;
    private EditText nickEdit;

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = new RetriveDataTask();
        nickEdit = (EditText) findViewById(R.id.nickname);


        //get rid of it at the end
        nickEdit.setText("iskorrrka");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void onCollageCreate(View view) {

        String nickName = nickEdit.getText().toString();

        if (nickName.isEmpty()) {
            Toast.makeText(this, "Введите логин!", Toast.LENGTH_SHORT).show();
        } else {

            //TODO: get user id from username
            //TODO: сделать при помощи AsyncTask
            task.execute(nickName);

            //TODO: get photos of current user

        }
    }

    class RetriveDataTask extends AsyncTask<String, Long, List<String>> {


        @Override
        protected List<String> doInBackground(String... params) {

            String nickName = nickEdit.getText().toString();
            List<String> photoUrls;

            try {

                String userIdRequest = "https://api.instagram.com/v1/users/search?q=" + nickName + "&client_id=" + CLIENT_ID;
                String response = getJsonData(userIdRequest);
                userId = getUserIdFromJson(response);
                Log.d(LOG_TAG, "This '" + nickName + "' userId = " + userId);

                String photoUrlRequest = "https://api.instagram.com/v1/users/" + userId + "/media/recent/?client_id=" + CLIENT_ID;
                String photoUrlResponse = getJsonData(photoUrlRequest);

                photoUrls = getPhotoUrlsFromJson(photoUrlResponse);

                for (String photoUrl : photoUrls) {
                    Log.d(LOG_TAG, photoUrl);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getJsonData(String request) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String dataJsonString = null;

            try {
                URL url = new URL(request);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    dataJsonString = null;

                dataJsonString = buffer.toString();
                return dataJsonString;

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Something wrong with URL", e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Something wrong with connection", e);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        final String DATA = "data";
        final String ID = "id";


        private long getUserIdFromJson(String jsonStr) throws JSONException {

            JSONObject dataJson = new JSONObject(jsonStr);
            JSONArray dataJsonArray = dataJson.getJSONArray(DATA);
            JSONObject object = dataJsonArray.getJSONObject(0);

            return object.getLong(ID);
        }

        private List<String> getPhotoUrlsFromJson(String jsonStr) throws JSONException {

            final String IMAGES = "images";
            final String RES_1 = "low_resolution";
            final String RES_2 = "thumbnail";
            final String RES_3 = "standard_resolution";
            final String URL = "url";

            List<String> urls = new ArrayList<String>();
            Log.d(this.getClass().getSimpleName(), jsonStr);
            JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray(DATA);

            JSONObject jo;
            String url;

            for (int i = 0; i < jsonArray.length(); ++i) {
                jo = jsonArray.getJSONObject(i).getJSONObject(IMAGES).getJSONObject(RES_2);
                url = jo.get(URL).toString();
                urls.add(url);
            }

            return urls;
        }

    }

}
