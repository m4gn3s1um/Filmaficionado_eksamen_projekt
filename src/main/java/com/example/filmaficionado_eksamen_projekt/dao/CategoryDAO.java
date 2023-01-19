package com.example.filmaficionado_eksamen_projekt.dao;

import com.example.filmaficionado_eksamen_projekt.model.Category;
import java.util.List;

// Her laver vi vores DAO til Category
public interface CategoryDAO {

    public void addCategory(String catego);
    public List<Category> getAllCategories();

    public void removeCategory(int categoryID);

    public void updateCategory(String category, int categoryID);

}
