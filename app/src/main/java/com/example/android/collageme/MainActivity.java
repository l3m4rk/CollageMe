package com.example.android.collageme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    private static final String APP_NAME = "CollageMe";


    private static final String CLIENT_ID = "3c1e20ba1ae246e0b7bc2c602b72d6ee";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private EditText nickEdit;

    private String nameOfFolder = "/" + APP_NAME + "/images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        nickEdit = (EditText) findViewById(R.id.nickname);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    private void recursiveDelete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                recursiveDelete(child);
        }
        fileOrDirectory.delete();
    }

    private void removeAllFilesFromDirectory(File d) {
        String[] children = d.list();
        if (children.length != 0) {
            for (int i = 0; i < children.length; ++i)
                new File(d, children[i]).delete();
        }
        Log.d(LOG_TAG, Arrays.toString(children));
    }

    public void onCollageCreate(View view) {

        String nickName = nickEdit.getText().toString();

        if (nickName.isEmpty()) {
            Toast.makeText(this, "Введите логин!", Toast.LENGTH_SHORT).show();
        } else {

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {

                RetrieveDataTask task = new RetrieveDataTask();
                task.execute(nickName);

            } else {
                Toast.makeText(this, "Подключение к сети не установлено!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class RetrieveDataTask extends AsyncTask<String, Long, List<String>> {

        List<String> photoUrls;
        List<String> photos = new ArrayList<String>();
        private String nickName;
        private String nameOfPersonFolder;


        @Override
        protected List<String> doInBackground(String... params) {

            nickName = params[0];
            nameOfPersonFolder = "/" + nickName;

            try {

                String userIdRequest = "https://api.instagram.com/v1/users/search?q=" + nickName + "&client_id=" + CLIENT_ID;

                Log.d(LOG_TAG, userIdRequest);

                String response = getJsonData(userIdRequest);

                if (response == null) {
                    return null;
                }

                long userId = getUserIdFromJson(response);

                if (userId == 0) {
                    return null;
                }

                Log.d(LOG_TAG, "This '" + nickName + "' userId = " + userId);

                String photoUrlRequest = "https://api.instagram.com/v1/users/" + userId + "/media/recent/?client_id=" + CLIENT_ID;
                String photoUrlResponse = getJsonData(photoUrlRequest);

                if (photoUrlResponse == null) {
                    return null;
                }

                photoUrls = getPhotoUrlsFromJson(photoUrlResponse);

                for (int i = 0; i < photoUrls.size(); i++) {
                    try {
                        Bitmap bmp = Picasso.with(getApplicationContext()).load(photoUrls.get(i)).get();
                        photos.add(saveToInternalStorage(bmp, i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return photos;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> urls) {
            super.onPostExecute(urls);
            if (urls == null) {
                Toast.makeText(MainActivity.this, "По указанному логину нет данных!", Toast.LENGTH_SHORT).show();
            } else {
                if (urls.size() != 0) {
                    String[] strings = new String[urls.size()];

                    for (int i = 0; i < urls.size(); ++i)
                        strings[i] = urls.get(i);
                    Intent intent = new Intent(getApplicationContext(), PhotoPickerActivity.class);
                    intent.putExtra("photo", strings);
                    intent.putExtra("nickname", nickEdit.getText().toString());
                    startActivity(intent);
                }
            }
        }

        private String getJsonData(String request) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String dataJsonString;

            try {
                URL url = new URL(request);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

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

            if (userExists(dataJsonArray, nickName)) {
                JSONObject object = dataJsonArray.getJSONObject(0);
                return object.getLong(ID);
            } else
                return 0;
        }

        private List<String> getPhotoUrlsFromJson(String jsonStr) throws JSONException {

            final String IMAGES = "images";
            final String RES_1 = "low_resolution";
            final String RES_2 = "thumbnail";
            final String RES_3 = "standard_resolution";
            final String URL = "url";

            List<String> urls = new ArrayList<String>();

            int code = new JSONObject(jsonStr).getJSONObject("meta").getInt("code");
            Log.d(LOG_TAG, "Code = " + code);


            JSONObject jo;
            JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray(DATA);

            String url;

            for (int i = 0; i < jsonArray.length(); ++i) {
                jo = jsonArray.getJSONObject(i).getJSONObject(IMAGES).getJSONObject(RES_3);
                url = jo.get(URL).toString();
                urls.add(url);
            }

            return urls;
        }

        private boolean userExists(JSONArray array, String username) {
            return array.toString().contains("\"username\":\"" + username + "\"");
        }

        private String saveToInternalStorage(Bitmap bitmap, int number) {
            String nameOfFile = "photo";
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + nameOfFolder + nameOfPersonFolder;
            File d = new File(filePath);

            if (!d.exists()) {
                d.mkdirs();
            }

            File file = new File(d, nameOfFile + number + ".jpg");
            if (file.exists())
                file.delete();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getAbsolutePath();
        }


    }

}
