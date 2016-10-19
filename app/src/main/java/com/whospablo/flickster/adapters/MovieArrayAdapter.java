package com.whospablo.flickster.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whospablo.flickster.R;
import com.whospablo.flickster.models.Movie;

import java.util.List;

/**
 * Created by pablo_arango on 10/18/16.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private static class ViewHolder {
        TextView movieTitle;
        TextView movieOverview;
        ImageView moviePoster;
    }

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.movie_row, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if( convertView == null ){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_row, parent,  false);
            viewHolder.movieTitle = (TextView) convertView.findViewById(R.id.movie_title_tv);
            viewHolder.movieOverview = (TextView) convertView.findViewById(R.id.movie_overview_tv);
            viewHolder.moviePoster = (ImageView) convertView.findViewById(R.id.movie_poster_iv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Movie movie = getItem(position);

        viewHolder.movieTitle.setText(movie.getTitle());
        viewHolder.movieOverview.setText(movie.getOverview());

        Picasso.with(getContext())
                .load(movie.getPosterPath())
                .error(R.drawable.ic_error)
                .into(viewHolder.moviePoster);

        return convertView;
    }
}
