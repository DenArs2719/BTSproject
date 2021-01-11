package com.example.btsproject.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel
{
    private static MovieDataBase dataBase;

    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;

    public MainViewModel(@NonNull Application application)
    {
        super(application);

        ///get out database
        dataBase = MovieDataBase.getInstance(getApplication());

        ///method automatically will work in other thread
        movies = dataBase.movieDao().getAllMovies();
        favouriteMovies = dataBase.movieDao().getAllMoviesFromFavourites();
    }


    ///method for get movie by id
    public Movie getMovieById(int movieId)
    {
        try {
            return new GetMovieTask().execute(movieId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    ///method for delete movies
    public void deleteAllMovies()
    {
        new DeleteMoviesTask().execute();
    }

    ///method for insert movie
    public void insertMovie(Movie movie)
    {
        new InsertMoviesTask().execute(movie);
    }

    ///method for delete movie
    public void deleteMovie(Movie movie)
    {
        new DeleteTask().execute(movie);
    }

    ///class for thread, to take movie from database
    private static class GetMovieTask extends AsyncTask<Integer,Void,Movie>
    {
        @Override
        protected Movie doInBackground(Integer... integers) {
            if(integers != null && integers.length > 0 )
            {
                return dataBase.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    ///class for thread, to delete movie from database
    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            dataBase.movieDao().deleteAllMovies();
            return null;
        }
    }

    ///class for thread, to insert movie in database
    private static class InsertMoviesTask extends AsyncTask<Movie, Void, Void>
    {

        @Override
        protected Void doInBackground(Movie... movies)
        {
            if(movies != null && movies.length > 0)
            {
                dataBase.movieDao().insertMovie(movies[0]);
            }

            return null;
        }
    }

    ///class for thread, to delete movie from database
    private static class DeleteTask extends AsyncTask<Movie, Void, Void>
    {

        @Override
        protected Void doInBackground(Movie... movies)
        {
            if (movies != null && movies.length > 0) {
                dataBase.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }


    ///work with database favourite_movies

    ///method for inset movie
    public void insertMovieToFavourite(FavouriteMovie movie)
    {
        new InsertIntoFavoriteTask().execute(movie);
    }

    ///method for delete movie
    public void deleteMovieFromFavourite(FavouriteMovie movie)
    {
        new DeleteFromFavouriteTask().execute(movie);
    }

    ///method for get movie by id
    public FavouriteMovie getFavouriteMovieById(int movieId)
    {
        try {
            return new GetFavouriteMovieTask().execute(movieId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    ///class for thread, to get favourite movie from database
    private static class GetFavouriteMovieTask extends AsyncTask<Integer,Void,FavouriteMovie>
    {

        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if(integers != null && integers.length > 0 )
            {
                return dataBase.movieDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    ///class for thread, to insert favourite movie in database
    private static class InsertIntoFavoriteTask extends AsyncTask<FavouriteMovie, Void, Void>
    {

        @Override
        protected Void doInBackground(FavouriteMovie... movies)
        {
            if(movies != null && movies.length > 0)
            {
                dataBase.movieDao().insertMovieToFavourite(movies[0]);
            }

            return null;
        }
    }

    ///class for thread, to delete favourite movie from database
    private static class DeleteFromFavouriteTask extends AsyncTask<FavouriteMovie, Void, Void>
    {

        @Override
        protected Void doInBackground(FavouriteMovie... movies)
        {
            if (movies != null && movies.length > 0) {
                dataBase.movieDao().deleteMovieFromFavourite(movies[0]);
            }
            return null;
        }
    }


    public LiveData<List<Movie>> getMovies()
    {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies()
    {
        return favouriteMovies;
    }
}
