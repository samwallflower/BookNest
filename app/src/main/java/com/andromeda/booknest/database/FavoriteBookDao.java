package com.andromeda.booknest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andromeda.booknest.model.FavoriteBook;

import java.util.List;

@Dao
public interface FavoriteBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteBook book);
    @Delete
    void delete(FavoriteBook book);
    @Query("SELECT * FROM favorite_books")
    LiveData<List<FavoriteBook>> getAllFavoriteBooks();
    @Query("SELECT COUNT(*) FROM favorite_books WHERE bookId = :bookId")
    int isFavorite(String bookId);
    @Query("DELETE FROM favorite_books WHERE bookId = :bookId")
    void deleteById(String bookId);
}
