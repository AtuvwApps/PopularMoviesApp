package com.example.popularmoviesstageone.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.example.popularmoviesstageone.model.Movie;
import com.example.popularmoviesstageone.adapters.MovieAdapter;
import com.example.popularmoviesstageone.responses.MovieResponse;
import com.example.popularmoviesstageone.R;
import com.example.popularmoviesstageone.database.AppDatabase;
import com.example.popularmoviesstageone.databinding.ActivityMainBinding;
import com.example.popularmoviesstageone.retrofitInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {

    public static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static String PAGE = "1";
    public static String LANGUAGE = "en-US";
    public static String CATEGORY = "popular";
    public static String API_KEY = "[INSERT YOUR API KEY]";
    private ActivityMainBinding binding;
    private List<Movie> movies;
    private LiveData<List<Movie>> favouriteMovies;
    private AppDatabase mDb;
    retrofitInterface myInterface;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myInterface = retrofit.create(retrofitInterface.class);
        movies = new ArrayList<Movie>();
        mDb = AppDatabase.getInstance(getApplicationContext());
        if(CATEGORY == "favourites"){
            setTitle(R.string.favourites);
            getFavouriteMovies();
        }else if(CATEGORY == "top_rated"){
            setTitle(R.string.top_rated_movies);
            getMovies(CATEGORY);
        }else {
            setTitle(R.string.popular_movies);
            getMovies(CATEGORY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.popular:
                CATEGORY = "popular";
                setTitle(R.string.popular_movies);
                getMovies(CATEGORY);
                return true;
            case R.id.top_rated:
                CATEGORY = "top_rated";
                setTitle(R.string.top_rated_movies);
                getMovies(CATEGORY);
                return true;
            case R.id.favourites:
                CATEGORY = "favourites";
                setTitle(R.string.favourites);
                getFavouriteMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getFavouriteMovies(){
        binding.error.setVisibility(View.INVISIBLE);
        binding.moviesRecyclerView.setVisibility(View.VISIBLE);
        favouriteMovies = mDb.movieDao().loadAllMovies();
        favouriteMovies.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                startRecycler(movies);
                if(movies.size() == 0){
                    noFavourites();
                }
            }
        });
    }

    public void noFavourites(){
        binding.moviesRecyclerView.setVisibility(View.INVISIBLE);
        binding.error.setVisibility(View.VISIBLE);
        binding.error.setText(R.string.no_favourites);
    }

    public void getMovies(String category){
        if(isOnline()) {
            binding.error.setVisibility(View.INVISIBLE);
            binding.moviesRecyclerView.setVisibility(View.VISIBLE);
            Call<MovieResponse> call = myInterface.getMovies(category, API_KEY, LANGUAGE, PAGE);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if(response.isSuccessful()) {
                        List<Movie> results = response.body().getResults();
                        movies = results;
                        startRecycler(movies);
                    }else{
                        Log.e("Movies call not successful", response.toString());
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        else {
            binding.moviesRecyclerView.setVisibility(View.INVISIBLE);
            binding.error.setVisibility(View.VISIBLE);
            binding.error.setText(R.string.error);
        }
    }

    public void startRecycler(List<Movie> m){
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        binding.moviesRecyclerView.setLayoutManager(layoutManager);
        binding.moviesRecyclerView.setAdapter(new MovieAdapter(m, this, this));
    }

    public void startSelectedMovieActivity(Movie m){
        Intent intent = new Intent(this, MovieActivity.class);
        intent.putExtra("Movie", m);
        startActivity(intent);
    }

    @Override
    public void onClick(int position) {
        if(CATEGORY == "favourites"){
            favouriteMovies.observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(List<Movie> movies) {
                    startSelectedMovieActivity(movies.get(position));
                }
            });
        }else {
            startSelectedMovieActivity(movies.get(position));
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    /**
     * Better not to set a fixed number of columns for the gridview
     * instead use a method like this one to calculate it for you, using the size of the devices screen
     * @param context
     * @return
     */
    public static int calculateNoOfColumns(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }
}
