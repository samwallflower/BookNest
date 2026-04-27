package com.andromeda.booknest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("docs")
    private List<SearchResult> docs;
    @SerializedName("numFound")
    private int numFound;

    public List<SearchResult> getDocs() {
        return docs;
    }

    public int getNumFound() {
        return numFound;
    }
}
