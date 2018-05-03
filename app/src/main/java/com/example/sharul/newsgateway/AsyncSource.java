package com.example.sharul.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sharul on 5/6/17.
 */

public class AsyncSource extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsAsyncLoaderTask";
    private ArrayList<String> so = new ArrayList<>();
    private MainActivity mainActivity;
    private final String NewsSURL = "https://newsapi.org/v1/sources?language=en&country=us";
    private final String yourAPIKey = "a0453299b4b94149bc2ae281122a847e"; //Prerna's API Key
    private final String NewsCatURL = "https://newsapi.org/v1/sources?language=en&country=us";
    private ArrayList<String> source = new ArrayList<>();
    private ArrayList<String> category = new ArrayList<>();
    private String getCategory = "";
    private ArrayList<String> id = new ArrayList<>();


    public AsyncSource(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s)
    {
        // Toast.makeText(mainActivity,"In Async",Toast.LENGTH_SHORT).show();
        mainActivity.setSources(source,category,id);
    }
    @Override
    protected String doInBackground(String... params) {
        if(params[0].equals("Category"))
        {
            getCategory = params[1];
            Uri.Builder buildURL = Uri.parse(NewsSURL).buildUpon();
            buildURL.appendQueryParameter("category", getCategory);
            buildURL.appendQueryParameter("apikey", yourAPIKey);

            //buildURL.appendQueryParameter("search_text", params[0]);

            String urlToUse = buildURL.build().toString();
            Log.d(TAG, "doInBackground: " + urlToUse);

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                Log.d(TAG, "doInBackground: " + sb.toString());

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                return null;
            }
            parseJSON(sb.toString());
        }else
        {
            Uri.Builder buildURL = Uri.parse(NewsSURL).buildUpon();
            buildURL.appendQueryParameter("apikey", yourAPIKey);

            //buildURL.appendQueryParameter("search_text", params[0]);

            String urlToUse = buildURL.build().toString();
            Log.d(TAG, "doInBackground: " + urlToUse);

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }

                Log.d(TAG, "doInBackground: " + sb.toString());

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                return null;
            }
            parseJSON(sb.toString());
        }

        return null;
    }

    private void parseJSON(String s) {

        try {

            JSONObject stock = new JSONObject(s);
            JSONArray oStock = (JSONArray) stock.get("sources");

            int olen = oStock.length();
            for (int i = 0; i < olen; i++) {
                so.add(oStock.getString(i));
                JSONObject j = new JSONObject(so.get(i));
                source.add(j.getString("name"));
                id.add(j.getString("id"));
                if (!(category.contains(j.getString("category"))))
                    category.add(j.getString("category"));
                Log.d(TAG, source.get(i));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}