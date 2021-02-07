///activity who will show us all popular films
package com.example.btsproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btsproject.adapters.ReviewAdapter;
import com.example.btsproject.adapters.TrailerAdapter;
import com.example.btsproject.data.FavouriteMovie;
import com.example.btsproject.data.MainViewModel;
import com.example.btsproject.data.Movie;
import com.example.btsproject.data.Review;
import com.example.btsproject.data.Trailer;
import com.example.btsproject.model.Result;
import com.example.btsproject.utils.JSONUtils;
import com.example.btsproject.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity
{
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private Result movie;
    private ImageView imageViewAddToFavourite;

    private RecyclerView recyclerViewReviews;
    private RecyclerView recyclerViewTrailers;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private int id = 0;

    private MainViewModel viewModel;
    private FavouriteMovie favouriteMovie;


    ///петод для создания меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    ///метод для возможности нажатия на элементы меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        ///получаем id элемента на который нажали
        int itemId = item.getItemId();

        ///выбираем ,на что нажали
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
        setContentView(R.layout.activity_detail);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);

        ///получаем наши данные
        Intent intent = getIntent();

        if(intent != null && intent.hasExtra("id"))
        {
            id = intent.getIntExtra("id",-1);
        }
        else
        {
            ///закрываем активность,если ошибка
            finish();
        }
        Log.i("intent",String.valueOf(id));

        ///получаем наш  viewModel(который отвечает за фильм)
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        /// получаем наш фильм
        //movie = viewModel.getMovieById(id,getString(R.string.api_key));
        movie = viewModel.getMovieById(id);


        if(movie != null)
        {
            createMovieDetails(movie);
        }
        else
        {
            movie = viewModel.getFavouriteMovieById(id);
            createMovieDetails(movie);
        }


        setFavourite();

        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);

        reviewAdapter = new ReviewAdapter();
        trailerAdapter =  new TrailerAdapter();

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url)
            {
                ///используем неяный Intent , чтобы запустить трейлер через youtube
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewReviews.setAdapter(reviewAdapter);
        recyclerViewTrailers.setAdapter(trailerAdapter);

        loadReview(movie.getId());
        loadVideo(movie.getId());


    }

    private void createMovieDetails(Result movie)
    {
        ///устанавливаем значения
        Picasso.get().load("https://image.tmdb.org/t/p/w500"+movie.getPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));

    }

    ///метод для обработки нажатия на картинку ,для добавления фильма в избранное
    public void onClickChangeFavourite(View view)
    {
        ///значит,что фильма в базе данных нет
        if(favouriteMovie == null)
        {
            ///вставляем нужный обьект
            viewModel.insertMovieToFavourite(new FavouriteMovie(movie));
            Toast.makeText(com.example.btsproject.DetailActivity.this, R.string.added_to_favourite,Toast.LENGTH_SHORT).show();
        }
        else
        {
            viewModel.deleteMovieFromFavourite(favouriteMovie);
            Toast.makeText(com.example.btsproject.DetailActivity.this, R.string.deleted_from_favourite,Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite()
    {
        ///получаем фильм
        favouriteMovie = viewModel.getFavouriteMovieById(id);

        if(favouriteMovie == null)
        {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        }
        else
        {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }

    ///метод для загрузки отзывов
    private void loadReview(int filmId)
    {
        ///получем отзывы в формате JSON
        JSONObject jsonObject = NetworkUtils.getJSONReviewForVideo(filmId);

        ///получем список отзывов
        ArrayList<Review> reviews = JSONUtils.getReviewInfoFromJSON(jsonObject);

        ///устанавливаем отзывы в адаптере
        reviewAdapter.setReviews(reviews);

    }

    ///метод для загрузки трейлеров
    private void loadVideo(int filmId)
    {
        ///получем  трейлеры
        JSONObject jsonObject = NetworkUtils.getJSONForVideo(filmId);

        ///получем список трейлеров
        ArrayList<Trailer>  trailers = JSONUtils.getTrailerFromJSON(jsonObject);

        ///устанавливаем трейлеры в адаптер
        trailerAdapter.setTrailers(trailers);

    }

}