package com.example.btsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.btsproject.adapters.MovieAdapter;
import com.example.btsproject.data.MainViewModel;
import com.example.btsproject.model.Example;
import com.example.btsproject.model.MovieApiResponse;
import com.example.btsproject.model.Result;
import com.example.btsproject.service.MovieApiService;
import com.example.btsproject.service.RetrofitInstance;

import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity //implements LoaderManager.LoaderCallbacks<JSONObject>
{
    private RecyclerView recyclerViewPosters;
    private MovieAdapter adapter;
    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewTopRated;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 123;
    private LoaderManager loaderManager;

    private int pageNumber = 1;
    private static boolean isLoading = false;
    private static  int methodOfSort;

    private ProgressBar progressBarLoading;

    //retrofit test below
    private ArrayList<Result> results = new ArrayList<>();
    private int page_counter = 1;

    ///method for creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /// Method for menu element click events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        /// Get clicked element id
        int itemId = item.getItemId();

        /// Is clicked element favourite or chosen from menu
        switch (itemId)
        {
            case R.id.itemMain:
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this,FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;

            case R.id.itemSearch:
                Intent intentToSearch = new Intent(this,SearchActivity.class);
                startActivity(intentToSearch);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Singleton pattern
        /// Manages all media loads in app
        loaderManager = LoaderManager.getInstance(this);


        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);


        /// Setting event listener
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                //pageNumber = 1;
                Toast.makeText(MainActivity.this, String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
                results.clear();
                //setMethodOfSort(isChecked);
                if(isChecked) {
                    getTopRatedMovies(page_counter);
                }
                else {
                    getPopularMovies(page_counter);
                }
            }
        });
        switchSort.setChecked(false);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MovieAdapter();
        adapter.setMovies(results);
        recyclerViewPosters.setAdapter(adapter);


        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                ///получаем фильм на который нажали
                Result movie = adapter.getMovies().get(position);
                Toast.makeText(MainActivity.this, ""+position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this,DetailActivity.class);

                intent.putExtra("id",movie.getId());

                startActivity(intent);

            }
        });



        recyclerViewPosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(MainActivity.this, "Load next page", Toast.LENGTH_SHORT).show();
                    getPopularMovies(++page_counter);
                }
            }
        });

        getPopularMovies(++page_counter);

    }




    //Retrofit method
    //in next, these two method(below) will be transferred to (ViewModel) and in this (MainActivity) we will be signed to Observable variable from (ViewModel)
    public void getPopularMovies(int page) {
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<MovieApiResponse> call = movieApiService.getPopularMovies(getString(R.string.api_key), page);
        call.enqueue(new Callback<MovieApiResponse>() {
            //if load data is success
            @Override
            public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                MovieApiResponse movieApiResponse = response.body();
                if(movieApiResponse != null && movieApiResponse.getResults() != null) {
                    //Log.i("ttag", movieApiResponse.getResults().get(0).getBackdropPath());
                    results.addAll((ArrayList<Result>) movieApiResponse.getResults());
                    adapter.setMovies(results);
                    //fillRecyclerView();
                    for(Result movie : results)
                    {
                        viewModel.insertMovie(movie);
                    }
                }
            }
            //if got an error while loading data
            @Override
            public void onFailure(Call<MovieApiResponse> call, Throwable t) {

            }
        });
    }

    public void getTopRatedMovies(int page) {
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<MovieApiResponse> call = movieApiService.getTopRatedMovies(getString(R.string.api_key), page);
        call.enqueue(new Callback<MovieApiResponse>() {
            @Override
            public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                MovieApiResponse movieApiResponse = response.body();
                if(movieApiResponse != null && movieApiResponse.getResults() != null) {
                    results.addAll((ArrayList<Result>) movieApiResponse.getResults());
                    adapter.setMovies(results);
                    //fillRecyclerView();
                    for(Result movie : results)
                    {
                        viewModel.insertMovie(movie);
                    }

                }
            }

            @Override
            public void onFailure(Call<MovieApiResponse> call, Throwable t) {

            }
        });
    }

    public void getMovieByTitle(long page, String title) {
        MovieApiService movieApiResponse = RetrofitInstance.getService();
        Call<MovieApiResponse> call = movieApiResponse.searchMovieByTitle(getString(R.string.api_key), title, page);
        call.enqueue(new Callback<MovieApiResponse>() {
            @Override
            public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                MovieApiResponse movieApiResponse = response.body();
                if(movieApiResponse != null && movieApiResponse.getResults() != null) {
                    results.addAll((ArrayList<Result>) movieApiResponse.getResults());
                    adapter.setMovies(results);
                }
            }

            @Override
            public void onFailure(Call<MovieApiResponse> call, Throwable t) {

            }
        });
    }

}
