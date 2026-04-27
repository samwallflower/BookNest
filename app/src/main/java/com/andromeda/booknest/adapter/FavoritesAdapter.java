package com.andromeda.booknest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.model.FavoriteBook;
import com.bumptech.glide.Glide;
import com.example.booknest.R;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    public interface OnFavoriteClickListener {
        void onBookClick(FavoriteBook book);
    }

    private List<FavoriteBook> books = new ArrayList<>();
    private final OnFavoriteClickListener listener;

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<FavoriteBook> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    // called by ItemTouchHelper when user swipes to delete
    public FavoriteBook getBookAt(int position) {
        return books.get(position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteBook book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.year.setText(book.getPublishYear());

        Glide.with(holder.cover.getContext())
                .load(book.getCoverUrl())
                .placeholder(R.drawable.ic_book_placeholder)
                .into(holder.cover);

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, author, year;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imgCover);
            title = itemView.findViewById(R.id.tvTitle);
            author = itemView.findViewById(R.id.tvAuthor);
            year = itemView.findViewById(R.id.tvYear);
        }
    }
}
