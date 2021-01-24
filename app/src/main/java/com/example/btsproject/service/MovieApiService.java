package com.example.btsproject.service;

import com.example.btsproject.model.MovieApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
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


}
