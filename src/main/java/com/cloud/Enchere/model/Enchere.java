package com.cloud.Enchere.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cloud.Enchere.database.DatabaseConnection;

public class Enchere {
    private int id;
    private User utilisateur;
    private Categorie categorie;
    private LocalDateTime beginning;
    private double mise_enchere;
    private float duration;
    private String description;
    private int status;
    private static final DatabaseConnection dbc = new DatabaseConnection();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public double getMise_enchere() {
        return mise_enchere;
    }

    public void setMise_enchere(double mise_enchere) {
        this.mise_enchere = mise_enchere;
    }

    public LocalDateTime getBeginning() {
        return beginning;
    }

    public void setBeginning(LocalDateTime beginning) {
        this.beginning = beginning;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static List<Enchere> findAll() throws ClassNotFoundException, SQLException {
        List<Enchere> encheres = new ArrayList<>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dbc.connect();
            stmt = connection.prepareStatement("select * from enchere"); 
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Enchere e = new Enchere();
                e.setId(rs.getInt("idenchere"));
                User user = new User();
                user.setIdUser(rs.getInt("idutilisateur"));
                user.findById(connection);
                e.setUtilisateur(user);
                Categorie cat = new Categorie();
                cat.setId(rs.getInt("idcategorie"));
                cat.findById(connection);
                e.setCategorie(cat);
                e.setBeginning(rs.getTimestamp("dateheure").toLocalDateTime());
                e.setMise_enchere(rs.getDouble("prixmiseenchere"));
                e.setDuration(rs.getFloat("duree"));
                e.setDescription(rs.getString("description"));
                e.setStatus(rs.getInt("status"));
                encheres.add(e);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return encheres;
    }

    public void findById(Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select * from enchere where idenchere=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()) {
                id = rs.getInt("idenchere");
                User user = new User();
                user.setIdUser(rs.getInt("idutilisateur"));
                user.findById(connection);
                utilisateur = user;
                Categorie cat = new Categorie();
                cat.setId(rs.getInt("idcategorie"));
                cat.findById(connection);
                beginning = rs.getTimestamp("dateheure").toLocalDateTime();
                mise_enchere = rs.getDouble("prixmiseenchere");
                duration = rs.getFloat("duree");
                description = rs.getString("description");
                status = rs.getInt("status");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }

    public Enchere findById() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Enchere enchere = new Enchere();

        try {
            connection = dbc.connect();
            stmt = connection.prepareStatement("select * from enchere where idenchere=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()) {
                enchere.setId(rs.getInt("idenchere"));
                User user = new User();
                user.setIdUser(rs.getInt("idutilisateur"));
                user.findById(connection);
                enchere.setUtilisateur(user);
                Categorie cat = new Categorie();
                cat.setId(rs.getInt("idcategorie"));
                cat.findById(connection);
                enchere.setCategorie(cat);
                enchere.setBeginning(rs.getTimestamp("dateheure").toLocalDateTime());
                enchere.setMise_enchere(rs.getDouble("prixmiseenchere"));
                enchere.setDuration(rs.getFloat("duree"));
                enchere.setDescription(rs.getString("description"));
                enchere.setStatus(rs.getInt("status"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return enchere;
    }

    public Enchere save() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Enchere newEnchere = new Enchere();

        try {
			connection = dbc.connect();
            stmt = connection.prepareStatement("insert into enchere(idutilisateur,idcategorie,dateheure,prixmiseenchere,duree,description,status) values(?,?,?,?,?,?,?)");
            stmt.setInt(1, utilisateur.getIdUser());
            stmt.setInt(2, categorie.getId());
            stmt.setTimestamp(3, Timestamp.valueOf(beginning));
            stmt.setDouble(4, mise_enchere);
            stmt.setFloat(5, duration);
            stmt.setString(6, description);
            stmt.setInt(7, status);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("select idenchere from enchere order by idenchere desc limit 1");
            rs = stmt.executeQuery();

            if(rs.next()) {
                newEnchere.setId(rs.getInt("idenchere"));
                newEnchere.findById(connection);
            }

		} catch (ClassNotFoundException | SQLException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return newEnchere;
    }
}
