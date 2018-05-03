package com.example.sharul.newsgateway;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.sharul.newsgateway.MainActivity.ACTION_NEWS_STORY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ArrayList<String> items = new ArrayList<>();
    private Menu menu;

    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private NewsReceiver newsReceiver;
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    private ArrayList<String> Msource = new ArrayList<>();
    private ArrayList<String> Mcategory = new ArrayList<>();
    private ArrayList<String> NewsId = new ArrayList<>();
    private ArrayList<String> authorList = new ArrayList<>();
    static final String DATA_EXTRA1 = "DATA_EXTRA1";
    private boolean flag = false;
    private String categoryselected = "";
    boolean cflag = false;
    String src = "";
    int ac = 0;
    int a = 0;
    int ind = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item, items));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                pager.setBackground(null);
                selectItem(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close   /* "close drawer" description for accessibility */
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setBackgroundResource(R.drawable.news);

        Intent intent1 = new Intent(MainActivity.this, NewsService.class);
        startService(intent1);

        newsReceiver = new NewsReceiver();

        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter1);

        //if(savedInstanceState==null)
        new AsyncSource(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"all");
    }

    public void setSources(ArrayList<String> s, ArrayList<String> c,ArrayList<String> Id) {

        Msource.clear();
        Mcategory.clear();
        Msource = s;
        Mcategory = c;
        NewsId = Id;
        Collections.sort(Mcategory);
        if (flag == false) {
            menu.add(R.menu.menu_main, Menu.NONE, 0, "all");
            for (int i = 0; i < Mcategory.size(); i++) {
                menu.add(R.menu.menu_main, Menu.NONE, 0, Mcategory.get(i));
            }
            flag = true;
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, Msource));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("FLAG", String.valueOf(cflag));
        if(cflag)
        {
            outState.putString("C", categoryselected);
        }
        else {
            Log.d(TAG,"in onSave" + src);
            outState.putString("S", src);
            outState.putInt("count",ac);
            outState.putInt("ind",pager.getCurrentItem());
            Log.d(TAG,"OnSave Index" + pager.getCurrentItem());
        }
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        String s = savedInstanceState.getString("FLAG");
        if(s.equals("true"))
        {
            String cs = savedInstanceState.getString("C");
            Log.d(TAG,"In onRestore: " + cs);
            new AsyncSource(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Category",cs);
        }
        else
        {
            pager.setBackground(null);
            String sr = savedInstanceState.getString("S");
            a = savedInstanceState.getInt("count");
            ind = savedInstanceState.getInt("ind");
            reDoFragments();
            Log.d(TAG,"In onRestore index: " + ind);


        }
    }

    private void reDoFragments() {

        for (int i = 0; i < pageAdapter.getCount(); i++) {
            pageAdapter.notifyChangeInPosition(i);
        }
        fragments.clear();

        for (int i = 0; i < a; i++) {

            fragments.add(NewsFragment.newInstance(i));

            // Log.d(TAG,"INSIDE REDO" + (i));
        }

        pageAdapter.notifyDataSetChanged();
        //Log.d(TAG,"INDEX: " + ind);
        pager.setCurrentItem(ind);
    }

    private void selectItem(int position)
    {
        items=Msource;
        setTitle(items.get(position));
        src = NewsId.get(position);
        Intent intent2 = new Intent();
        intent2.setAction(NewsService.ACTION_MSG_TO_SERVICE);
        intent2.putExtra(DATA_EXTRA1, src);
        sendBroadcast(intent2);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //menu.add(R.menu.menu_main, Menu.NONE, 0, cat.get(0));
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.toString().equals("all")) {
            new AsyncSource(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"all");
        } else {
            cflag = true;
            Log.d(TAG,"Category selected "+item);
            categoryselected = item.toString();
            new AsyncSource(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Category",item.toString());
        }
        return true;

    }


    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }

    class NewsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals(ACTION_NEWS_STORY))
            {
                if(intent.hasExtra(NewsService.SERVICE_DATA))
                {
                    authorList = intent.getStringArrayListExtra(NewsService.SERVICE_DATA);
                    ac = authorList.size();
                    fragments.clear();

                    for (int i = 0; i < ac; i++) {
                        fragments.add(NewsFragment.newInstance(i));
                        //Log.d(TAG,"INSIDE REDO" + (i));

                    }

                    pageAdapter.notifyDataSetChanged();
                    pager.setCurrentItem(0);
                }
            }
        }
    }
}
