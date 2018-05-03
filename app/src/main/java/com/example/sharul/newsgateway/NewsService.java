package com.example.sharul.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sharul on 5/6/17.
 */

public class NewsService extends Service {

    private static final String TAG = "MyService";
    private boolean running = true;
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    private ServiceReceiver sReceiver;
    private HashMap<String, ArrayList<String>> slist = new HashMap<>();
    static final String SERVICE_DATA = "SERVICE_DATA";
    private ArrayList<String> title = new ArrayList<>();


    public NewsService()
    {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        sReceiver = new ServiceReceiver();
        IntentFilter filter2 = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(sReceiver, filter2);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(running){
                    if (slist.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Intent intent1 = new Intent();
                        intent1.setAction(MainActivity.ACTION_NEWS_STORY);
                        intent1.putStringArrayListExtra(SERVICE_DATA, slist.get("Author"));
                        sendBroadcast(intent1);
                        slist.clear();
                    }

                }


                Log.d(TAG, "run: Ending loop");
            }
        }).start();


        return Service.START_STICKY;
    }
    public void setArticle(HashMap<String, ArrayList<String>> d)
    {
        slist.clear();
        slist.putAll(d);
        title.addAll(slist.get("Title"));
    }


    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        running = false;
        unregisterReceiver(sReceiver);
        super.onDestroy();
    }
    class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_MSG_TO_SERVICE))
            {
                if(intent.hasExtra(MainActivity.DATA_EXTRA1))
                {
                    String source = "";
                    source = intent.getStringExtra(MainActivity.DATA_EXTRA1);
                    source = source.replaceAll("\\s","");
                    new AsyncArticle(NewsService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,source);

                }
            }
        }
    }
}
