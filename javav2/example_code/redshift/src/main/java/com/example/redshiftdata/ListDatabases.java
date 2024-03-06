// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.redshiftdata;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.ListDatabasesRequest;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.ListDatabasesResponse;
import software.amazon.awssdk.services.redshiftdata.model.ListTablesRequest;
import software.amazon.awssdk.services.redshiftdata.model.ListTablesResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.model.TableMember;
import software.amazon.awssdk.services.redshiftdata.paginators.ListDatabasesIterable;

import java.util.List;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDatabases {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    ListDatabases <database> <dbUser> <sqlStatement> <clusterId>\s

                Where:
                    database - The name of the database (for example, dev)\s
                    dbUser - The master user name\s
                    clusterId - The id of the Redshift cluster (for example, redshift-cluster)\s
                """;

      //  if (args.length != 3) {
      //      System.out.println(usage);
      //      System.exit(1);
      //  }

        String database = "dev" ;// args[0];
        String dbUser = "awsuser" ; // args[1];
        String clusterId = "redshift-cluster-wf" ; //args[2];
        Region region = Region.US_EAST_1;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
                .region(region)
                .build();

        listAllDatabases(redshiftDataClient, clusterId, dbUser, database);
        redshiftDataClient.close();
    }

    public static void listAllDatabases(RedshiftDataClient redshiftDataClient, String clusterId, String dbUser,
            String database) {
        try {
            ListDatabasesRequest databasesRequest = ListDatabasesRequest.builder()
                    .clusterIdentifier(clusterId)
                    .dbUser(dbUser)
                    .database(database)
                    .build();

            ListDatabasesIterable listDatabasesIterable = redshiftDataClient.listDatabasesPaginator(databasesRequest);
            listDatabasesIterable.stream()
                .flatMap(r -> r.databases().stream())
                .forEach(db -> System.out
                    .println("The database name is : " + db));

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
