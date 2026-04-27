package com.andromeda.booknest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {
    @SerializedName("key")
    private String key;
    @SerializedName("title")
    private String title;
    @SerializedName("author_name")
    private List<String> authorNames;
    @SerializedName("cover_i")
    private Long coverId;
    @SerializedName("first_publish_year")
    private Integer firstPublishYear;
    @SerializedName("subject")
    private List<String> subjects;

    // helper methods

    public String getBookId(){
        if (key == null) return "";
        return key.replace("/works/", "");
    }

    public String getTitle() {
        return title!=null ? title : "Unknown Title";
    }

    public String getFirstAuthor() {
        if (authorNames == null || authorNames.isEmpty()) return "Unknown Author";
        return authorNames.get(0);
    }

    public String getCoverUrl() {
        if (coverId == null) return null;
        return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
    }

    public String getPublishYear() {
        return firstPublishYear !=null ? String.valueOf(firstPublishYear) : "N/A";
    }

    public String getSubject() {
        if (subjects == null || subjects.isEmpty()) return "Unknown Subject";
        return subjects.get(0);
    }

    public String getKey() {
        return key;
    }

    public Long getCoverId() {
        return coverId;
    }
}
