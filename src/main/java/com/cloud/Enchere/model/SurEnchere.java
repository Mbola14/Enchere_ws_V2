package com.cloud.Enchere.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.cloud.Enchere.database.DatabaseConnection;

public class SurEnchere {
    private int id;
    private Enchere enchere;
    private User owner;
    private double montant;
	private static final DatabaseConnection dbc = new DatabaseConnection();

	public double getMontant() {
		return montant;
	}

	public void setMontant(double montant) {
		this.montant = montant;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Enchere getEnchere() {
		return enchere;
	}

	public void setEnchere(Enchere enchere) {
		this.enchere = enchere;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SurEnchere findLastProp(Connection connection) throws ClassNotFoundException, SQLException {
		SurEnchere surEnchere = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection = dbc.connect();
			stmt = connection.prepareStatement("select * from surenchere where idenchere=? order by idsurenchere desc limit 1");
			stmt.setInt(1, this.id);

			rs = stmt.executeQuery();

			if(rs.next()) {
				surEnchere = new SurEnchere();
				enchere = new Enchere();
				enchere.setId(rs.getInt("idenchere"));
				surEnchere.setEnchere(enchere);
				owner = new User();
				owner.setIdUser(rs.getInt("idutilisateur"));
				surEnchere.setOwner(owner);
				surEnchere.setMontant(rs.getDouble("montant"));
			}
		} catch (ClassNotFoundException | SQLException e) {
			throw e;
		} finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
		}

		return surEnchere;
	}

	public void findById(Connection connection) throws SQLException {
		PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("select * from surenchere where idsurenchere=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()) {
				
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
	}
}
