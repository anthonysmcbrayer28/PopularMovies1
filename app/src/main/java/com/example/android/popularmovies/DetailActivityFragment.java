package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static String youtube;
    public static String youtube2;
    public static String overview;
    public static String rating;
    public static String date;
    public static String review;
    public static String title;
    public static String poster;
    public static boolean favorite;
    public static ArrayList<String> comments;
    public static Button b;



    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        getActivity().setTitle("Movie Details");

        review = null;
        if(intent !=null && intent.hasExtra("overview"))
        {
            overview = intent.getStringExtra("overview");
            TextView tv = (TextView) rootView.findViewById(R.id.overview);
            tv.setText(overview);

        }
        if(intent !=null && intent.hasExtra("title"))
        {
            title = intent.getStringExtra("title");
            TextView tv = (TextView) rootView.findViewById(R.id.title);
            tv.setText(title);

        }
        if(intent !=null && intent.hasExtra("rating"))
        {
            rating = intent.getStringExtra("rating");
            TextView tv = (TextView) rootView.findViewById(R.id.rating);
            tv.setText(rating);
        }
        if(intent !=null && intent.hasExtra("date"))
        {
            date = intent.getStringExtra("date");
            TextView tv = (TextView) rootView.findViewById(R.id.date);
            tv.setText(date);

        }
        if(intent !=null && intent.hasExtra("poster"))
        {
            poster = intent.getStringExtra("poster");
            ImageView iv = (ImageView) rootView.findViewById(R.id.poster);

            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).resize(
                    MoviesFragment.width, (int)(MoviesFragment.width*1.5)).into(iv);

        }
        if(intent !=null && intent.hasExtra("youtube"))
        {
            youtube = intent.getStringExtra("youtube");

        }
        if(intent !=null && intent.hasExtra("youtube2"))
        {
            youtube2 = intent.getStringExtra(youtube2);
        }
        if(intent !=null && intent.hasExtra("comments"))
        {
            comments = intent.getStringArrayListExtra("comments");
            for(int i = 0; i<comments.size();i++)
            {
                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear);
                View divider = new View(getActivity());
                TextView tv = new TextView(getActivity());
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(p);
                int paddingPixel = 10;
                float density = getActivity().getResources().getDisplayMetrics().density;
                int paddingDP = (int) (paddingPixel * density);
                tv.setPadding(0, paddingDP, 0, paddingDP);
                RelativeLayout.LayoutParams x = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                x.height = 1;
                divider.setLayoutParams(x);
                divider.setBackgroundColor(Color.BLACK);

                tv.setText(comments.get(i));
                layout.addView(divider);
                layout.addView(tv);

                if(review == null)
                {
                    review = comments.get(i);
                }
                else{
                    review+="divider123" + comments.get(i);
                }
            }

        }
        b = (Button)rootView.findViewById(R.id.favorite);
        if(intent !=null && intent.hasExtra("favorite"))
        {
            favorite = intent.getBooleanExtra("favorite", false);
            if(!favorite)
            {
                b.setText("FAVORITE");
                b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            }
            else
            {
                b.setText("UNFAVORITE");
                b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
            }
        }

        return rootView;
    }

    private Intent createShareIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for " + title + ": " +
                "https://www.youtube.com/watch?v=" + youtube);
        return shareIntent;
    }

}