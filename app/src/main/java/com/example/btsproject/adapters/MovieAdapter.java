package com.example.btsproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btsproject.R;
import com.example.btsproject.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>
{
    private List<Movie> movies;


    private OnPosterClickListener onPosterClickListener;
    private OnReachEndListener onReachEndListener;

    public MovieAdapter()
    {
        movies = new ArrayList<>();
    }

    ///interface for processing touching to film(poster)
    public interface OnPosterClickListener
    {
        void onPosterClick(int position);
    }

    ///interface for load films,if user go to end of list (after it will load more films)
    public interface OnReachEndListener
    {
        void onReachEnd();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ///making our View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position)
    {
        ///add data, if user go to end of list (after it will load more films)
        if(movies.size()>=20 && position > movies.size() - 4 && onReachEndListener != null)
        {
            onReachEndListener.onReachEnd();
        }
        ///getting film
        Movie movie = movies.get(position);
        ///getting picture
        ImageView imageView = holder.imageViewSmallPoster;

        ///working with Picasso and download picture
        Picasso.get().load(movie.getPosterPath()).into(imageView);
    }

    @Override
    public int getItemCount()
    {
        return movies.size();
    }

    public void addMovies(List<Movie> movies)
    {
        this.movies.addAll(movies);

        ///when we add new elements,telling it to our adapter
        notifyDataSetChanged();
    }


    public List<Movie> getMovies()
    {
        return movies;
    }

    public void clear()
    {
        this.movies.clear();

        ///telling to our adapter about changes
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies)
    {
        this.movies = movies;

        ///when we set elements,telling it to our adapter
        notifyDataSetChanged();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener)
    {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener)
    {
        this.onReachEndListener = onReachEndListener;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(onPosterClickListener != null)
                    {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
