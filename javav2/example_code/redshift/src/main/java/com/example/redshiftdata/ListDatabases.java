 //snippet-sourcedescription:[ListDatabases.java demonstrates how to list databases and tables that are part of a cluster by using a RedshiftDataClient object.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Redshift]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshiftdata;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.ListDatabasesRequest;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.ListDatabasesResponse;
import software.amazon.awssdk.services.redshiftdata.model.ListTablesRequest;
import software.amazon.awssdk.services.redshiftdata.model.ListTablesResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.model.TableMember;
import java.util.List;


 /**
  * Before running this Java V2 code example, set up your development environment, including your credentials.
  *
  * For more information, see the following documentation topic:
  *
  * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
  */
public class ListDatabases {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    ListDatabases <database> <dbUser> <sqlStatement> <clusterId> \n\n" +
            "Where:\n" +
            "    database - The name of the database (for example, dev) \n" +
            "    dbUser - The master user name \n" +
            "    clusterId - The id of the Redshift cluster (for example, redshift-cluster) \n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String database = args[0];
        String dbUser = args[1];
        String clusterId = args[2];
        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listAllDatabases(redshiftDataClient,clusterId, dbUser, database) ;
        listAllTables(redshiftDataClient,clusterId, dbUser, database);
        redshiftDataClient.close();
    }

    public static void listAllDatabases(RedshiftDataClient redshiftDataClient,String clusterId, String dbUser, String database) {

        try {
            ListDatabasesRequest databasesRequest = ListDatabasesRequest.builder()
                .clusterIdentifier(clusterId)
                .dbUser(dbUser)
                .database(database)
                .build();

            ListDatabasesResponse databasesResponse = redshiftDataClient.listDatabases(databasesRequest);
            List<String> databases = databasesResponse.databases();
            for (String dbName: databases) {
                System.out.println("The database name is : "+dbName);
            }

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void listAllTables(RedshiftDataClient redshiftDataClient,String clusterId, String dbUser, String database){

        try {
            ListTablesRequest tablesRequest = ListTablesRequest.builder()
                .clusterIdentifier(clusterId)
                .database(database)
                .dbUser(dbUser)
                .build();

            ListTablesResponse tablesResponse = redshiftDataClient.listTables(tablesRequest);
            List<TableMember> tables = tablesResponse.tables();
            for (TableMember table: tables) {
                System.out.println("The table name is : "+table.name());
            }

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
