package com.whospablo.flickster;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.whospablo.flickster.adapters.MovieArrayAdapter;
import com.whospablo.flickster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.utils.URIBuilder;

public class MovieStreamActivity extends AppCompatActivity {

    private ArrayList<Movie> mMovies;
    private MovieArrayAdapter mMovieArrayAdapter;
    private ListView mMoviesListView;
    private SwipeRefreshLayout swipeContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_stream);

        initializeGlobalVariables();
        fetchMoviesAsync();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMoviesAsync();
            }
        });

        swipeContainer.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
                );


    }

    private void initializeGlobalVariables() {
        mMoviesListView = (ListView) findViewById(R.id.movie_list_view);
        mMovies = new ArrayList<>();
        mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
        mMoviesListView.setAdapter(mMovieArrayAdapter);
    }

    private void fetchMoviesAsync() {
        try {
            URIBuilder urlb = new URIBuilder(getResources().getString(R.string.now_playing_api));
            urlb.addParameter("api_key", getResources().getString(R.string.movie_api_key));

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(urlb.build().toString(), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray movieJSONResults = null;
                    try {
                        movieJSONResults = response.getJSONArray("results");

                        mMovies.addAll(Movie.fromJSONArray(movieJSONResults));
                        mMovieArrayAdapter.notifyDataSetChanged();

                        swipeContainer.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
