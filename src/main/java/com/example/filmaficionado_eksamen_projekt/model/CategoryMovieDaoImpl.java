package com.example.filmaficionado_eksamen_projekt.model;

import com.example.filmaficionado_eksamen_projekt.dao.CategoryMovieDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryMovieDaoImpl implements CategoryMovieDAO {

    private Connection con;

    public CategoryMovieDaoImpl() {
        try {
            // Connection til vores Database
            con = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-BI4ROKK;database=MovieProject;userName=sa;password=12345;encrypt=true;trustServerCertificate=true");
        }
        catch (SQLException e) {System.err.println("can not create connection" + e.getMessage());}
    }


    // Funktion der finder alle film der er relateret til en bestemt kategori
    @Override
    public List<Movie> getMoviesInCategory(Category catList) {

        List<Movie> moviesInCategory = new ArrayList<>();

        try {
            // Selecter alt fra Movie, Category, CategoryMovie hvor Category.categoryID = CategoryMovie.categoryID og CategoryMovie.movieID = Movie.movieID. Sørger for at vi finder den helt rigtige film der er associeret med en kategori. Category.categoryID = ? (den valgte kategori)
            PreparedStatement ps = con.prepareStatement("select * from Movie, Category, CategoryMovie where Category.categoryID = CategoryMovie.categoryID AND CategoryMovie.movieID = Movie.movieID AND Category.categoryID = ?");
            ps.setInt(1, catList.getId()); // catList = den kategori der er valgt
            ResultSet rs = ps.executeQuery();
            Movie mov;
            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String director = rs.getString(3);
                String movieDescription = rs.getString(4);
                float IMDBrating = rs.getFloat(5);
                float ownRating = rs.getFloat(6);
                String fileLink = rs.getString(7);
                Date lastSeen = rs.getDate(8);
                int favorite = rs.getInt(9);

                mov = new Movie(id, title, director, movieDescription, IMDBrating, ownRating, fileLink, lastSeen, favorite);
                moviesInCategory.add(mov); // Tilføjer de film der matcher det vi leder efter
            }
        }
        catch (SQLException t) {System.err.println("Kunne ikke få alle sange" + t.getMessage());}

        return moviesInCategory;
    }


    // Funktion der fjerner en film fra en bestemt kategori
    @Override
    public void removeMovieFromCategory(int movieIDFromCategory, int categoryID) {

        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM CategoryMovie WHERE movieID = ? AND categoryID = ?;"); // Sletter fra CategoryMovie hvor den valgte movieID og categoryID er. (Altså i den samme row)
            ps.setInt(1, movieIDFromCategory);
            ps.setInt(2, categoryID);
            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kunne ikke slette film fra category" + e.getMessage());}
    }


    // Funktion der tilknytter en film til en kategori
    @Override
    public void addMovieToCategory(int id1, int id2, int fav) {

        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO CategoryMovie VALUES (?,?,?);"); // Indsætter en ny CategoryMovie med movieID (valgte films ID) og categoryID (valgte kategori's ID) og fav sættes til 0 hver gang.
            ps.setInt(1, id1);
            ps.setInt(2, id2);
            ps.setInt(3, fav);
            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kunne ikke tilføje film til category" + e.getMessage());}
    }


    // Funktion der tilføjer en film til at være favorit / den bedste i den kategori.
    @Override
    public void addToFavorite(int id1, int id2) {

        try {
            PreparedStatement ps = con.prepareStatement("UPDATE CategoryMovie SET favorite = 1 WHERE movieID = ? AND categoryID = ? ;"); // Opdaterer favorite i den row hvor movieID og categoryID findes.
            ps.setInt(1, id1);
            ps.setInt(2, id2);
            ps.executeUpdate();

        }
        catch (SQLException e) {System.err.println("Kunne ikke tilføje film til category" + e.getMessage());}
    }


    // Funktion der finder alle film der er favoritter i den valgte kategori
    @Override
    public List<Movie> getMovieFavorites(Category cateList) {

        List<Movie> moviesFavoritesInCategory = new ArrayList<>();

        try
        {
            // Selecter alt fra Movie, Category og CategoryMovie, categoryID stemmer overens med den valgte kategori OG hvor favorite = 1
            PreparedStatement ps = con.prepareStatement("select * from Movie, Category, CategoryMovie where Category.categoryID = CategoryMovie.categoryID AND CategoryMovie.movieID = Movie.movieID AND Category.categoryID = ? AND CategoryMovie.favorite = 1;");
            ps.setInt(1, cateList.getId());

            ResultSet rs = ps.executeQuery();
            Movie mov;
            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String director = rs.getString(3);
                String movieDescription = rs.getString(4);
                float IMDBrating = rs.getFloat(5);
                float ownRating = rs.getFloat(6);
                String fileLink = rs.getString(7);
                Date lastSeen = rs.getDate(8);
                int favorite = rs.getInt(9);

                mov = new Movie(id, title, director, movieDescription, IMDBrating, ownRating, fileLink, lastSeen, favorite);
                moviesFavoritesInCategory.add(mov); // Tilføjer de film der matcher det vi leder efter
            }
        }
        catch (SQLException e) {}

        return moviesFavoritesInCategory;
    }
}
