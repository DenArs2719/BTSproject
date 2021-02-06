package com.example.btsproject.data;


import androidx.room.Entity;
import androidx.room.Ignore;

import com.example.btsproject.model.Result;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie extends Result
{
    public FavouriteMovie(Boolean adult, String backdropPath, Integer id, String originalLanguage, String originalTitle, String overview, Double popularity, String posterPath, String releaseDate, String title, Boolean video, Double voteAverage, Integer voteCount) {
        super(adult,backdropPath,id,originalLanguage,originalTitle,overview,popularity,posterPath,releaseDate,title,video,voteAverage,voteCount);
    }

    @Ignore
    public FavouriteMovie(Result movie)
    {
        super(movie.getAdult(),movie.getBackdropPath(),movie.getId(),movie.getOriginalLanguage(),movie.getOriginalTitle(),movie.getOverview(),movie.getPopularity(),movie.getPosterPath(),movie.getReleaseDate(),movie.getTitle(),movie.getVideo(),movie.getVoteAverage(),movie.getVoteCount());
    }

}
