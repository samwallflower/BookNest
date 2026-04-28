package com.andromeda.booknest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.model.SearchResult;
import com.bumptech.glide.Glide;
import com.andromeda.booknest.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    public interface OnBookClickListener {
        void onBookClick(SearchResult book);
        void onFavoriteClick(SearchResult book);
    }
    private List<SearchResult> books = new ArrayList<>();
    private final OnBookClickListener listener;

    public SearchAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<SearchResult> books) {
        this.books = books;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getFirstAuthor());
        holder.year.setText(book.getPublishYear());

        if(book.getCoverUrl()!=null){
            Glide.with(holder.cover.getContext())
                    .load(book.getCoverUrl())
                    .into(holder.cover);
        }else {
            holder.cover.setImageResource(R.drawable.ic_book_placeholder);
        }
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(book));

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover, btnFavorite;
        TextView title, author, year;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imgCover);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            title = itemView.findViewById(R.id.tvTitle);
            author = itemView.findViewById(R.id.tvAuthor);
            year = itemView.findViewById(R.id.tvYear);
        }
    }
}
