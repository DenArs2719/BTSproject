package com.example.btsproject;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchActivity extends AppCompatActivity
{
    private EditText actorName;
    private ImageButton search;

    private MainViewModel viewModel;
    private ArrayList<Result> results = new ArrayList<>();
    private MovieAdapter adapter;
    private RecyclerView recyclerViewPosters;

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
        setContentView(R.layout.activity_search);

        actorName = findViewById(R.id.actorName);
        search = findViewById(R.id.buttonFind);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);

        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MovieAdapter();
        adapter.setMovies(results);
        recyclerViewPosters.setAdapter(adapter);

        // Event listener on "enter" on keyboard
        actorName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO) {
                    search.performClick();
                    // Hide keyboard
                    closeKeyboard();
                    actorName.clearFocus();
                    return true;
                }
                return false;
            }
        });

        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                ///получаем фильм на который нажали
                Result movie = adapter.getMovies().get(position);
                Toast.makeText(SearchActivity.this, ""+position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SearchActivity.this,DetailActivity.class);

                intent.putExtra("id",movie.getId());

                startActivity(intent);

            }
        });


    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void onClickToSearch(View view)
    {
        if(actorName.getText().toString().equals(""))
        {
            adapter.clear();
            results.clear();
        }
        else {
            adapter.clear();
            results.clear();
            getMovieByPersonName(actorName.getText().toString());
        }
        closeKeyboard();
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

                for(Result movie: results)
                {
                    viewModel.insertMovie(movie);
                }
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
            public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response)
            {
                getMovieByPersonId(response.body().getResults().get(0).getId());
            }

            @Override
            public void onFailure(Call<MovieApiResponse> call, Throwable t) {

            }
        });
    }




}
