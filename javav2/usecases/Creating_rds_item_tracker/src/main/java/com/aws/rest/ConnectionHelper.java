/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    private static ConnectionHelper instance;
    private String url;

    private ConnectionHelper() {
        url = "jdbc:mysql://formit.crpo45shym6k.us-west-2.rds.amazonaws.com:3306/mydb?useSSL=false";
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null) {
            instance = new ConnectionHelper();
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(instance.url, "root","root1234");

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.getStackTrace();
        }
        return null;
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}