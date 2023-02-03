package com.cloud.Enchere.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Categorie {
    private Integer id;
    private String nom;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void findById(Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select * from categorie where idcategorie=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()) {
                id = rs.getInt("idcategorie");
                nom = rs.getString("nomcategorie");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }
}
