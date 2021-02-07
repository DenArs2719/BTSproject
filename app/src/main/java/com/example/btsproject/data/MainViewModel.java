package com.example.btsproject.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.btsproject.model.Result;
import com.example.btsproject.service.MovieApiService;
import com.example.btsproject.service.RetrofitInstance;

import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel
{
    private static MovieDataBase dataBase;

    private LiveData<List<Result>> movies;
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
    public Result getMovieById(int movieId,String api)
    {
        Result r = new Result();
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<Result> call = movieApiService.getMovieById(movieId,api);
        call.enqueue(new Callback<Result>()
        {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response)
            {
                r.setOriginalTitle(response.body().getOriginalTitle());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t)
            {

            }
        });

        Log.i("info",r.getOriginalTitle());
        return r;
    }

    ///метод для получения фильма по id
    public Result getMovieById(int movieId)
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
    public void insertMovie(Result movie)
    {
        new InsertMoviesTask().execute(movie);
    }

    ///method for delete movie
    public void deleteMovie(Result movie)
    {
        new DeleteTask().execute(movie);
    }

    ///class for thread, to take movie from database
    private static class GetMovieTask extends AsyncTask<Integer,Void,Result>
    {
        @Override
        protected Result doInBackground(Integer... integers) {
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
    private static class InsertMoviesTask extends AsyncTask<Result, Void, Void>
    {

        @Override
        protected Void doInBackground(Result... movies)
        {
            if(movies != null && movies.length > 0)
            {
                dataBase.movieDao().insertMovie(movies[0]);
            }

            return null;
        }
    }

    ///class for thread, to delete movie from database
    private static class DeleteTask extends AsyncTask<Result, Void, Void>
    {

        @Override
        protected Void doInBackground(Result... movies)
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


    public LiveData<List<Result>> getMovies()
    {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies()
    {
        return favouriteMovies;
    }
}
