package com.example.popularmoviesstageone.responses;

import com.example.popularmoviesstageone.model.Review;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("results")
    @Expose
    private List<Review> results = null;

    public ReviewResponse(){}

    public ReviewResponse(String id, List<Review> results){
        this.id = id;
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
