package com.example.sharul.newsgateway;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sharul on 5/6/17.
 */

public class AsyncArticle extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncLoaderTask";
    private MainActivity mainActivity;
    private NewsService myService;
    private final String NewsArtURL = "https://newsapi.org/v1/articles?";
    private String[] sArray = new String[20];
    private final String yourAPIKey = "a0453299b4b94149bc2ae281122a847e"; //Prerna's API Key
    private ArrayList<String> article = new ArrayList<>();
    private ArrayList<String> author = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> description = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> web = new ArrayList<>();
    public HashMap<String, ArrayList<String>> Data = new HashMap<>();
    private Bitmap myBitmap;
    private String rSource = "";

    public AsyncArticle(NewsService ms) {
        //mainActivity = ma;
        myService = ms;
    }

    @Override
    protected void onPostExecute(String s)
    {
        Log.d("Async try Article ", String.valueOf(Data.get("Author")));
        myService.setArticle(Data);
        String g = "";
    }

    @Override
    protected String doInBackground(String... params) {

        rSource = params[0];

        Uri.Builder buildURL = Uri.parse(NewsArtURL).buildUpon();
        buildURL.appendQueryParameter("source",rSource);
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
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
        return null;
    }

    private void parseJSON(String s) {
        News.all.clear();
        String np = "";
        String utc = "";
        try {

            JSONObject stock = new JSONObject(s);
            JSONArray oStock = (JSONArray) stock.get("articles");

            String getS = "";
            getS = stock.getString("source");
            for (int i = 0; i < oStock.length(); i++) {
                article.add(oStock.getString(i));
                JSONObject data = new JSONObject(article.get(i));
                author.add(data.getString("author"));
                title.add(data.getString("title"));
                description.add(data.getString("description"));
                url.add(data.getString("urlToImage"));
                web.add(data.getString("url"));

                try {
                    Log.d(TAG, "URL to Image " + data.getString("urlToImage"));
                    String IUrl = data.getString("urlToImage");
                    if (rSource.equals("buzzfeed")) {
                        String changedUrl = IUrl.replace("http:", "https:");
                        java.net.URL url = new java.net.URL(changedUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Log.d(TAG, "conn: " + input);
                        myBitmap = BitmapFactory.decodeStream(input);
                    } else {
                        java.net.URL url = new java.net.URL(IUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Log.d(TAG, "conn: " + input);
                        myBitmap = BitmapFactory.decodeStream(input);
                    }
                    if(!(data.getString("publishedAt").equals("null"))) {
                        np = "";
                        if(data.getString("publishedAt").contains("+00:00")) {
                            utc = data.getString("publishedAt").replace("+00:00", "Z");
                        }else
                        {
                            utc = data.getString("publishedAt");
                        }
                        //Date d = data.getString("publishedAt");
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                            SimpleDateFormat pd = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
                            Date parsed = sdf.parse(utc);
                            //String np = String.valueOf(pd.parse(parsed.toString()));
                            np = pd.format(parsed);
                        }catch (Exception e)
                        {
                            SimpleDateFormat adf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.US);
                            SimpleDateFormat pd = new SimpleDateFormat("MMMM dd, yyyy HH:mm");

                            Date parsed = adf.parse(utc);
                            //String np = String.valueOf(pd.parse(parsed.toString()));
                            np = pd.format(parsed);

                        }
                        //sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

                        Log.d(TAG, "Time: " + data.getString("publishedAt"));
                        Log.d(TAG, "Parsed Time: " + np);
                        time.add(np);
                    }
                    else {
                        np = "null";
                    }

                    //im.setImageBitmap(myBitmap);

                } catch (Exception e) {
                    e.printStackTrace();

                }
                News.addnew(data.getString("title"),data.getString("author"),myBitmap,
                        data.getString("description"),oStock.length(),np,data.getString("url"));
            }
            Data.put("Author",author);
            Data.put("Title",title);
            Data.put("Description",description);
            Data.put("URL",url);
            Data.put("Time",time);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
