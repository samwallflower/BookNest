package com.andromeda.booknest.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.andromeda.booknest.model.BookNote;
import com.andromeda.booknest.model.FavoriteBook;
import com.andromeda.booknest.model.ReadingListBook;

// everytime you change the schema increase the version number pleaseeee
@Database(
        entities = {
                FavoriteBook.class,
                ReadingListBook.class,
                BookNote.class
        },
        version = 1,
        exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase instance;
    public abstract FavoriteBookDao favoriteBookDao();
    public abstract ReadingListDao readingListDao();
    public abstract BookNoteDao bookNoteDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "booknest_database"
            )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
