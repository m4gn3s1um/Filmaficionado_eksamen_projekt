package com.example.filmaficionado_eksamen_projekt.dao;

import com.example.filmaficionado_eksamen_projekt.model.Movie;
import java.util.List;

// Her laver vi vores DAO til Movie
public interface MovieDAO {

    public void addMovie(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7);

    public void editMovie(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7, int id);

    public List<Movie> searchMovie(String query);

    public List<Movie> getAllMovies();

    public void removeMovie(int id);

    public void updateLastSeen(String s0, int id);

    public List<Movie> getLowRatedOrOldMovies();

    public List<Movie> filterByTitle(String query);

    public List<Movie> filterByIMDB(String query);

    public List<Movie> filterByCategory(String query);

}
