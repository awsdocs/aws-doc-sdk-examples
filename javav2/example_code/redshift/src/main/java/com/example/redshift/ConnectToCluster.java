//snippet-sourcedescription:[ConnectToCluster.java demonstrates how to connect to an Amazon Redshift cluster using the JDBC API and AWS Secrets Manager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Redshift ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.redshift;

// snippet-start:[firehose.java2.connect.import]
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.ResultSet;
// snippet-end:[firehose.java2.connect.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * If you have issues connecting, check your inbound rules that belong to the security group.
 *
 * To run this example, create an Amazon Redshift cluster and then create a database named dev.
 * Next, create a table named Work that contains the specific fields. For details, see the Creating the
 * Resource section in the following article:
 *
 * https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/CreatingSpringRedshiftRest
 *
 * This example requires an AWS Secrets Manager secret that contains the database credentials. If you do not create a
 * secret, this example will not work. For details, see:
 *
 * https://docs.aws.amazon.com/secretsmanager/latest/userguide/integrating_how-services-use-secrets_RS.html
 */

public class ConnectToCluster {

    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage:\n" +
            "    <secretName> \n\n" +
            "Where:\n" +
            "    secretName - The name of the AWS Secrets Manager secret that contains the database credentials" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
       }

        // Get the Amazon RDS credentials from AWS Secrets Manager.
        String secretName = args[0];
        Gson gson = new Gson();
        User user = gson.fromJson(String.valueOf(getSecretValues(secretName)), User.class);
        connectCluster(user) ;
    }
    // Get the Amazon Redshift credentials from AWS Secrets Manager.
    private static String getSecretValues(String secretName) {
        SecretsManagerClient secretClient = getSecretClient();
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }
    private static SecretsManagerClient getSecretClient() {
        Region region = Region.US_WEST_2;
        return SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }

    // snippet-start:[firehose.java2.connect.main]
    public static void connectCluster(User user) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //Dynamically load driver at runtime.
            //Redshift JDBC 4 driver: com.amazon.redshift.jdbc4.Driver
            Class.forName("com.amazon.redshift.jdbc.Driver");

            // Open a connection and define properties.
            System.out.println("Connecting to database...");
            Properties props = new Properties();

            // Uncomment the following line if using a keystore.
            // props.setProperty("ssl", "true");
            String host = "jdbc:redshift://"+user.getHost()+":5439/dev" ;
            String userName = user.getUsername();
            String password = user.getPassword();
            props.setProperty("user", userName );
            props.setProperty("password", password );
            conn = DriverManager.getConnection(host, props);

            // A simple query to retrieve data from the work table.
            System.out.println("Listing data from the work table...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT idwork, description FROM work;";
            ResultSet rs = stmt.executeQuery(sql);

            //Get the data from the result set.
            while(rs.next()){
                // Retrieve two columns.
                String idWork = rs.getString("idwork");
                String description = rs.getString("description");

                //Display values.
                System.out.println("Id work: " + idWork);
                System.out.println("Description: " + description);
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