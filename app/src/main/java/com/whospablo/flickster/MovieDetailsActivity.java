package com.whospablo.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whospablo.flickster.models.Movie;

/**
 * Created by pablo_arango on 10/21/16.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE = "movie";
    Movie mMovie;
    TextView mMovieTitle;
    TextView mMovieOverview;
    ImageView mMoviePoster;
    ImageView mMovieBackdrop;


    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_movie_details);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(MovieListActivity.ITEM_CLICKED)){
            mMovie =  intent.getParcelableExtra(MovieListActivity.ITEM_CLICKED);
            initializeAndFIllDetails(mMovie);
        } else if (state != null && state.containsKey(MOVIE)) {
            mMovie = state.getParcelable(MOVIE);
            initializeAndFIllDetails(mMovie);
        } else {
            finish();
        }
    }

    private void initializeAndFIllDetails(Movie movie) {
        mMovieTitle = (TextView) findViewById(R.id.movie_title_tv);
        mMovieOverview = (TextView) findViewById(R.id.movie_overview_tv);
        mMoviePoster = (ImageView) findViewById(R.id.movie_poster_iv);
        mMovieBackdrop = (ImageView) findViewById(R.id.movie_backdrop_iv);

        mMovieTitle.setText(movie.getTitle());
        mMovieOverview.setText(movie.getOverview());

        Picasso.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.ic_movie)
                .error(R.drawable.ic_movie)
                .into(mMoviePoster);

        Picasso.with(this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.ic_movie)
                .error(R.drawable.ic_movie)
                .into(mMovieBackdrop);

    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        state.putParcelable(MOVIE, mMovie);
        super.onSaveInstanceState(state);
    }
}
