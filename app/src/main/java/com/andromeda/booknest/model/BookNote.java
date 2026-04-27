package com.andromeda.booknest.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "book_notes")
public class BookNote {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String bookId;
    private String bookTitle;
    private String noteText;

    public BookNote(String bookId, String bookTitle, String noteText) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.noteText = noteText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
