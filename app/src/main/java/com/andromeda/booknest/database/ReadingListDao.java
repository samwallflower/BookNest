package com.andromeda.booknest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.andromeda.booknest.model.ReadingListBook;

import java.util.List;

@Dao
public interface ReadingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReadingListBook book);
    @Query("DELETE FROM reading_list WHERE bookId = :bookId")
    void deleteById(String bookId);
    @Query("SELECT * FROM reading_list ORDER BY position ASC")
    LiveData<List<ReadingListBook>> getAllBooks();

    @Query("SELECT COUNT(*) FROM reading_list WHERE bookId = :bookId")
    int isInReadingList(String bookId);

    @Update
    void update(ReadingListBook book);

    @Query("SELECT MAX(position) FROM reading_list")
    int getMaxPosition();
}
