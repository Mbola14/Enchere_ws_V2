package com.cloud.Enchere.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Compte {
    private int id;
    private User utilisateur;
    private Double solde;

    public Double getSolde() {
		return solde;
	}

	public void setSolde(Double solde) {
		this.solde = solde;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public User getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(User utilisateur) {
		this.utilisateur = utilisateur;
	}

    public void findById(Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select * from compte where idcompte=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()) {
                id = rs.getInt("idcategorie");
				User usr = new User();
				usr.setIdUser(rs.getInt("idutilisateur"));
				usr.findById(connection);
				utilisateur = usr;
				solde = rs.getDouble("solde");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }
}