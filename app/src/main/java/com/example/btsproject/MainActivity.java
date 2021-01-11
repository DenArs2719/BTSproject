package com.example.btsproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
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



import com.example.btsproject.adapters.MovieAdapter;
import com.example.btsproject.data.MainViewModel;
import com.example.btsproject.data.Movie;
import com.example.btsproject.utils.JSONUtils;
import com.example.btsproject.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject>
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

    ///метод для создания меню
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

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        adapter = new MovieAdapter();

        /// To load movies at the beginning
        switchSort.setChecked(true);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));

        /// Setting adapter to recyclerViewPosters
        recyclerViewPosters.setAdapter(adapter);


        /// Setting event listener
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                pageNumber = 1;
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);

        /// Movie item click event
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener()
        {
            @Override
            public void onPosterClick(int position)
            {
                /// getting movies from API
                Movie movie = adapter.getMovies().get(position);

                /// creating Intent
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("id",movie.getId());
                startActivity(intent);
            }
        });

        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener()
        {
            @Override
            public void onReachEnd()
            {
                if(!isLoading)
                {
                    downloadData(methodOfSort,pageNumber);
                }
            }
        });

        LiveData<List<Movie>> moviesFromLifeData = viewModel.getMovies();
        /// adding observer
        moviesFromLifeData.observe(this, new Observer<List<Movie>>()
        {
            /// Every time when data will be changed in database we will install it inside adapter
            @Override
            public void onChanged(List<Movie> movies)
            {
                /// If internet connection is not available
                if(pageNumber == 1)
                {
                    adapter.setMovies(movies);
                }

            }
        });

    }

    public void onClickSetPopularity(View view)
    {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }


    public void onClickSetTopRated(View view)
    {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated)
    {

        if(isTopRated)
        {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white_color));
        }
        else
        {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white_color));
        }
        downloadData(methodOfSort,pageNumber);

    }

    private void downloadData(int methodOfSort, int page)
    {
        /// Creating URL
        URL url = NetworkUtils.buildURL(methodOfSort,page);

        /// Creating bundle
        Bundle bundle = new Bundle();

        /// Inserting data
        bundle.putString("url",url.toString());

        /// Starting loader
        /// Method will check:
        /// if loader exists. It will be created if not.
        /// if loader exists it will be restarted.
        loaderManager.restartLoader(LOADER_ID,bundle,this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle)
    {
        /// Creating loader
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this,bundle);

        /// Creating event listener to loader(start of data load);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading()
            {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    /// End of data load
    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject)
    {
        /// Getting movies list
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        /// load new data if it appeared
        if(movies != null && !movies.isEmpty())
        {

            /// if movies were successfully loaded adapter database should be cleared
            if(pageNumber == 1)
            {
                /// clear previous data
                viewModel.deleteAllMovies();
                adapter.clear();
            }

            for(Movie movie: movies)
            {
                /// insert new data
                viewModel.insertMovie(movie);
            }

            adapter.addMovies(movies);
            pageNumber++;
            progressBarLoading.setVisibility(View.INVISIBLE);
        }

        /// Deleting loader after data load
        loaderManager.destroyLoader(LOADER_ID);

        /// is data load completed
        isLoading = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}
