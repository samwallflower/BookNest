package com.andromeda.booknest;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andromeda.booknest.database.AppDatabase;
import com.andromeda.booknest.model.FavoriteBook;
import com.andromeda.booknest.model.ReadingListBook;
import com.bumptech.glide.Glide;

import com.google.android.material.button.MaterialButton;

import java.util.concurrent.Executors;

public class BookDetailActivity extends AppCompatActivity {
    private String bookId;
    private String title;
    private String author;
    private String coverUrl;
    private String year;
    private String description;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // retrieveing data from previous screen

        bookId = getIntent().getStringExtra("bookId");
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");
        coverUrl = getIntent().getStringExtra("coverUrl");
        year = getIntent().getStringExtra("year");
        description = getIntent().getStringExtra("description");

        // Bind views

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvDescription = findViewById(R.id.tvDescription);
        ImageView imgCover = findViewById(R.id.imgCover);
        MaterialButton btnFavorite = findViewById(R.id.btnFavorite);
        MaterialButton btnReadingList = findViewById(R.id.btnReadingList);

        tvTitle.setText(title);
        tvAuthor.setText("by "+author);
        tvYear.setText("First published: "+(year!=null ? year : "N/A"));
        tvDescription.setText(description!=null && !description.isEmpty() ?
                description : "No description available for this book");

        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .into(imgCover);

        // add to favorites button

        btnFavorite.setOnClickListener(v -> {
            FavoriteBook fav = new FavoriteBook(
                    bookId,
                    title,
                    author,
                    coverUrl,
                    description !=null ? description : "",
                    year
            );
            Executors.newSingleThreadExecutor().execute(()->{
                int exists = AppDatabase.getInstance(this)
                        .favoriteBookDao()
                        .isFavorite(bookId);
                if(exists>0){
                    runOnUiThread(() -> Toast.makeText(this,
                            "Already in Favorites", Toast.LENGTH_SHORT).show());
                }else{
                    AppDatabase.getInstance(this)
                            .favoriteBookDao()
                            .insert(fav);
                    runOnUiThread(() -> Toast.makeText(this,
                            "Book added to Favorites", Toast.LENGTH_SHORT).show());
                }

            });
        });

        // Add to Reading List button
        btnReadingList.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(()-> {
                int exists = AppDatabase.getInstance(this)
                        .readingListDao()
                        .isInReadingList(bookId);
                if (exists > 0) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "Already in Reading List", Toast.LENGTH_SHORT).show());
                } else {
                    int maxPos = AppDatabase.getInstance(this)
                            .readingListDao()
                            .getMaxPosition();

                    ReadingListBook rlBook = new ReadingListBook(
                            bookId,
                            title,
                            author,
                            coverUrl,
                            maxPos + 1

                    );
                    AppDatabase.getInstance(this)
                            .readingListDao()
                            .insert(rlBook);
                    runOnUiThread(() -> Toast.makeText(this,
                            "Book added to Reading List", Toast.LENGTH_SHORT).show());

                }
            });
        });

    }
}
