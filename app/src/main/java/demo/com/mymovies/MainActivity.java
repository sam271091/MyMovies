package demo.com.mymovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import demo.com.mymovies.adapters.MovieAdapter;
import demo.com.mymovies.data.MainViewModel;
import demo.com.mymovies.data.Movie;
import demo.com.mymovies.utils.JSONUtils;
import demo.com.mymovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch SwitchSort;
    private TextView textViewTop_Rated;
    private TextView textViewTop_Popularity;
    private MainViewModel viewModel;
    private static final int LOADER_ID = 123;
    private LoaderManager loaderManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itemMain :
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite :
                Intent intentToFavourite = new Intent(this,FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loaderManager = LoaderManager.getInstance(this);

//        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY,5);
//
//
//        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
//        StringBuilder builder = new StringBuilder();
//
//        for (Movie movie:movies){
//            builder.append(movie.getTitle()).append("\n");
//        }
//
//        Log.i("MyResult",builder.toString());

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        SwitchSort = findViewById(R.id.switchSort);

        textViewTop_Rated = findViewById(R.id.textViewTopRated);
        textViewTop_Popularity = findViewById(R.id.textViewPopularity);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this,2));
        movieAdapter = new MovieAdapter();

//        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY,1);
//        final ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
//        movieAdapter.setMovies(movies);
        recyclerViewPosters.setAdapter(movieAdapter);

        SwitchSort.setChecked(true);

        SwitchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });

        SwitchSort.setChecked(false);
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                //Toast.makeText(MainActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("id",movie.getId());
                startActivity(intent);
            }
        });

        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "Конец страницы", Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });

    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        SwitchSort.setChecked(false);
    }

    public void onClickTopRated(View view) {
        setMethodOfSort(true);
        SwitchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated){
        int methodOfSort;

        if (isTopRated){
            textViewTop_Rated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTop_Popularity.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            textViewTop_Popularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTop_Rated.setTextColor(getResources().getColor(R.color.white_color));
            methodOfSort = NetworkUtils.POPULARITY;
        }

        downloadData(methodOfSort,1);
    }


    private void downloadData(int methodOfSort,int page){

        URL url = NetworkUtils.buildURL(methodOfSort,page);
        Bundle bundle = new Bundle();
        bundle.putString("url",url.toString());
        loaderManager.restartLoader(LOADER_ID,bundle,this);

    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int i, @Nullable Bundle bundle) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this,bundle);

        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {

        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        if (movies != null && !movies.isEmpty()){
            viewModel.deleteAllMovies();

            for (Movie movie : movies){
                viewModel.insertMovie(movie);
            }
        }

        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}
