package com.example.popularmoviesstageone;

import com.example.popularmoviesstageone.responses.MovieResponse;
import com.example.popularmoviesstageone.responses.ReviewResponse;
import com.example.popularmoviesstageone.responses.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface retrofitInterface {
    @GET("{category}")
    Call<MovieResponse> getMovies(
            @Path("category") String category,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") String page
    );

    @GET("{videos}")
    Call<VideoResponse> getVideos(
            @Path("videos") String videos,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("{reviews}")
    Call<ReviewResponse> getReviews(
            @Path("reviews") String reviews,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
}
