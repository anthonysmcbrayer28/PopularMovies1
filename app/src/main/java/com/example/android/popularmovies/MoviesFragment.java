package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {


    static GridView gridview;
    static int width;
    static ArrayList<String> posters;
    static boolean sortByPop;
    static String API_KEY = "8913fe3dd006394120f45b515455875d";
    static PreferenceChangeListener listener;
    static SharedPreferences prefs;
    static boolean sortByFavorites;
    static ArrayList<String> postersF = new ArrayList<String>();
    static ArrayList<String> overviews;
    static ArrayList<String> titles;
    static ArrayList<String> dates;
    static ArrayList<String> ratings;
    static ArrayList<String> youtubes;
    static ArrayList<String> youtubes2;
    static ArrayList<String> ids;
    static ArrayList<Boolean> favorited;
    static ArrayList<ArrayList<String>> comments;
    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        WindowManager wm =(WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(MainActivity.TABLATE)
        {
            width = size.x/6;
        }
        else width=size.x/3;
        if(getActivity()!=null)
        {
            ArrayList<String> array = new ArrayList<String>();
            ImageAdapter adapter = new ImageAdapter(getActivity(),array,width);
            gridview = (GridView)rootView.findViewById(R.id.gridView);

            gridview.setColumnWidth(width);
            gridview.setAdapter(adapter);
        }
        //listen for presses on gridview items
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                favorited = new ArrayList<Boolean>();
                for(int i = 0; i<titles.size();i++)
                {
                    favorited.add(false);
                }
                Intent intent = new Intent(getActivity(),DetailActivity.class).
                        putExtra("overview",overviews.get(position)).
                        putExtra("poster", posters.get(position)).
                        putExtra("title",titles.get(position)).
                        putExtra("dates",dates.get(position)).
                        putExtra("rating",ratings.get(position)).
                        putExtra("youtube",youtubes.get(position)).
                        putExtra("youtube2",youtubes2.get(position)).
                        putExtra("comments",comments.get(position)).
                        putExtra("favorite",favorited.get(position));
                startActivity(intent);

            }
        });



        return rootView;
    }
    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridview.setAdapter(null);
            onStart();
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);

        if(prefs.getString("sortby","popularity").equals("popularity"))
        {
            getActivity().setTitle("Most Popular Movies");
            sortByPop = true;
            sortByFavorites=false;
        }
        else if(prefs.getString("sortby","popularity").equals("rating"))
        {
            getActivity().setTitle("Highest Rated Movies");
            sortByPop = false;
            sortByFavorites=false;
        }
        else if(prefs.getString("sortby","popularity").equals("favorites"))
        {
            getActivity().setTitle("Favorited Movies");
            sortByPop = false;
            sortByFavorites=true;
        }
        TextView textView = new TextView(getActivity());
        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.relativelayout);
        if(sortByFavorites)
        {
            if(postersF.size()==0)
            {
                textView.setText("You have no favorites movies.");
                if(layout.getChildCount()==1)
                    layout.addView(textView);
                gridview.setVisibility(GridView.GONE);
            }
            else{
                gridview.setVisibility(GridView.VISIBLE);
                layout.removeView(textView);
            }
            if(postersF!=null&&getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),postersF,width);
                gridview.setAdapter(adapter);
            }
        }
        else{
            gridview.setVisibility(GridView.VISIBLE);
            layout.removeView(textView);
        }

        if(isNetworkAvailable())
        {

            new ImageLoadTask().execute();
        }
        else{
            TextView textview1 = new TextView(getActivity());
            RelativeLayout layout1 = (RelativeLayout)getActivity().findViewById(R.id.relativelayout);
            textview1.setText("You are not connected to the Internet");
            if(layout1.getChildCount()==1)
            {
                layout1.addView(textview1);
            }
            gridview.setVisibility(GridView.GONE);
        }
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null &&activeNetworkInfo.isConnected();
    }
    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while(true){
                try{
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                }
                catch(Exception e)
                {
                    continue;
                }
            }

        }
        @Override
        protected void onPostExecute(ArrayList<String>result)
        {
            if(result!=null && getActivity()!=null)
            {
                ImageAdapter adapter = new ImageAdapter(getActivity(),result, width);
                gridview.setAdapter(adapter);

            }
        }
        public String[] getPathsFromAPI(boolean sortbypop)
        {
            while(true)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    if (sortbypop) {
                        urlString ="http://api.themoviedb.org/3/movie/popular?api_key=8913fe3dd006394120f45b515455875d" ;
                    } else {
                        urlString ="http://api.themoviedb.org/3/movie/top_rated?api_key=8913fe3dd006394120f45b515455875d" ;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();

                    try {
                        overviews = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"overview")));
                        titles = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"original_title")));
                        ratings = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"vote_average")));
                        dates = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"release_date")));
                        ids = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"id")));
                        while(true)
                        {
                            youtubes = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,0)));
                            youtubes2 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,1)));
                            int nullCount = 0;
                            for(int i = 0; i<youtubes.size();i++)
                            {
                                if(youtubes.get(i)==null)
                                {
                                    nullCount++;
                                    youtubes.set(i,"no video found");
                                }
                            }
                            for(int i = 0; i<youtubes2.size();i++)
                            {
                                if(youtubes2.get(i)==null)
                                {
                                    nullCount++;
                                    youtubes2.set(i,"no video found");
                                }
                            }
                            if(nullCount>2)continue;
                            break;
                        }
                        comments = getReviewsFromIds(ids);
                        return getPathsFromJSON(JSONResult);

                    } catch (JSONException e) {
                        return null;
                    }
                }catch(Exception e)
                {
                    continue;
                }finally {
                    if(urlConnection!=null)
                    {
                        urlConnection.disconnect();
                    }
                    if(reader!=null)
                    {
                        try{
                            reader.close();
                        }catch(final IOException e)
                        {
                        }
                    }
                }


            }

        }
        public String[] getYoutubesFromIds(ArrayList<String> ids, int position)
        {
            String[] results = new String[ids.size()];
            for(int i =0; i<ids.size(); i++)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/videos?api_key=8913fe3dd006394120f45b515455875d ";


                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();
                    try {
                        results[i] = getYoutubeFromJSON(JSONResult, position);
                    } catch (JSONException E) {
                        results[i] = "no video found";
                    }
                } catch (Exception e) {

                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                        }
                    }
                }
            }
            return results;

        }
        public ArrayList<ArrayList<String>> getReviewsFromIds(ArrayList<String> ids)
        {
            outerloop:
            while(true)
            {
                ArrayList<ArrayList<String>> results = new ArrayList<>();
                for(int i =0; i<ids.size(); i++)
                {
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String JSONResult;

                    try {
                        String urlString = null;
                        urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/reviews?api_key=8913fe3dd006394120f45b515455875d" ;
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        //Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }
                        if (buffer.length() == 0) {
                            return null;
                        }
                        JSONResult = buffer.toString();
                        try {
                            results.add(getCommentsFromJSON(JSONResult));
                        } catch (JSONException E) {
                            return null;
                        }
                    } catch (Exception e) {
                        continue outerloop;

                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                            }
                        }
                    }
                }
                return results;

            }
        }
        public ArrayList<String> getCommentsFromJSON(String JSONStringParam)throws JSONException{
            JSONObject JSONString = new JSONObject(JSONStringParam);
            JSONArray reviewsArray = JSONString.getJSONArray("results");
            ArrayList<String> results = new ArrayList<>();
            if(reviewsArray.length()==0)
            {
                results.add("No reviews found for this movie.");
                return results;
            }
            for(int i = 0; i<reviewsArray.length(); i++)
            {
                JSONObject result = reviewsArray.getJSONObject(i);
                results.add(result.getString("content"));
            }
            return results;

        }
        public String getYoutubeFromJSON(String JSONStringParam, int position) throws JSONException
        {
            JSONObject JSONString = new JSONObject(JSONStringParam);
            JSONArray youtubesArray = JSONString.getJSONArray("results");
            JSONObject youtube;
            String result = "no videos found";
            if(position ==0)
            {
                youtube = youtubesArray.getJSONObject(0);
                result = youtube.getString("key");
            }
            else if(position==1)
            {
                if(youtubesArray.length()>1)
                {
                    youtube = youtubesArray.getJSONObject(1);
                }
                else{
                    youtube = youtubesArray.getJSONObject(0);
                }
                result = youtube.getString("key");
            }
            return result;
        }
        public String[] getStringsFromJSON(String JSONStringParam, String param)  throws JSONException
        {
            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                if(param.equals("vote_average"))
                {
                    Double number = movie.getDouble("vote_average");
                    String rating =Double.toString(number)+"/10";
                    result[i]=rating;
                }
                else {
                    String data = movie.getString(param);
                    result[i] = data;
                }
            }
            return result;
        }
        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException {

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }
    }
}

