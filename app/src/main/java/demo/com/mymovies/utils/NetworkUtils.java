package demo.com.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    private static final String Base_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";




    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_API_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_VOTE_COUNTS = "vote_count.gte";

    private static final String API_KEY = "d062dad3ffd1ef341f40e5d8bac413c2";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;


    public static URL buildURLToVideos(int id,String lang){
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS,id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .appendQueryParameter(PARAMS_API_LANGUAGE,lang).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static URL buildURLToReviews(int id,String lang){
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS,id)).buildUpon()
                .appendQueryParameter(PARAMS_API_LANGUAGE,lang)
                .appendQueryParameter(PARAMS_API_KEY,API_KEY).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static JSONObject getJSONForVideos(int id,String lang){

        JSONObject result = null;
        URL url = buildURLToVideos(id,lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static JSONObject getJSONForReviews(int id,String lang){

        JSONObject result = null;
        URL url = buildURLToReviews(id,lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static URL buildURL(int sortBy,int page,String lang){
        URL result = null;

        String methodOfSort;
        if (sortBy == POPULARITY){
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }

        Uri uri = Uri.parse(Base_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .appendQueryParameter(PARAMS_API_LANGUAGE,lang)
                .appendQueryParameter(PARAMS_SORT_BY,methodOfSort)
                .appendQueryParameter(PARAMS_VOTE_COUNTS,MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE,Integer.toString(page))
                .build();

        try {
             result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static JSONObject getJSONFromNetwork(int sortBy,int page,String lang){

        JSONObject result = null;
        URL url = buildURL(sortBy,page,lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }



    public static class JSONLoader extends AsyncTaskLoader<JSONObject>{

       private Bundle bundle;

       private onStartLoadingListener onStartLoadingListener;


       public interface onStartLoadingListener{

           void onStartLoading();

        }

        public void setOnStartLoadingListener(JSONLoader.onStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {

           if (onStartLoadingListener != null){
               onStartLoadingListener.onStartLoading();
           }
           forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
           if (bundle == null){
               return  null;
           }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            JSONObject result = null;

            if (url == null ){
                return null;
            }

            HttpsURLConnection connection = null;

            try {

                connection = (HttpsURLConnection)url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();

                StringBuilder builder = new StringBuilder();

                while (line != null){
                    builder.append(line);
                    line = reader.readLine();
                }

                result = new JSONObject(builder.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }


            return result;
        }
    }


    private static class JSONLoadTask extends AsyncTask<URL,Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(URL... urls) {

            JSONObject result = null;

            if (urls == null || urls.length == 0){
                return result;
            }

            HttpsURLConnection connection = null;

            try {

                connection = (HttpsURLConnection)urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();

                StringBuilder builder = new StringBuilder();

                while (line != null){
                    builder.append(line);
                    line = reader.readLine();
                }

                result = new JSONObject(builder.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }


            return result;
        }
    }

}
