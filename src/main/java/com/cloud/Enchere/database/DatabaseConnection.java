package com.cloud.Enchere.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String driver = "org.postgresql.Driver"; 
    private static final String url = "jdbc:postgresql://localhost:5432/enchere_dev"; 
    private static final String username = "cloud_dev_local"; 
    private static final String pwd = "cloud";

    public Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        Connection con = DriverManager.getConnection(url,username,pwd);
        
        return con;
    }
}
