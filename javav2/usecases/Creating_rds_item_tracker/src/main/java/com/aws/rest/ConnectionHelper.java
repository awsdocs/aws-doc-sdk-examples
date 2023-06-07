/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    public static Connection getConnection(String host, String user, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url2 = "jdbc:mysql://"+host+":3306/mydb?useSSL=false";
            return DriverManager.getConnection(url2, user, password);

        } catch (SQLException | ClassNotFoundException e) {
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