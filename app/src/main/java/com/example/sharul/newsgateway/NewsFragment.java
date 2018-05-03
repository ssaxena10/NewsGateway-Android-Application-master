package com.example.sharul.newsgateway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sharul on 5/6/17.
 */

public class NewsFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String COUNT = "COUNT";
    private static final String TAG = "MyFragment";
    private ImageView im;
    public static String index;
    View v;
    boolean flag = false;
    public static final NewsFragment newInstance(int message)
    {
        NewsFragment f = new NewsFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(EXTRA_MESSAGE, message);
        index = Integer.toString(message);
        // bdl.putInt(COUNT, i);
        f.setArguments(bdl);
        return f;
    }


    public int getShownIndex() {
        return getArguments().getInt(EXTRA_MESSAGE, 0);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.myfragment_layout, container, false);

        int message = getArguments().getInt(EXTRA_MESSAGE);
        TextView title = (TextView) v.findViewById(R.id.textView);
        if(News.get(getShownIndex()).getTitle().equals("null"))
            title.setText("");
        else
            title.setText(News.get(getShownIndex()).getTitle());
        TextView author = (TextView) v.findViewById(R.id.author);
        if(News.get(getShownIndex()).getAuthor().equals("null"))
            author.setText("");
        else
            author.setText(News.get(getShownIndex()).getAuthor());
        TextView des = (TextView) v.findViewById(R.id.description);
        if(News.get(getShownIndex()).getDescription().equals("null"))
            des.setText("");
        else
            des.setText(News.get(getShownIndex()).getDescription());
        // des.setText(v.toString());
        TextView count = (TextView) v.findViewById(R.id.count);
        count.setText(Integer.toString(message + 1) + " of " + News.get(getShownIndex()).getCount());
        TextView ti = (TextView) v.findViewById(R.id.time);
        //ti.setText(v.toString());
        if(News.get(getShownIndex()).getTime().equals("null"))
            ti.setText("");
        else
            ti.setText(News.get(getShownIndex()).getTime());
        im = (ImageView) v.findViewById(R.id.image);
        Bitmap bn = News.get(getShownIndex()).getB();
        im.setImageBitmap(bn);
        //description.setText(message);
        im.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Uri uri = Uri.parse(News.get(getShownIndex()).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Uri uri = Uri.parse(News.get(getShownIndex()).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        des.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Uri uri = Uri.parse(News.get(getShownIndex()).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return v;
    }

}
