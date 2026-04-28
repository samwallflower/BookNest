package com.andromeda.booknest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.model.BookNote;
import com.andromeda.booknest.R;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<BookNote> notes = new ArrayList<>();

    public BookNote getNoteAt(int position){
        return notes.get(position);
    }

    public void setNotes(List<BookNote> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookNote note = notes.get(position);
        holder.bookTitle.setText(note.getBookTitle());
        holder.noteText.setText(note.getNoteText());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, noteText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.tvBookTitle);
            noteText = itemView.findViewById(R.id.tvNoteText);
        }
    }
}
