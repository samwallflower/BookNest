package com.andromeda.booknest.ui.favorites;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.BookDetailActivity;
import com.andromeda.booknest.adapter.FavoritesAdapter;
import com.andromeda.booknest.database.AppDatabase;
import com.andromeda.booknest.model.FavoriteBook;
import com.andromeda.booknest.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {
    private FavoritesAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        adapter = new FavoritesAdapter(book -> {
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("bookId", book.getBookId());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("coverUrl", book.getCoverUrl());
            intent.putExtra("publishYear", book.getPublishYear());
            intent.putExtra("description", book.getDescription());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Observe LiveData from Room

        AppDatabase.getInstance(requireContext())
                .favoriteBookDao()
                .getAllFavoriteBooks()
                .observe(getViewLifecycleOwner(), books -> {
                    adapter.setBooks(books);
                    tvEmpty.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
                });

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                FavoriteBook deletedBook = adapter.getBookAt(position);
                //Delete from database
                Executors.newSingleThreadExecutor().execute(()->
                        AppDatabase.getInstance(requireContext())
                                .favoriteBookDao()
                                .deleteById(deletedBook.getBookId()));


                // Snackbar with undo

                Snackbar.make(view, "\""+deletedBook.getTitle()+"\" removed from Favorites", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v ->
                                Executors.newSingleThreadExecutor().execute(()->
                                        AppDatabase.getInstance(requireContext())
                                                .favoriteBookDao()
                                                .insert(deletedBook)))
                        .show();

            }

            // Draw red bg while swiping
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                ColorDrawable bg = new ColorDrawable(Color.parseColor("#F44336"));
                if(dX <0){
                    bg.setBounds(itemView.getRight() + (int)dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                }else{
                    bg.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int)dX, itemView.getBottom());
                }
                bg.draw(c);
            }

        }).attachToRecyclerView(recyclerView);

    }
}
