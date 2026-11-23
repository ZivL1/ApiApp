package com.katza.apiapp;

import com.google.gson.annotations.SerializedName;

public class MovieRatingResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("averageRating")
    private Double averageRating;

    @SerializedName("numVotes")
    private Integer numVotes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }
}
