package com.cloud.Enchere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.cloud.Enchere.model.User;
import com.cloud.Enchere.database.DatabaseConnection;
import com.cloud.Enchere.exceptions.AuthenticationException;

@Service
public class UsersService {
    public User authenticate(User auth) throws AuthenticationException, ClassNotFoundException, SQLException {
        User curr_user = User.findUserByEmailAndAndPassword(auth.getUsername(), DigestUtils.md5DigestAsHex(auth.getPassword().getBytes()));
        if(curr_user == null) {
            throw new AuthenticationException("username or password incorrect");
        }

        return curr_user;
    }

    public User authenticate(Connection connection, User auth) throws AuthenticationException, ClassNotFoundException, SQLException {
        User curr_user = User.findUserByEmailAndAndPassword(connection, auth.getUsername(), DigestUtils.md5DigestAsHex(auth.getPassword().getBytes()));
        if(curr_user == null) {
            throw new AuthenticationException("username or password incorrect");
        }

        return curr_user;
    }

    public User sign_up(User newUser) throws SQLException, ClassNotFoundException {
        newUser.setPassword(DigestUtils.md5DigestAsHex(newUser.getPassword().getBytes()));
        DatabaseConnection dbc = new DatabaseConnection();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
			connection = dbc.connect();
            connection.setAutoCommit(false);

            stmt = connection.prepareStatement("insert into utilisateur(nom,prenom,datenaissance,username,password) values(?,?,?,?,?)");
            stmt.setString(1, newUser.getNom());
            stmt.setString(2, newUser.getPrenom());
            stmt.setDate(3, newUser.getDatenaissance());
            stmt.setString(4, newUser.getUsername());
            stmt.setString(5, newUser.getPassword());
            stmt.executeUpdate();

            stmt = connection.prepareStatement("select * from utilisateur order by idutilisateur desc limit 1");
            rs = stmt.executeQuery();
            int lastUserId = 0;
            if(rs.next()) {
                lastUserId = rs.getInt("idutilisateur");
                newUser.setIdUser(lastUserId);
                newUser.setPassword(rs.getString("password"));
            }

            stmt = connection.prepareStatement("insert into compte(idutilisateur,solde) values(?,?)");
            stmt.setInt(1, lastUserId);
            stmt.setDouble(2, 0.0);
            stmt.executeUpdate();

            connection.commit();
		} catch (ClassNotFoundException | SQLException e) {
            if(connection != null) connection.rollback();
			throw e;
		} finally {
            if(connection != null && !connection.isClosed()) connection.close();
            if(stmt != null && !stmt.isClosed()) stmt.close();
            if(rs != null && !rs.isClosed()) rs.close();
        }

        return newUser;
    }
}