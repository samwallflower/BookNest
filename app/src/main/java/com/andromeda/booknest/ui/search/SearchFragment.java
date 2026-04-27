package com.andromeda.booknest.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andromeda.booknest.BookDetailActivity;
import com.andromeda.booknest.adapter.SearchAdapter;
import com.andromeda.booknest.api.RetrofitClient;
import com.andromeda.booknest.database.AppDatabase;
import com.andromeda.booknest.model.FavoriteBook;
import com.andromeda.booknest.model.SearchResponse;
import com.andromeda.booknest.model.SearchResult;
import com.example.booknest.R;

import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private EditText etSearch;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        adapter = new SearchAdapter(new SearchAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(SearchResult book) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra("bookId", book.getBookId());
                intent.putExtra("title", book.getTitle());
                intent.putExtra("author", book.getFirstAuthor());
                intent.putExtra("publishYear", book.getPublishYear());
                intent.putExtra("coverUrl", book.getCoverUrl());
                startActivity(intent);
            }


            @Override
            public void onFavoriteClick(SearchResult book) {
                saveFavorite(book);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Search when user presses the search key on Adapter
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                String query = etSearch.getText().toString().trim();
                if(!query.isEmpty()){
                    searchBooks(query);
                }
                return true;
            }
            return false;
        });

        //Search button

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if(!query.isEmpty()){
                searchBooks(query);
            } else {
                Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void searchBooks(String query) {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        RetrofitClient.getInstance().getApi()
                .searchBooks(query, 20)
                .enqueue(new Callback<SearchResponse>(){
                    @Override
                    public void onResponse(@NonNull Call<SearchResponse> call,
                                           @NonNull Response<SearchResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if(response.isSuccessful() && response.body() != null){
                            List<SearchResult> books = response.body().getDocs();
                            if(books!=null && !books.isEmpty()){
                                adapter.setBooks(books);
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                tvEmpty.setText("No books found for \"" + query + "\"");
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                        }else{
                            tvEmpty.setText("Error fetching results. Try again");
                            tvEmpty.setVisibility(View.VISIBLE);

                        }
                    }
                    
                    @Override
                    public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setText("Network error: " + t.getMessage());
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }
    
    private void saveFavorite(SearchResult book){
        FavoriteBook fav = new FavoriteBook(
                book.getBookId(),
                book.getTitle(),
                book.getFirstAuthor(),
                book.getCoverUrl(),
                "",
                book.getPublishYear()
        );
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(requireContext()).favoriteBookDao().insert(fav);
            requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), "Book added to favorites", Toast.LENGTH_SHORT).show());
        });

    }

}
