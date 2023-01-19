package com.example.filmaficionado_eksamen_projekt.model;

import com.example.filmaficionado_eksamen_projekt.dao.MovieDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MovieDaoImpl implements MovieDAO {

    private Connection con;

    public MovieDaoImpl(){
        try{
            // Connection til vores Database
            con = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-BI4ROKK;database=MovieProject;userName=sa;password=12345;encrypt=true;trustServerCertificate=true");
        }
        catch (SQLException e) {System.err.println("can not create connection" + e.getMessage());}
    }


    // Funktion der tilføjer en film
    @Override
    public void addMovie(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7)
    {
        try{
            PreparedStatement ps = con.prepareStatement("INSERT INTO Movie VALUES (?,?,?,?,?,?,?,?);"); // Tilføjer en film til vores database med de values der bliver indsat
            ps.setString(1, s0);
            ps.setString(2, s1);
            ps.setString(3, s2);
            ps.setFloat(4, Float.parseFloat(s3));
            ps.setFloat(5, Float.parseFloat(s4));
            ps.setString(6, s5);
            ps.setString(7, s6);
            ps.setInt(8,Integer.parseInt(s7));
            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kunne ikke tilføje film" + e.getMessage());}
    }

    // Funktion der gør at man kan redigere en film
    @Override
    public void editMovie(String s0, String s1, String s2, String s3, String s4, String s5, String s6, String s7, int id) {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE Movie SET title = ?, director = ?, movieDescription = ?, IMDBrating = ?, ownRating = ?, fileLink = ?, lastSeen = ?, favorite = ?  WHERE movieID = ?;"); // Opdaterer de værdier der bliver indsat HVOR movieID = ? (den valgte film)
            ps.setString(1, s0);
            ps.setString(2, s1);
            ps.setString(3, s2);
            ps.setFloat(4, Float.parseFloat(s3));
            ps.setFloat(5, Float.parseFloat(s4));
            ps.setString(6, s5);
            ps.setString(7, s6);
            ps.setInt(8, Integer.parseInt(s7));
            ps.setInt(9, id);
            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kunne ikke redgiere film" + e.getMessage());}
    }

    // Funktion der fjerner en film
    @Override
    public void removeMovie(int id) {
        try
        {
            PreparedStatement ps = con.prepareStatement("delete from CategoryMovie where movieID = ?"); // Fjerner en film fra CategoryMovie (hvor Movie og Category er koblet sammen)
            PreparedStatement ps1 = con.prepareStatement("delete from Movie where movieID = ?"); // Fjerner en film fra Movie

            ps.setInt(1, id);
            ps.executeUpdate();
            ps1.setInt(1,id);
            ps1.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kunne ikke slette film" + e.getMessage());}
    }

    // Funktion der opdaterer vores lastSeen
    @Override
    public void updateLastSeen(String s0, int id) {
        try
        {
            PreparedStatement ps = con.prepareStatement("UPDATE Movie SET lastSeen = ?  WHERE movieID = ?;"); // Opdaterer vores lastSeen i den film vi lige har afspillet
            ps.setString(1, s0);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
        catch (SQLException e){System.err.println("ss" + e.getMessage());}
    }

    // Funktion der får alle film der er lavt rated eller ikke er blevet set i lang tid
    @Override
    public List<Movie> getLowRatedOrOldMovies() {
        List<Movie> showOldAndLowRatedMovies = new ArrayList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie WHERE ownRating BETWEEN 0 AND 4 OR lastSeen < '2021-01-13';"); // Selecter alt fra Movie hvor vores personlige film rating er lav ELLER ikke er blevet set i mere end 2 år
            ResultSet rs = ps.executeQuery();

            Movie mov;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String director = rs.getString(3);
                String movieDescription = rs.getString(4);
                float IMDBrating = rs.getFloat(5);
                float ownRating = rs.getFloat(6);
                String fileLink = rs.getString(7);
                Date lastSeen = rs.getDate(8);
                int favorite = rs.getInt(9);

                mov = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                showOldAndLowRatedMovies.add(mov); // Tilføjer filmen til listen
            }
        }
        catch (SQLException e) {System.err.println("can not access records" + e.getMessage());}

        return showOldAndLowRatedMovies;
    }

    // Funktion der filtrerer på titel
    @Override
    public List<Movie> filterByTitle(String query) {
        List<Movie> filterByTitle = new LinkedList<>();

        try {
            // Selecter alt fra Movie hvor titlen = ? (det brugeren søger på), og så bliver den filtreret fra Ascending til Descending på titlen så det vil sige det kommer i alfabetisk rækkefølge, A-Z.
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie WHERE title LIKE ? ORDER BY title ASC");
            ps.setString(1, query + "%"); // Ved at gøre brug af "%" i vores setString, kan man søge på en del af en titel. F.eks. kan man søge på "t", og så ville alle film der starter med T blive vist
            ResultSet rs1 = ps.executeQuery();

            // Selecter titel og categoryName fra Movie,Category og CategoryMovie, hvor Movie.movieID og Category.categoryID stemmer overens med CategoryMove.movieID og CategoryMove.categoryID. Her bliver der filtreret efter filmens titel, i alfabetisk rækkefølge
            PreparedStatement ps3 = con.prepareStatement("SELECT title, categoryName FROM Movie,Category,CategoryMovie WHERE Movie.movieID = CategoryMovie.movieID AND Category.categoryID = CategoryMovie.categoryID AND Category.categoryName = ? ORDER BY Movie.title ASC;");
            ps3.setString(1, query);
            ResultSet rs3 = ps3.executeQuery();

            Movie m;
            while (rs1.next()) {
                int id = rs1.getInt(1);
                String title = rs1.getString(2);
                String director = rs1.getString(3);
                String movieDescription = rs1.getString(4);
                float IMDBrating = rs1.getFloat(5);
                float ownRating = rs1.getFloat(6);
                String fileLink = rs1.getString(7);
                Date lastSeen = rs1.getDate(8);
                int favorite = rs1.getInt(9);

                m = new Movie(id, title, director, movieDescription, IMDBrating, ownRating, fileLink, lastSeen, favorite);
                filterByTitle.add(m); // Indsætter film der mather det man har søgt på
            }

            Movie mov;
            while (rs3.next()) {
                String title = rs3.getString(1);

                mov = new Movie(0, title, null, null, 0, 0, null, null, 0);
                filterByTitle.add(mov);// Indsætter film der mather det man har søgt på
            }
        } catch (SQLException e) {throw new RuntimeException(e);}

        return filterByTitle;
    }


    // Funktion der filtrerer efter IMDB rating
    @Override
    public List<Movie> filterByIMDB(String query) {

        List<Movie> filterByIMDB = new LinkedList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie WHERE title LIKE ? ORDER BY IMDBrating DESC"); // Selecter alt fra Movie hvor det man har søgt på bliver filtreret efter IMDB rating
            ps.setString(1, query + "%");
            ResultSet rs1 = ps.executeQuery();

            // Selecter alt fra Movie, Category og CategoryMovie hvor det man har søgt på bliver filtreret efter IMDB rating. Det gør at vi sådan set kan søge på en kategori, og så vil alle film tilknyttet den også blive filtreret efter IMDB rating
            PreparedStatement ps3 = con.prepareStatement("SELECT * FROM Movie,Category,CategoryMovie WHERE Movie.movieID = CategoryMovie.movieID AND Category.categoryID = CategoryMovie.categoryID AND Category.categoryName = ? ORDER BY Movie.IMDBrating DESC;");
            ps3.setString(1,query);
            ResultSet rs3 = ps3.executeQuery();

            Movie m;
            while(rs1.next())
            {
                int id = rs1.getInt(1);
                String title = rs1.getString(2);
                String director = rs1.getString(3);
                String movieDescription = rs1.getString(4);
                float IMDBrating = rs1.getFloat(5);
                float ownRating = rs1.getFloat(6);
                String fileLink = rs1.getString(7);
                Date lastSeen = rs1.getDate(8);
                int favorite = rs1.getInt(9);

                m = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                filterByIMDB.add(m); // Indsætter film der mather det man har søgt på
            }

            Movie mov;
            while(rs3.next())
            {

                int id = rs3.getInt(1);
                String title = rs3.getString(2);
                String director = rs3.getString(3);
                String movieDescription = rs3.getString(4);
                float IMDBrating = rs3.getFloat(5);
                float ownRating = rs3.getFloat(6);
                String fileLink = rs3.getString(7);
                Date lastSeen = rs3.getDate(8);
                int favorite = rs3.getInt(9);

                mov = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                filterByIMDB.add(mov); // Indsætter film der mather det man har søgt på
            }
        } catch (SQLException e) {throw new RuntimeException(e);}

        return filterByIMDB;
    }

    // Funktion der filtrerer efter kategori
    @Override
    public List<Movie> filterByCategory(String query) {
        List<Movie> filterByCategory = new LinkedList<>();

        try
        {
            // Selecter alt fra Movie,Category og CategoryMovie. Man søger på en titel og så bliver der alfabetisk filtreret efter kategoriens navn, A-Z.
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie,Category, CategoryMovie WHERE Movie.movieID = CategoryMovie.movieID AND Category.categoryID = CategoryMovie.categoryID AND Movie.title LIKE ? ORDER BY Category.categoryName ASC");
            ps.setString(1, query + "%");
            ResultSet rs1 = ps.executeQuery();
            Movie m;
            while(rs1.next())
            {
                int id = rs1.getInt(1);
                String title = rs1.getString(2);
                String director = rs1.getString(11); // I vores director string indsætter vi det der står i columnindex 11, det vil sige det der står iCategory.categoryName.
                String movieDescription = rs1.getString(4);
                float IMDBrating = rs1.getFloat(5);
                float ownRating = rs1.getFloat(6);
                String fileLink = rs1.getString(7);
                Date lastSeen = rs1.getDate(8);
                int favorite = rs1.getInt(9);

                m = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                filterByCategory.add(m); // Indsætter film der mather det man har søgt på
            }
        } catch (SQLException e) {throw new RuntimeException(e);}

        return filterByCategory;
    }


    // Funktion hvor man kan søge på forskellige ting. Her bliver der ikke filtreret på ascending eller descending
    @Override
    public List<Movie> searchMovie(String query) {

        List<Movie> searchedMovies = new LinkedList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie WHERE title LIKE ?"); // Selecter alt fra Movie hvor title = ? (det man har søgt på)
            PreparedStatement ps2 = con.prepareStatement("SELECT * FROM Movie WHERE IMDBrating LIKE ?"); // Selecter alt fra Movie hvor IMDBrating = ? (det tal man har søgt på)
            // Selecter alt fra Movie,Category og CategoryMovie hvor Category.categoryName = ? (Kategori navnet der er søgt på).
            PreparedStatement ps3 = con.prepareStatement("SELECT * FROM Movie,Category,CategoryMovie WHERE Movie.movieID = CategoryMovie.movieID AND Category.categoryID = CategoryMovie.categoryID AND Category.categoryName = ?;");

            ps.setString(1, query + "%");
            ps2.setString(1, query);
            ps3.setString(1, query);

            ResultSet rs1 = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();
            ResultSet rs3 = ps3.executeQuery();

            Movie m;
            while(rs1.next())
            {
                int id = rs1.getInt(1);
                String title = rs1.getString(2);
                String director = rs1.getString(3);
                String movieDescription = rs1.getString(4);
                float IMDBrating = rs1.getFloat(5);
                float ownRating = rs1.getFloat(6);
                String fileLink = rs1.getString(7);
                Date lastSeen = rs1.getDate(8);
                int favorite = rs1.getInt(9);

                m = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                searchedMovies.add(m); // Indsætter film der mather det man har søgt på
            }

            Movie mo;
            while(rs2.next())
            {
                int id = rs2.getInt(1);
                String title = rs2.getString(2);
                String director = rs2.getString(3);
                String movieDescription = rs2.getString(4);
                float IMDBrating = rs2.getFloat(5);
                float ownRating = rs2.getFloat(6);
                String fileLink = rs2.getString(7);
                Date lastSeen = rs2.getDate(8);
                int favorite = rs2.getInt(9);

                mo = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                searchedMovies.add(mo); // Indsætter film der mather det man har søgt på
            }

            Movie mov;
            while(rs3.next())
            {
                int id = rs3.getInt(1);
                String title = rs3.getString(2);
                String director = rs3.getString(11);
                String movieDescription = rs3.getString(4);
                float IMDBrating = rs3.getFloat(5);
                float ownRating = rs3.getFloat(6);
                String fileLink = rs3.getString(7);
                Date lastSeen = rs3.getDate(8);
                int favorite = rs3.getInt(9);

                mov = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                searchedMovies.add(mov); // Indsætter film der mather det man har søgt på
            }
        }
        catch (SQLException e) {System.err.println("Kunne ikke finde sangen, " + e.getMessage());}

        return searchedMovies;
    }


    // Funktion der får alle film fra vores Movie database
    @Override
    public List<Movie> getAllMovies() {
        List<Movie> showAllMovies = new ArrayList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Movie"); // Selecter alt fra Movie
            ResultSet rs = ps.executeQuery();

            Movie mov;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String director = rs.getString(3);
                String movieDescription = rs.getString(4);
                float IMDBrating = rs.getFloat(5);
                float ownRating = rs.getFloat(6);
                String fileLink = rs.getString(7);
                Date lastSeen = rs.getDate(8);
                int favorite = rs.getInt(9);

                mov = new Movie(id,title,director,movieDescription,IMDBrating,ownRating,fileLink,lastSeen,favorite);
                showAllMovies.add(mov); // Indsætter ALLE film i en liste
            }
        }
        catch (SQLException e) {System.err.println("can not access records" + e.getMessage());}

        return showAllMovies;
    }
}
