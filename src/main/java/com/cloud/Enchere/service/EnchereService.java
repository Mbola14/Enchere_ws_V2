package com.cloud.Enchere.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cloud.Enchere.database.DatabaseConnection;
import com.cloud.Enchere.exceptions.EmptySearchParamException;
import com.cloud.Enchere.exceptions.EmptySearchResultException;
import com.cloud.Enchere.model.Categorie;
import com.cloud.Enchere.model.Data;
import com.cloud.Enchere.model.Enchere;
import com.cloud.Enchere.model.SearchModel;
import com.cloud.Enchere.model.User;


@Service
public class EnchereService {
    public List<Enchere> fetchAll() throws ClassNotFoundException, SQLException {
        return Enchere.findAll();
    }

    public Enchere fetchById(int enchereId) throws ClassNotFoundException, SQLException {
        Enchere enchere = new Enchere();
        enchere.setId(enchereId);
        return enchere.findById();
    }

    public Data search(SearchModel searchModel) throws ClassNotFoundException, SQLException {
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Data data = new Data();
        String desc = searchModel.getDescription();
        Integer categorieId = searchModel.getCategorieId();
        Date date = searchModel.getDate();
        Double prix = searchModel.getPrix();
        Integer status = searchModel.getStatut();

        String sql = "select * from enchere where ";
        if(desc != null || date != null ||
            prix != null ||  categorieId != null || status != null) {
            List<String> requests = new ArrayList<>();
            if(desc != null) {
                requests.add("description like '%"+desc+"%'");
            }
            if(date != null) {
                requests.add("(dateheure::date)='"+date+"'");
            }
            if(prix != null) {
                requests.add("prixmiseenchere="+prix);
            }
            if(categorieId != null) {
                requests.add("idcategorie="+categorieId);
            }
            if(status != null) {
                requests.add("status="+status);
            }

            for(int i = 0; i < requests.size(); i += 1) {
                if(i == 0) sql += requests.get(i) + " ";
                else sql += " and " + requests.get(i); 
            }

            System.out.println(sql);

            try {
				connection = dbc.connect();
                stmt = connection.prepareStatement(sql);
                rs = stmt.executeQuery();
                List<Enchere> results = new ArrayList<Enchere>();
                boolean noRes = true;
                while(rs.next()) {
                    noRes = false;
                    Enchere result = new Enchere();
                    result.setId(rs.getInt("idenchere"));
                    User user = new User();
                    user.setIdUser(rs.getInt("idutilisateur"));
                    user.findById(connection);
                    result.setUtilisateur(user);
                    Categorie categorie = new Categorie();
                    categorie.setId(rs.getInt("idcategorie"));
                    categorie.findById(connection);
                    result.setCategorie(categorie);
                    result.setBeginning(rs.getTimestamp("dateheure").toLocalDateTime());
                    result.setMise_enchere(rs.getDouble("prixmiseenchere"));
                    result.setDuration(rs.getFloat("duree"));
                    result.setDescription(rs.getString("description"));
                    result.setStatus(rs.getInt("status"));
                    results.add(result);
                } 
                if(noRes) { 
                    EmptySearchResultException esrExc = new EmptySearchResultException("aucun résultat");
                    data.setException(esrExc.getMessage());
                }
                else data.setResult(results);
			} catch (ClassNotFoundException | SQLException e) {
                throw e;
			} finally {
                if(connection != null && !connection.isClosed()) connection.close();
                if(stmt != null && !stmt.isClosed()) stmt.close();
                if(rs != null && !rs.isClosed()) rs.close();
            }
        } else {
            EmptySearchParamException espExc = new EmptySearchParamException("aucun paramètre de recherche");
            data.setException(espExc.getMessage());
        }

        return data;
    }

    public List<Enchere> findByUser(int userId) throws ClassNotFoundException, SQLException {
        List<Enchere> history_results = new ArrayList<>();
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dbc.connect();
            stmt = connection.prepareStatement("select * from enchere_global where idutilisateur=?");
            stmt.setInt(1, userId);

            rs = stmt.executeQuery();

            while(rs.next()) {
                Enchere e = new Enchere();
                e.setId(rs.getInt("idenchere"));
                e.setBeginning(rs.getTimestamp("dateheure").toLocalDateTime());
                e.setMise_enchere(rs.getDouble("prixmiseenchere"));
                e.setDuration(rs.getFloat("duree"));
                e.setDescription(rs.getString("description"));
                e.setStatus(rs.getInt("status"));
                User user = new User();
                user.setIdUser(rs.getInt("idutilisateur"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                e.setUtilisateur(user);
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("idcategorie"));
                categorie.setNom(rs.getString("nomcategorie"));
                e.setCategorie(categorie);
                history_results.add(e);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
        return history_results;
    }

    // new
    public List<Enchere> findByUserAndStatus(int userId, int status) throws ClassNotFoundException, SQLException {
        List<Enchere> history_results = new ArrayList<>();
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            connection = dbc.connect();
            stmt = connection.prepareStatement("select * from enchere_global where idutilisateur=? and status=?");
            stmt.setInt(1, userId);
            stmt.setInt(2, status);
            rs = stmt.executeQuery();

            while(rs.next()) {
                Enchere e = new Enchere();
                e.setId(rs.getInt("idenchere"));
                e.setBeginning(rs.getTimestamp("dateheure").toLocalDateTime());
                e.setMise_enchere(rs.getDouble("prixmiseenchere"));
                e.setDuration(rs.getFloat("duree"));
                e.setDescription(rs.getString("description"));
                e.setStatus(rs.getInt("status"));
                User user = new User();
                user.setIdUser(rs.getInt("idutilisateur"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                e.setUtilisateur(user);
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("idcategorie"));
                categorie.setNom(rs.getString("nomcategorie"));
                e.setCategorie(categorie);
                history_results.add(e);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
        return history_results;
    }

    public Enchere saveEnchere(Enchere newEnchere) throws ClassNotFoundException, SQLException {
        newEnchere.setBeginning(LocalDateTime.now());
        newEnchere.setStatus(1);
        return newEnchere.save();
    }

    public List<Enchere> getExpiredByUser(int userId) throws ClassNotFoundException, SQLException {
        List<Enchere> expired = new ArrayList<>();
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
			connection = dbc.connect();
            verify_expiration(connection, userId);
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement("select * from enchere_global where status=2 and idutilisateur=?");
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while(rs.next()) {
                Enchere e = new Enchere();
                e.setId(rs.getInt("idenchere"));
                e.setBeginning(rs.getTimestamp("dateheure").toLocalDateTime());
                e.setMise_enchere(rs.getDouble("prixmiseenchere"));
                e.setDuration(rs.getFloat("duree"));
                e.setDescription(rs.getString("description"));
                e.setStatus(rs.getInt("status"));
                User user = new User();
                user.setIdUser(rs.getInt("idutilisateur"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                e.setUtilisateur(user);
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("idcategorie"));
                categorie.setNom(rs.getString("nomcategorie"));
                e.setCategorie(categorie);
                expired.add(e);
            }
		} catch (ClassNotFoundException | SQLException e) {
			throw e;
		} finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return expired;
    }

    private void verify_expiration(Connection connection, int userId) throws ClassNotFoundException, SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement("select * from enchere_expiration");
            rs = stmt.executeQuery();

            while(rs.next()) {
                int enchereId = rs.getInt("idenchere");
                LocalDateTime expirationTime = rs.getTimestamp("ending").toLocalDateTime();
                if(verify(expirationTime)) {
                    expire(connection, stmt, rs, enchereId, userId);
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }
    }

    private void expire(Connection connection, PreparedStatement stmt, ResultSet rs, int enchereId, int userId) throws SQLException {
        try {
            connection.setAutoCommit(false);
            
            stmt = connection.prepareStatement("update enchere set status=2 where idenchere=?");
            stmt.setInt(1, enchereId);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("select idmouvement, montant from preview_transact where idenchere=? order by montant desc limit 1");
            stmt.setInt(1, enchereId);
            rs = stmt.executeQuery();
            int to_block_mvt_id = 0;
            double to_credit_sum = 0.0;
            if(rs.next()) { 
               to_credit_sum = rs.getDouble("montant");
               to_block_mvt_id = rs.getInt("idmouvement");
            }

            stmt = connection.prepareStatement("update mouvement set status=2 where idmouvement=?");
            stmt.setInt(1, to_block_mvt_id);
            stmt.executeUpdate();

            stmt = connection.prepareStatement("update compte set solde = solde + ? where idutilisateur = ?");
            stmt.setDouble(1, to_credit_sum);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            if(connection != null) connection.rollback();
            throw e;
        }
    }

    private boolean verify(LocalDateTime expTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if(currentDateTime.equals(expTime) || currentDateTime.isAfter(expTime)) return true;

        return false;
    }

    // new
    public List<Categorie> findAllCategorie() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Categorie> all_cat = new ArrayList<Categorie>();
        try {
            connection = new DatabaseConnection().connect();
            stmt = connection.prepareStatement("select * from categorie");
            rs = stmt.executeQuery();

            while(rs.next()) {
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("idcategorie"));
                categorie.setNom(rs.getString("nomcategorie"));
                all_cat.add(categorie);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw e;
        } finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return all_cat;
    }
}
