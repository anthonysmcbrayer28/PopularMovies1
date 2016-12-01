package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.popularmovies.R.drawable.androidparty;
import static com.example.android.popularmovies.R.drawable.download;

/**
 * Created by antho_000 on 11/22/2016.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> array;
    private int width;

    public ImageAdapter(Context c, ArrayList<String> paths, int x)
    {
        mContext = c;
        array=paths;
        width = x;
    }
    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null)
        {
            imageView = new ImageView(mContext);
        }
        else{
            imageView = (ImageView) convertView;
        }

        Drawable d =resizeDrawable(mContext.getResources().getDrawable(R.drawable.download));
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + array.get(position)).
                resize(width, (int)(width*1.5)).placeholder(d).into(imageView);
        return imageView;

    }
    private Drawable resizeDrawable(Drawable image)
    {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b,width, (int)(width*1.5),false);
        return new BitmapDrawable(mContext.getResources(),bitmapResized);
    }
}