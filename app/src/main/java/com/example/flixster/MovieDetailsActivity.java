package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    public static String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=aa1d75202114e558be15552fcdc90775&language=en-US";

    // The movie to display
    Movie movie;

    // The view objects
    TextView tvTitle, tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster, ivPlay;
    String fullUrl, youTubeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // Resolve the view objects
        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;
        rbVoteAverage = binding.rbVoteAverage;
        ivPoster = binding.ivPoster;
        ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTrailer();
            }
        });
        ivPlay = binding.ivPlay;
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { goToTrailer(); }
        });

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // Setter movie parameters
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);

        fullUrl = String.format(VIDEO_URL, movie.getIdMovie());
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(fullUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject object = json.jsonObject;
                try {
                    JSONArray results = object.getJSONArray("results");
                    JSONObject movie = results.getJSONObject(0);
                    youTubeId = movie.getString("key");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });

        String imageURL;
        int radius = 0, margin = 0;

        // if the phone is in landscape
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // then imageURL = back drop image
            imageURL = movie.getBackdropPath();
            radius = 40;
            margin = 10;
        } else {
            // else imageURL = poster image
            imageURL = movie.getPosterPath();
            radius = 50;
            margin = 5;
        }

        Glide.with(this)
                .load(imageURL)
                .centerCrop()
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivPoster);
    }

    // When the user clicks on a row, show MovieDetailsActivity for the selected movie
    public void goToTrailer() {
        // create intent for the new activity
        Intent intent = new Intent(this, MovieTrailerActivity.class);
        // serialize the movie using parceler, use its short name as a key
        intent.putExtra(MovieTrailerActivity.class.getSimpleName(), youTubeId);
        // show the activity
        startActivity(intent);
    }
}