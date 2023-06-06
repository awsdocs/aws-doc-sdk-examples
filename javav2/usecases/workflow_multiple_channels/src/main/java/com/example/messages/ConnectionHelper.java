/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.messages;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    private static ConnectionHelper instance;
    private String url;
    private ConnectionHelper() {
        url = "jdbc:mysql://localhost:3306/mydb?useSSL=false";
    }

    public static Connection getConnection() throws SQLException {

      /*
        if (instance == null)
            instance = new ConnectionHelper();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(instance.url, "root", "root1234");
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.getStackTrace();
        }
        return null;

       */

            try {
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://awstracker.csf1if1wwrox.us-east-1.rds.amazonaws.com:3306/mydatabase";
                String username = "root";
                String password = "root1234";

                return DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC driver not found.", e);
            }
    }
}