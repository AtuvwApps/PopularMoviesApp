package com.example.popularmoviesstageone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.example.popularmoviesstageone.model.Movie;
import com.example.popularmoviesstageone.R;
import com.example.popularmoviesstageone.model.Review;
import com.example.popularmoviesstageone.responses.ReviewResponse;
import com.example.popularmoviesstageone.model.Video;
import com.example.popularmoviesstageone.responses.VideoResponse;
import com.example.popularmoviesstageone.database.AppDatabase;
import com.example.popularmoviesstageone.databinding.ActivityMovieBinding;
import com.example.popularmoviesstageone.retrofitInterface;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieActivity extends AppCompatActivity {
    public static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    public static String LANGUAGE = "en-US";
    public static String API_KEY = "[INSERT YOUR API KEY]";
    public static String VIDEOS = "videos";
    public static String REVIEWS = "reviews";
    private List<Video> videos;
    private List<Review> reviews;
    retrofitInterface retrofitVideoInterface;
    retrofitInterface retrofitReviewInterface;
    Retrofit retrofit;
    boolean favourite = false;
    private AppDatabase mDb;
    private Movie selectedMovie;

    private ActivityMovieBinding binding;
    public String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videos = new ArrayList<Video>();
        reviews = new ArrayList<Review>();

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        selectedMovie = intent.getParcelableExtra("Movie");
        setTitle(selectedMovie.getTitle());
        Picasso.get().load(imageBaseUrl + selectedMovie.getPosterPath()).into(binding.poster);
        binding.rating.setText(selectedMovie.getVoteAverage()+"/10");
        binding.releaseDate.setText(selectedMovie.getReleaseDate());
        binding.plot.setText(selectedMovie.getOverview());
        checkIfAlreadyFavourite();
        BASE_URL = BASE_URL + Integer.toString(selectedMovie.getId())+"/";
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitVideoInterface = retrofit.create(retrofitInterface.class);
        retrofitReviewInterface = retrofit.create(retrofitInterface.class);
        getVideos();
        getReviews();

        //Reset the base url so as to not add on multiple movie ID's every time we open this activity
        BASE_URL = "https://api.themoviedb.org/3/movie/";

        binding.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favourite){
                    binding.favouriteBtn.setBackground(getDrawable(R.drawable.star_hollow));
                    favourite = false;
                    Toast.makeText(MovieActivity.this, "Movie removed from favourites", Toast.LENGTH_SHORT).show();

                    mDb.movieDao().deleteMovie(selectedMovie);
                }else{
                    binding.favouriteBtn.setBackground(getDrawable(R.drawable.star_filled));
                    favourite = true;
                    Toast.makeText(MovieActivity.this, "Movie added to favourites", Toast.LENGTH_SHORT).show();

                    mDb.movieDao().insertMovie(selectedMovie);
                }
            }
        });
    }

    public void getVideos(){
        Call<VideoResponse> call = retrofitVideoInterface.getVideos(VIDEOS, API_KEY, LANGUAGE);
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.isSuccessful()) {
                    List<Video> results = response.body().getResults();
                    videos = results;
                    setupVideos();
                }else{
                    Log.e("Videos call not successful", response.toString());
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void setupVideos(){
        for(int i = 0; i < videos.size(); i++){
            final String thisKey = YOUTUBE_BASE_URL + videos.get(i).getKey();
            Button btn = new Button(this);
            btn.setText("Play "+videos.get(i).getType());
            binding.trailersLayout.addView(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openVideo(thisKey);
                }
            });
        }
    }

    public void getReviews(){
        Call<ReviewResponse> call = retrofitReviewInterface.getReviews(REVIEWS, API_KEY, LANGUAGE);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if(response.isSuccessful()) {
                    List<Review> results = response.body().getResults();
                    reviews = results;
                    setupReviews();
                }else{
                    Log.e("Reviews call not successful", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void setupReviews(){
        for(int i = 0; i < reviews.size(); i++){
            binding.reviews.append("Author: "+reviews.get(i).getAuthor()+"\n");
            binding.reviews.append("\""+reviews.get(i).getContent()+"\"\n");
        }
    }

    public void openVideo(String key){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(key));
        startActivity(intent);
    }

    public void checkIfAlreadyFavourite(){
        LiveData<List<Movie>> favourites = mDb.movieDao().loadAllMovies();
        favourites.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                for(int i = 0; i < movies.size(); i++){
                    if(selectedMovie.getId() == movies.get(i).getId()){
                        favourite = true;
                        binding.favouriteBtn.setBackground(getDrawable(R.drawable.star_filled));
                    }
                }
            }
        });

    }
}
