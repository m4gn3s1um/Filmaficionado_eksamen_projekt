package com.example.filmaficionado_eksamen_projekt.model;

import java.util.Date;

// Vores Movie class

public class Movie {

    private int id;

    private String title;

    private String director;

    private String movieDescription;

    private float imdbRating;

    private float myRating;

    private String fileName;

    private Date lastSeen;

    private int favorite;

    public Movie(int id, String title, String director, String movieDescription,
                 float imdbRating, float myRating, String fileName, Date lastSeen, int favorite)
    {
        this.id = id;
        this.title = title;
        this.director = director;
        this.movieDescription = movieDescription;
        this.imdbRating = imdbRating;
        this.myRating = myRating;
        this.fileName = fileName;
        this.lastSeen = lastSeen;
        this.favorite = favorite;
    } // Constructor

    public String toString(){return title + " // " + "IMDB rating: " + imdbRating + " // " + " " + director;} // Det der skal vises i listViews
    public int getId() {return id;} // Får ID

    public String getTitle() {return title;} // Får titlen

    public String getDirector() {return director;} // Får instruktøren

    public String getMovieDescription() {return movieDescription;} // Får filmbeskrivelsen

    public float getImdbRating() {return imdbRating;} // Får IMDB rating

    public float getMyRating() {return myRating;} // Får den personlige rating vi gav en film

    public String getFileName() {return fileName;} // Får filNavnet

    public Date getLastSeen() {return lastSeen;} // Får lastSeen (hvornår vi sidst så den)

    public int getFavorite() {return favorite;} // Får favorite

}
