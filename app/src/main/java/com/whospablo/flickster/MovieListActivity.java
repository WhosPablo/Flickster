package com.whospablo.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

public class MovieListActivity extends AppCompatActivity {

    public static final String LIST_STATE = "MOVIE_LIST_STATE";
    public static final String LIST = "MOVIE_LIST";
    public static final String ITEM_CLICKED = "MOVIE_SELECTED";
    private ArrayList<Movie> mMovies;
    private MovieArrayAdapter mMovieArrayAdapter;
    private ListView mMoviesListView;
    private SwipeRefreshLayout swipeContainer;
    private Parcelable mScrollState;


    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_movie_list);

        setUpMoviesListView(state);
        setupSwipeContainer();
    }

    private void setUpMoviesListView(Bundle state) {
        mMoviesListView = (ListView) findViewById(R.id.movie_list_view);
        View view = getLayoutInflater().inflate(R.layout.header_movie_list, null);
        mMoviesListView.addHeaderView(view, null, false);

        if(state==null || !state.containsKey(LIST_STATE) || !state.containsKey(LIST)){
            mMovies = new ArrayList<>();
            mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
            mMoviesListView.setAdapter(mMovieArrayAdapter);
            fetchMoviesAsync();
        } else {
            mMovies = state.getParcelableArrayList(LIST);
            mScrollState = state.getParcelable(LIST_STATE);

            mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
            mMoviesListView.setAdapter(mMovieArrayAdapter);
            mMoviesListView.onRestoreInstanceState(mScrollState);
        }

        mMoviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = (Movie) parent.getItemAtPosition(position);

                Intent i = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                i.putExtra(ITEM_CLICKED, m);
                startActivity(i);
            }
        });
    }

    private void setupSwipeContainer() {
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

    @Override
    protected void onSaveInstanceState(Bundle state) {
        mScrollState = mMoviesListView.onSaveInstanceState();
        state.putParcelable(LIST_STATE, mScrollState);
        state.putParcelableArrayList(LIST, mMovies);
        super.onSaveInstanceState(state);
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
                        mMovies.clear();
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
