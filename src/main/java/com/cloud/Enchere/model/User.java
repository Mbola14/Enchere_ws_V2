package com.cloud.Enchere.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloud.Enchere.database.DatabaseConnection;

public class User {
    private Integer idUser;
    private String nom;
    private String prenom;
    private Date datenaissance;
    private String username;
    private String password;
    private static final DatabaseConnection dbc = new DatabaseConnection();

	public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public Date getDatenaissance() {
        return datenaissance;
    }
    public void setDatenaissance(Date datenaissance) {
        this.datenaissance = datenaissance;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

    public Compte getAccount(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {
        Compte compte = new Compte();

        stmt = connection.prepareStatement("select * from compte where idutilisateur=?");
        stmt.setInt(1, idUser);
        rs = stmt.executeQuery();

        if(rs.next()) {
            compte.setId(rs.getInt("idcompte"));
            compte.setUtilisateur(this);
            compte.setSolde(rs.getDouble("solde"));
        }
        return compte;
    }

    public static User findUserByEmailAndAndPassword(String username, String password) throws ClassNotFoundException, SQLException {
        User curr_user = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dbc.connect();
            stmt = connection.prepareStatement("select * from utilisateur where username=? and password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if(rs.next()) {
                curr_user = new User();
                curr_user.setIdUser(rs.getInt("idutilisateur"));
                curr_user.setNom(rs.getString("nom"));
                curr_user.setPrenom(rs.getString("prenom"));
                curr_user.setDatenaissance(rs.getDate("datenaissance"));
                curr_user.setUsername(rs.getString("username"));
                curr_user.setPassword(rs.getString("password"));
            } 
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return curr_user;
    }

    public static User findUserByEmailAndAndPassword(Connection connection, String username, String password) throws SQLException {
        User curr_user = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select * from utilisateur where username=? and password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if(rs.next()) {
                curr_user = new User();
                curr_user.setIdUser(rs.getInt("idutilisateur"));
                curr_user.setNom(rs.getString("nom"));
                curr_user.setPrenom(rs.getString("prenom"));
                curr_user.setDatenaissance(rs.getDate("datenaissance"));
                curr_user.setUsername(rs.getString("username"));
                curr_user.setPassword(rs.getString("password"));
            } 
        } catch (SQLException e) {
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return curr_user;
    }

    public void findById(Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select * from utilisateur where idutilisateur=?");
            stmt.setInt(1, idUser);
            rs = stmt.executeQuery();

            if(rs.next()) {
                idUser = rs.getInt("idutilisateur");
                nom = rs.getString("nom");
                prenom = rs.getString("prenom");
                datenaissance = rs.getDate("datenaissance");
                username = rs.getString("username");
                password = rs.getString("password");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }

    public void findById() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dbc.connect();
            stmt = connection.prepareStatement("select * from utilisateur where idutilisateur=?");
            stmt.setInt(1, idUser);
            rs = stmt.executeQuery();

            if(rs.next()) {
                idUser = rs.getInt("idutilisateur");
                nom = rs.getString("nom");
                prenom = rs.getString("prenom");
                datenaissance = rs.getDate("datenaissance");
                username = rs.getString("username");
                password = rs.getString("password");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }
}