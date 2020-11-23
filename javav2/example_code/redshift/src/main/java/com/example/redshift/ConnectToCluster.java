//snippet-sourcedescription:[ConnectToCluster.java demonstrates how to connect to an Amazon Redshift cluster using the JDBC API.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.redshift;

// snippet-start:[firehose.java2.connect.import]
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.ResultSet;
// snippet-end:[firehose.java2.connect.import]

/**
 * If you have issues connecting, check your inbound rules that belong to the security group
 */

public class ConnectToCluster {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ConnectToCluster <dbURL><masterUsername><masterUserPassword> \n\n" +
                "Where:\n" +
                "    dbURL - the URL to the Redshift cluster \n" +
                "    masterUsername - the master user name \n" +
                "    masterUserPassword - the password that corresponds to the master user name \n" ;

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String dbURL = args[0];
        String masterUsername = args[1];
        String masterUserPassword = args[2];

        connectCluster(dbURL, masterUsername, masterUserPassword) ;
    }

      // snippet-start:[firehose.java2.connect.main]
      public static void connectCluster(String dbURL, String masterUsername, String masterUserPassword) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //Dynamically load driver at runtime.
            //Redshift JDBC 4.1 driver: com.amazon.redshift.jdbc41.Driver
            //Redshift JDBC 4 driver: com.amazon.redshift.jdbc4.Driver
            Class.forName("com.amazon.redshift.jdbc.Driver");

            //Open a connection and define properties.
            System.out.println("Connecting to database...");
            Properties props = new Properties();

            //Uncomment the following line if using a keystore.
            //props.setProperty("ssl", "true");
            props.setProperty("user", masterUsername);
            props.setProperty("password", masterUserPassword);
            conn = DriverManager.getConnection(dbURL, props);

            //Try a simple query.
            System.out.println("Listing system tables...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from information_schema.tables;";
            ResultSet rs = stmt.executeQuery(sql);

            //Get the data from the result set.
            while(rs.next()){
                //Retrieve two columns.
                String catalog = rs.getString("table_catalog");
                String name = rs.getString("table_name");

                //Display values.
                System.out.print("Catalog: " + catalog);
                System.out.println(", Name: " + name);
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException | ClassNotFoundException ex){
            //For convenience, handle all errors here.
            ex.printStackTrace();
            System.exit(1);
        }finally{
            //Finally block to close resources.
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException ex){
                System.exit(1);
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
        System.out.println("Finished connectivity test.");
    }
}
// snippet-end:[firehose.java2.connect.main]