package com.example.btsproject.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.btsproject.model.Result;

@Database(entities = {Result.class,FavouriteMovie.class},version = 11,exportSchema = false)
public abstract class MovieDataBase extends RoomDatabase
{
    private static MovieDataBase dataBase;
    private static final String DB_NAME = "resultTables.db";
    private static final Object LOCK = new Object();

    ///use SINGLETON
    public static MovieDataBase getInstance(Context context)
    {
        ///for multithreading
        synchronized (LOCK) {
            if (dataBase == null) {
                dataBase = Room.databaseBuilder(context, MovieDataBase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return dataBase;
    }

    public abstract MovieDao movieDao();
}
