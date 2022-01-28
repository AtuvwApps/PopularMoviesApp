package com.example.popularmoviesstageone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.popularmoviesstageone.model.Movie;
import com.example.popularmoviesstageone.databinding.ListItemBinding;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Context context;
    private ItemClickListener itemClickListener;
    private String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";

    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ListItemBinding binding;
        ItemClickListener itemClickListener;

        public MovieViewHolder(ListItemBinding b, ItemClickListener itemClickListener) {
            super(b.getRoot());
            binding = b;
            this.itemClickListener = itemClickListener;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
           itemClickListener.onClick(getBindingAdapterPosition());
        }
    }

    public MovieAdapter(List<Movie> movies, Context context, ItemClickListener itemClickListener){
        this.movies = movies;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.getContext())), itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Picasso.get().load(imageBaseUrl + movies.get(position).getPosterPath()).into(holder.binding.posterImage);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        } else {
            return 0;
        }
    }

    public interface ItemClickListener {
        void onClick(int position);
    }
}
