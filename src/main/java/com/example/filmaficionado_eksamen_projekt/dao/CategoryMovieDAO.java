package com.example.filmaficionado_eksamen_projekt.dao;

import com.example.filmaficionado_eksamen_projekt.model.Category;
import com.example.filmaficionado_eksamen_projekt.model.Movie;
import java.util.List;

// Her laver vi vores DAO til CategoryMovie
public interface CategoryMovieDAO {

   public List<Movie> getMoviesInCategory(Category catList);

   public void removeMovieFromCategory(int movieIDFromCategory, int categoryID);

   public void addMovieToCategory(int id1, int id2, int id3);

   public void addToFavorite(int id1, int id2);

   public List<Movie> getMovieFavorites(Category cateList);




}
