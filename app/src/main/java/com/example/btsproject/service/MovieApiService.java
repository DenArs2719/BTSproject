package com.example.btsproject.service;

import com.example.btsproject.model.Example;
import com.example.btsproject.model.MovieApiResponse;
import com.example.btsproject.model.Result;
import com.example.btsproject.model.personByName.ExampleName;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("movie/popular")
    Call<MovieApiResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") long page);

    @GET("movie/top_rated")
    Call<MovieApiResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") long page);

    @GET("search/movie")
    Call<MovieApiResponse> searchMovieByTitle(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") long page
    );

    @GET("movie/{movie_id}?")
    Call<Result> getMovieById(
            @Path("movie_id") int movie,
            @Query("api_key") String api_key
    );

    //https://api.themoviedb.org/3/person/1158/movie_credits?api_key=0936d8c770393bd195267285dea65d1d&language=en-US
    //person/{person_id}/movie_credits
    //
    @GET("person/{person_id}/movie_credits?")
    Call<Example> getMovieByPersonId(
            @Path("person_id") int movie,
            @Query("api_key") String api_key
    );


    //https://api.themoviedb.org/3/search/person?api_key=0936d8c770393bd195267285dea65d1d&language=en-US&query=al&page=1&include_adult=false
    ///search/person
    //
    @GET("search/person")
    Call<MovieApiResponse> getMovieByPersonName(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

}
