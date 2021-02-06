package com.example.btsproject.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.btsproject.model.Result;

import java.util.List;

@Dao
public interface MovieDao
{
    @Query("SELECT * FROM resultTable")
    LiveData<List<Result>> getAllMovies();

    @Query("SELECT * FROM resultTable WHERE id == :movieId")
    Result getMovieById(int movieId);

    @Query("DELETE FROM resultTable")
    void deleteAllMovies();

    @Insert
    void insertMovie(Result movie);

    @Delete
    void deleteMovie(Result movie);

    ///работа с избронными фильмами
    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllMoviesFromFavourites();

    @Query("SELECT * FROM favourite_movies WHERE id == :movieId ")
    FavouriteMovie getFavouriteMovieById(int movieId);

    @Insert
    void insertMovieToFavourite(FavouriteMovie movie);

    @Delete
    void deleteMovieFromFavourite(FavouriteMovie movie);


}