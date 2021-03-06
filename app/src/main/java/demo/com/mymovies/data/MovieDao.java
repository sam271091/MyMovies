package demo.com.mymovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    @Query("SELECT * FROM movies WHERE id == :movieid")
    Movie getMovieById(int movieid);

    @Query("SELECT * FROM favourite_movies WHERE id == :movieid")
    FavouriteMovie getFavouriteMovieById(int movieid);

    @Query("DELETE  FROM movies")
    void deleteAllMovies();

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Insert
    void insertFavouriteMovie(FavouriteMovie movie);

    @Delete
    void deleteFavouriteMovie(FavouriteMovie movie);

}
