package com.example.filmaficionado_eksamen_projekt.model;

import java.util.ArrayList;
import java.util.List;

// Vores Category class
public class Category {

    private int id;

    private String categoryName;

    private List<Movie> movies = new ArrayList<>();

    public Category(int id, String categoryName)
    {
        this.id = id;
        this.categoryName = categoryName;
    } // Constructoren


    public String toString(){return categoryName;} // Det der blandt andet vises i listViewet

    public int getId(){return id;} // For kategoriens id

    public String getCategoryName(){return categoryName;} // Får kategoriens navn
}
