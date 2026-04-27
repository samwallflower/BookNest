package com.andromeda.booknest.ui.notes;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.adapter.NotesAdapter;
import com.andromeda.booknest.database.AppDatabase;
import com.andromeda.booknest.model.BookNote;
import com.example.booknest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executors;

public class NotesFragment extends Fragment {
    private NotesAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        FloatingActionButton fab = view.findViewById(R.id.fabAddNote);
        adapter = new NotesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //Observe all notes from Room
        AppDatabase.getInstance(requireContext())
                .bookNoteDao()
                .getAllNotes()
                .observe(getViewLifecycleOwner(), notes -> {
                    adapter.setNotes(notes);
                    tvEmpty.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
                });

        // Swipe to delete note

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                BookNote deletedNote = adapter.getNoteAt(viewHolder.getBindingAdapterPosition());

                Executors.newSingleThreadExecutor().execute(()->
                        AppDatabase.getInstance(requireContext())
                                .bookNoteDao()
                                .deleteById(deletedNote.getId()));

                Snackbar.make(view, "Note deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v ->
                                Executors.newSingleThreadExecutor().execute(()->
                                        AppDatabase.getInstance(requireContext())
                                                .bookNoteDao()
                                                .insert(deletedNote)))
                        .show();
            }

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
                } else{
                    bg.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int)dX, itemView.getBottom());
                }
                bg.draw(c);

            }

        }).attachToRecyclerView(recyclerView);

        //Fab opens dialog to add a note manually
        fab.setOnClickListener(v -> showAddNoteDialog());

    }

    private void showAddNoteDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_note, null);

        EditText etBookTitle = dialogView.findViewById(R.id.etBookTitle);
        EditText etNote = dialogView.findViewById(R.id.etNote);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add a Note")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etBookTitle.getText().toString().trim();
                    String note = etNote.getText().toString().trim();
                    if(title.isEmpty() || note.isEmpty()){
                        Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    BookNote newNote = new BookNote("manual",title, note);
                    Executors.newSingleThreadExecutor().execute(()->
                            AppDatabase.getInstance(requireContext())
                                    .bookNoteDao()
                                    .insert(newNote));
                    Toast.makeText(getContext(), "Note added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();

    }
}
