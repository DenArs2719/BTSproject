package com.example.btsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.btsproject.adapters.MovieAdapter;
import com.example.btsproject.data.MainViewModel;
import com.example.btsproject.model.Cast;
import com.example.btsproject.model.Crew;
import com.example.btsproject.model.Example;
import com.example.btsproject.model.MovieApiResponse;
import com.example.btsproject.model.Result;
import com.example.btsproject.model.personByName.ExampleName;
import com.example.btsproject.model.personByName.KnownFor;
import com.example.btsproject.service.MovieApiService;
import com.example.btsproject.service.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

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

    ///метод для создания меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    /// Method for menu element click events
    /*@Override
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
        }
        return super.onOptionsItemSelected(item);
    }*/

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
                Toast.makeText(MainActivity.this, ""+position, Toast.LENGTH_SHORT).show();
            }
        });



        recyclerViewPosters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(MainActivity.this, "Load next page", Toast.LENGTH_SHORT).show();
                    getPopularMovies(++page_counter);
                    //Log.i("ttag", "Load next page");
                }
            }
        });


        //BELOW TESTING RETROFIT ***************
        //getPopularMovies(page_counter);
        //getMovieByTitle(page_counter, "Matrix");

        //getMovieById(543);
        //Log.i("ttag", "test");

        //END TESTING  1146
        //getMovieByPersonId(1158);

        // *****************************ПОИСК ФИЛЬМОВ В КОТОРІХ СНИМАЛСЯ АКТЕР
        getMovieByPersonName("Al Pacino");
    }

    public void getMovieByPersonId(int person_id) {
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<Example> call = movieApiService.getMovieByPersonId(person_id, getString(R.string.api_key));
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                /*List<Cast>  list =  response.body().getCast();
                for(Cast crew : list) {
                    Log.i("ttag", crew.getBackdropPath()+"");
                    if(crew.getBackdropPath() != null) {
                        Result res = new Result();
                        res.setPosterPath(crew.getPosterPath());
                        results.add(res);
                    }
                }*/
                //results.addAll((ArrayList<Result>) movieApiResponse.getResults());
                    //Log.i("ttag", movieApiResponse.getResults().get(0).getBackdropPath());
                    results.addAll((ArrayList<Result>) response.body().getCrew());
                    adapter.setMovies(results);

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });
    }
    public void getMovieByPersonName(String name) {
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<MovieApiResponse> call = movieApiService.getMovieByPersonName(getString(R.string.api_key), name);
        call.enqueue(new Callback<MovieApiResponse>() {
            @Override
            public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                //Log.i("tag", response.body().getResults().get(0).getId().toString());
                getMovieByPersonId(response.body().getResults().get(0).getId());

            }

            @Override
            public void onFailure(Call<MovieApiResponse> call, Throwable t) {

            }
        });
    }

    public void getMovieById(int key) {
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<Result> call = movieApiService.getMovieById(key, getString(R.string.api_key));
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                //Toast.makeText(getApplicationContext(), response.body().getTitle()+" "+response.body().getOriginalLanguage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
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
