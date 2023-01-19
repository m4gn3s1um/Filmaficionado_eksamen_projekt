package com.example.filmaficionado_eksamen_projekt.model;

import com.example.filmaficionado_eksamen_projekt.dao.CategoryDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryDaoImpl implements CategoryDAO {

    private Connection con;

    public CategoryDaoImpl(){
        try{
            // Connection til vores Database
            con = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-BI4ROKK;database=MovieProject;userName=sa;password=12345;encrypt=true;trustServerCertificate=true");
        } catch (SQLException e) {
            System.err.println("can not create connection" + e.getMessage());
        }
    }


    // Funktion der tilføjer en ny kategori
    @Override
    public void addCategory(String catego) {
        try
        {
            PreparedStatement ps = con.prepareStatement("INSERT INTO Category VALUES(?);"); // Tilføjer en ny kategori til vores database
            ps.setString(1, catego);
            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kan ikke oprette category " + e.getMessage());}
    }


    // Funktion der får alle kategorierne der findes i vores Database
    @Override
    public List<Category> getAllCategories() {

        List<Category> showAllCategories = new ArrayList();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Category"); // Selecter alt fra vores Category table i vores Database
            ResultSet rs = ps.executeQuery();

            Category category;
            while(rs.next())
            {
                int id = rs.getInt(1);
                String categoryName = rs.getString(2);

                category = new Category(id,categoryName);
                showAllCategories.add(category); // Tilføjer hver kategori der findes til vores liste showAllCategories
            }
        }
        catch (SQLException e) {System.err.println("can not access records" + e.getMessage());}

        return showAllCategories;
    }


    // Funktion der fjerner en kategori fra vores Database
    @Override
    public void removeCategory(int categoryID) {
        try
        {
            PreparedStatement ps = con.prepareStatement("DELETE FROM Category WHERE categoryID = ?;"); // Fjerner den kategori der er valgt. Vores categoryID = den valgte kategori's ID
            ps.setInt(1,categoryID);
            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kunne ikke slette Category" + e.getMessage());}
    }


    // Funktion der opdaterer navnet på en kategori
    @Override
    public void updateCategory(String category, int categoryID) {
        try
        {
            PreparedStatement ps = con.prepareStatement("UPDATE Category SET categoryName = ? WHERE categoryID = ?;"); // Opdaterer den valgte kategori's navn

            ps.setString(1, category);
            ps.setInt(2,categoryID);

            ps.executeUpdate();
        }
        catch (SQLException e) {System.err.println("Kan ikke ændre Category navn " + e.getMessage());}
    }
}
