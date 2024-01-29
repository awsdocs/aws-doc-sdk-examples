// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[redshift.java.ConnectToCluster.complete]

package connection;

import java.sql.*;
import java.util.Properties;

public class ConnectToCluster {
   // Redshift driver:
   // "jdbc:redshift://x.y.us-west-2.redshift.amazonaws.com:5439/dev";
   static final String dbURL = "***jdbc cluster connection string ****";
   static final String MasterUsername = "***master user name***";
   static final String MasterUserPassword = "***master user password***";

   public static void main(String[] args) {
      Connection conn = null;
      Statement stmt = null;
      try {
         // Dynamically load driver at runtime.
         // Redshift JDBC 4.1 driver: com.amazon.redshift.jdbc41.Driver
         // Redshift JDBC 4 driver: com.amazon.redshift.jdbc4.Driver
         Class.forName("com.amazon.redshift.jdbc.Driver");

         // Open a connection and define properties.
         System.out.println("Connecting to database...");
         Properties props = new Properties();

         // Uncomment the following line if using a keystore.
         // props.setProperty("ssl", "true");
         props.setProperty("user", MasterUsername);
         props.setProperty("password", MasterUserPassword);
         conn = DriverManager.getConnection(dbURL, props);

         // Try a simple query.
         System.out.println("Listing system tables...");
         stmt = conn.createStatement();
         String sql;
         sql = "select * from information_schema.tables;";
         ResultSet rs = stmt.executeQuery(sql);

         // Get the data from the result set.
         while (rs.next()) {
            // Retrieve two columns.
            String catalog = rs.getString("table_catalog");
            String name = rs.getString("table_name");

            // Display values.
            System.out.print("Catalog: " + catalog);
            System.out.println(", Name: " + name);
         }
         rs.close();
         stmt.close();
         conn.close();
      } catch (Exception ex) {
         // For convenience, handle all errors here.
         ex.printStackTrace();
      } finally {
         // Finally block to close resources.
         try {
            if (stmt != null)
               stmt.close();
         } catch (Exception ex) {
         } // nothing we can do
         try {
            if (conn != null)
               conn.close();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      System.out.println("Finished connectivity test.");
   }
}
// snippet-end:[redshift.java.ConnectToCluster.complete]