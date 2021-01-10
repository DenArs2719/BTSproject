package com.example.btsproject.utils;


import com.example.btsproject.data.Movie;
import com.example.btsproject.data.Review;
import com.example.btsproject.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils
{
    //at this key we can get all of JSON Object, and put it into array
    private static  final String KEY_RESULTS = "results";
    private static  final String KEY_VOTE_COUNT = "vote_count";
    private static  final String KEY_TITLE = "title";
    private static  final String KEY_ID = "id";
    private static  final String KEY_ORIGINAL_TITLE = "original_title";
    private static  final String KEY_OVERVIEW = "overview";
    private static  final String KEY_POSTER_PATH = "poster_path";
    private static  final String KEY_BACKDROP_PATH = "backdrop_path";
    private static  final String KEY_VOTE_AVERAGE = "vote_average";
    private static  final String KEY_RELEASE_DATE = "release_date";


    ///for review
    private static final String KEY_CONTENT = "content";
    private static final String KEY_AUTHOR = "author";

    ///for video
    private static final String KEY_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    //for poster img
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    //get arrays of Movies
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject)
    {
        ArrayList<Movie> arrayList = new ArrayList<>();
        if(jsonObject == null)
        {
            return arrayList;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            for(int i = 0;i<jsonArray.length();i++)
            {
                ///get our data
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String tittle = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backDropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);

                ///created Movie object
                Movie movie = new Movie(id,voteCount,tittle,originalTitle,overview,posterPath,bigPosterPath,backDropPath,voteAverage,releaseDate);

                ///add object in our array
                arrayList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }


    //get arrays of Reviews
    public static ArrayList<Review> getReviewInfoFromJSON(JSONObject jsonObject)
    {
        ArrayList<Review> arrayList = new ArrayList<>();
        if(jsonObject == null)
        {
            return arrayList;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            ///проходимя по всем отзывам
            for(int i = 0;i<jsonArray.length();i++)
            {
                ///get our data
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                String author = objectMovie.getString(KEY_AUTHOR);
                String content = objectMovie.getString(KEY_CONTENT);

                ///created Movie object
                Review review = new Review(content,author);


                ///add object in our array
                arrayList.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    //get arrays of Trailers
    public static ArrayList<Trailer> getTrailerFromJSON(JSONObject jsonObject)
    {
        ArrayList<Trailer> arrayList = new ArrayList<>();
        if(jsonObject == null)
        {
            return arrayList;
        }
        try {
            ///create a jsonArray and get it by key
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);

            for(int i = 0;i<jsonArray.length();i++)
            {
                ///get our data
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                String video = BASE_YOUTUBE_URL + objectMovie.getString(KEY_VIDEO);
                String name = objectMovie.getString(KEY_NAME);

                ///created Movie object
                Trailer trailer = new Trailer(video,name);

                ///add object in our array
                arrayList.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
