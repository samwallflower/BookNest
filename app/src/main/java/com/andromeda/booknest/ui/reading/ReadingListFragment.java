package com.andromeda.booknest.ui.reading;

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
import com.andromeda.booknest.adapter.ReadingListAdapter;
import com.andromeda.booknest.database.AppDatabase;
import com.andromeda.booknest.model.ReadingListBook;
import com.example.booknest.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executors;

public class ReadingListFragment extends Fragment {
    private ReadingListAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reading_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        adapter = new ReadingListAdapter(new ReadingListAdapter.OnReadingListListener() {
            @Override
            public void onBookClick(ReadingListBook book) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra("bookId", book.getBookId());
                intent.putExtra("title", book.getTitle());
                intent.putExtra("author", book.getAuthor());
                intent.putExtra("coverUrl", book.getCoverUrl());
                startActivity(intent);

            }

            @Override
            public void onItemMoved(List<ReadingListBook> newList) {
                // Persist new order to Room
                Executors.newSingleThreadExecutor().execute(() -> {
                    for(ReadingListBook b : newList){
                        AppDatabase.getInstance(requireContext())
                                .readingListDao().update(b);
                    }
                });
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getBindingAdapterPosition();
                int to = target.getBindingAdapterPosition();
                adapter.onItemMove(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                ReadingListBook book = adapter.getBookAt(position);
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getInstance(requireContext())
                            .readingListDao().deleteById(book.getBookId());
                });

                Snackbar.make(view, "\""+book.getTitle()+"\" removed from reading list", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v ->
                                Executors.newSingleThreadExecutor().execute(()->
                                        AppDatabase.getInstance(requireContext())
                                                .readingListDao()
                                                .insert(book)))
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isActive){
                super.onChildDraw(c,recyclerView, viewHolder, dX, dY, actionState, isActive);
                View itemView = viewHolder.itemView;
                ColorDrawable bg = new ColorDrawable(Color.parseColor("#F44336"));
                if(dX>0){
                    bg.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int)dX, itemView.getBottom());
                } else if(dX<0){
                    bg.setBounds(itemView.getRight() + (int)dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                } else {
                    return;
                }
                bg.draw(c);

            }
        });

        touchHelper.attachToRecyclerView(recyclerView);
        adapter.setTouchHelper(touchHelper);

        //Observe Room LiveData
        AppDatabase.getInstance(requireContext())
                .readingListDao()
                .getAllBooks()
                .observe(getViewLifecycleOwner(), books->{
                    adapter.setBooks(books);
                    tvEmpty.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
                });


    }
}
