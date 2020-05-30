package demo.com.mymovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import demo.com.mymovies.data.FavouriteMovie;
import demo.com.mymovies.data.MainViewModel;
import demo.com.mymovies.data.Movie;

public class DetailActivity extends AppCompatActivity {

    ImageView imageViewAddToFavourite;
    ImageView imageViewBigPoster;
    TextView textViewTitle;
    TextView textViewOriginalTitle;
    TextView textViewRating;
    TextView textViewReleaseDate;
    TextView textViewOverview;
    int id;
    MainViewModel viewModel;
    Movie movie;
    FavouriteMovie favouriteMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")){

            id = intent.getIntExtra("id",-1);
        } else {
            finish();
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));

        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);

        setFavourite();
    }

    public void onClickChangeFavourite(View view) {



        if (favouriteMovie == null){
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, getString(R.string.add_to_favourite), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, getString(R.string.remove_from_favourite), Toast.LENGTH_SHORT).show();
        }
        setFavourite();

    }

    private void setFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null){
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }
}
