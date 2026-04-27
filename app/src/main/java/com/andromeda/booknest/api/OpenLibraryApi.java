package com.andromeda.booknest.api;

import retrofit2.http.Query;
import com.andromeda.booknest.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface OpenLibraryApi {
    // Search books by keyword: https://openlibrary.org/search.json?q=harry+potter&limit=20

    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit
    );
}
