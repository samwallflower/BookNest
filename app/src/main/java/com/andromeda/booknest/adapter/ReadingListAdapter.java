package com.andromeda.booknest.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.model.ReadingListBook;
import com.bumptech.glide.Glide;
import com.andromeda.booknest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadingListAdapter extends RecyclerView.Adapter<ReadingListAdapter.ViewHolder> {

    public interface OnReadingListListener {
        void onBookClick(ReadingListBook book);
        void onItemMoved(List<ReadingListBook> newList);
    }

    private List<ReadingListBook> books = new ArrayList<>();
    private final OnReadingListListener listener;
    private ItemTouchHelper touchHelper;

    public ReadingListAdapter(OnReadingListListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<ReadingListBook> books) {
        this.books = new ArrayList<>(books);
        notifyDataSetChanged();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    public ReadingListBook getBookAt(int position) {
        return books.get(position);
    }

    // called by ItemTouchHelper when drag-drop finishes

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(books, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        // update the positions
        for (int i = 0; i < books.size(); i++)
            books.get(i).setPosition(i);

        listener.onItemMoved(books);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reading_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadingListBook book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());

        Glide.with(holder.cover.getContext())
                .load(book.getCoverUrl())
                .placeholder(R.drawable.ic_book_placeholder)
                .into(holder.cover);

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));

        // Start drag when drag handle is clicked
        holder.dragHandle.setOnTouchListener((v,event)->{
            if(event.getActionMasked() == MotionEvent.ACTION_DOWN && touchHelper!=null){
            touchHelper.startDrag(holder);
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover, dragHandle;
        TextView title, author;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imgCover);
            dragHandle = itemView.findViewById(R.id.imgDragHandle);
            title = itemView.findViewById(R.id.tvTitle);
            author = itemView.findViewById(R.id.tvAuthor);
        }
    }
}
