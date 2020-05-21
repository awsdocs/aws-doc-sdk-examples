package com.aws.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper
{
    private String url;

    private static ConnectionHelper instance;
    private ConnectionHelper()
    {
        url = "jdbc:mysql://localhost:3306/mydb"; // REPLACE with URL to RDS instance
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null) {
            instance = new ConnectionHelper();
        }
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(instance.url, "root","root"); //REPLACE with user name and password to RDS instance
        }
        catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }
    public static void close(Connection connection)
    {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
