package com.cloud.Enchere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.cloud.Enchere.database.DatabaseConnection;
import com.cloud.Enchere.exceptions.NotEnoughSoldException;
import com.cloud.Enchere.exceptions.SelfEnchereException;
import com.cloud.Enchere.exceptions.SurEnchereMontantException;
import com.cloud.Enchere.model.Compte;
import com.cloud.Enchere.model.SurEnchere;

@Service
public class SurEnchereService {
    public void rencherir(SurEnchere newProp) throws SQLException, ClassNotFoundException, 
                                                SurEnchereMontantException, NotEnoughSoldException, SelfEnchereException {
        
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dbc.connect();
            compare(newProp, connection);
            verifySelf(newProp);
			Compte userAccount = newProp.getOwner().getAccount(connection, stmt, rs);
            double propMontant = newProp.getMontant();
            verifySolde(userAccount, propMontant);
            connection.setAutoCommit(false);

            stmt = connection.prepareStatement("select * from preview_transact where idenchere=? order by montant desc limit 1");
            stmt.setInt(1, newProp.getEnchere().getId());
            int preview_transact_account_id = 0;
            int preview_transact_mvt_id = 0;
            double preview_transact_montant = 0.0;
            rs = stmt.executeQuery();
            if(rs.next()) {
                preview_transact_mvt_id = rs.getInt("idmouvement");
                preview_transact_account_id = rs.getInt("idcompte");
                preview_transact_montant = rs.getDouble("montant");

                stmt = connection.prepareStatement("update compte set solde = solde + ? where idcompte = ?");
                stmt.setDouble(1, preview_transact_montant);
                stmt.setInt(2, preview_transact_account_id);
                stmt.executeUpdate();

                stmt = connection.prepareStatement("update mouvement set status=1 where idmouvement=?");
                stmt.setInt(1, preview_transact_mvt_id);
                stmt.executeUpdate();
            }

            stmt = connection.prepareStatement("update compte set solde = solde - ? where idcompte = ?");
            stmt.setDouble(1, propMontant);
            stmt.setInt(2, userAccount.getId());
            stmt.executeUpdate();

            stmt = connection.prepareStatement("insert into mouvement(montant,idcompte,idenchere) values (?,?,?)");
            stmt.setDouble(1, propMontant);
            stmt.setInt(2, userAccount.getId());
            stmt.setInt(3, newProp.getEnchere().getId());
            stmt.executeUpdate();

            stmt = connection.prepareStatement("insert into surenchere(idenchere,idutilisateur,montant) values (?,?,?)");
            stmt.setInt(1, newProp.getEnchere().getId());
            stmt.setInt(2, newProp.getOwner().getIdUser());
            stmt.setDouble(3, propMontant);
            stmt.executeUpdate();

            connection.commit();
		} catch (SQLException | ClassNotFoundException e) {
            if(connection != null) connection.rollback();
			throw e;
		} finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }

    private void compare(SurEnchere newProp, Connection connection) throws SurEnchereMontantException, SQLException, ClassNotFoundException {
        SurEnchere lastProp = newProp.findLastProp(connection);
        if(lastProp != null && newProp.getMontant() <= lastProp.getMontant()) throw new SurEnchereMontantException("montant :"+newProp.getMontant()+" inférieur ou égale au dernier montant proposer");
    }

    private void verifySolde(Compte compte, double montantProp) throws NotEnoughSoldException {
        if(compte.getSolde() < montantProp) throw new NotEnoughSoldException("votre solde est insuffisant pour effectuer cette transaction");
    }

    private void verifySelf(SurEnchere newProp) throws SelfEnchereException {
        if(newProp.getEnchere().getUtilisateur().getIdUser().equals(newProp.getOwner().getIdUser())) 
            throw new SelfEnchereException("vous ne pouvez pas enchérir sur votre propre enchère");
    }
}