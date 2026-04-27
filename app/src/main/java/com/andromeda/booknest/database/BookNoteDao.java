package com.andromeda.booknest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.andromeda.booknest.model.BookNote;

import java.util.List;

@Dao
public interface BookNoteDao {
    @Insert
    void insert(BookNote note);
    @Delete
    void delete(BookNote note);
    @Query("SELECT * FROM book_notes ORDER BY id DESC")
    LiveData<List<BookNote>> getAllNotes();
    @Query("DELETE FROM book_notes WHERE id = :id")
    void deleteById(int id);

}
