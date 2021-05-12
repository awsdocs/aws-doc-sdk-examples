//snippet-sourcedescription:[RetrieveData.java demonstrates how to query data and check the results by using a RedshiftDataClient object.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[04/05/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshiftdata;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.*;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementRequest;
import java.util.List;


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RetrieveData {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    RetrieveData <database> <dbUser> <sqlStatement> <clusterId> \n\n" +
                "Where:\n" +
                "    database - the name of the database (for example, dev) \n" +
                "    dbUser - the master user name \n" +
                "    sqlStatement - the sql statement to use (for example, select * from information_schema.tables;) \n" +
                "    clusterId - the id of the Redshift cluster (for example, redshift-cluster) \n" ;

          //if (args.length != 4) {
          //    System.out.println(USAGE);
          //    System.exit(1);
         // }

        String database =  "dev"; //args[0];
        String dbUser = "awsuser";  //args[1];
        String sqlStatement = "Select * from blog;";  //args[2];
        String clusterId = "redshift-cluster-1";  // args[3];

        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
                .region(region)
                .build();

        String id =  performSQLStatement(redshiftDataClient, database, dbUser, sqlStatement, clusterId);
        System.out.println("The identifier of the statement is "+id);
        checkStatement(redshiftDataClient,id );
        getResults(redshiftDataClient, id);
        redshiftDataClient.close();
    }

    public static void checkStatement(RedshiftDataClient redshiftDataClient,String sqlId ) {

        try {

            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                .id(sqlId)
                .build() ;

            // Wait until the sql statement processing is finished.
            boolean finished = false;
            String status = "";
            while (!finished) {

                DescribeStatementResponse response = redshiftDataClient.describeStatement(statementRequest);
                status = response.statusAsString();
                System.out.println("..."+status);

                if (status.compareTo("FINISHED") == 0) {
                    break;
                }
               Thread.sleep(1000);
            }

            System.out.println("The statement is finished!");

        } catch (RedshiftDataException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static String performSQLStatement(RedshiftDataClient redshiftDataClient,
                                             String database,
                                             String dbUser,
                                             String sqlStatement,
                                             String clusterId) {

        try {
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .database(database)
                .dbUser(dbUser)
                .sql(sqlStatement)
                .build();

            ExecuteStatementResponse response = redshiftDataClient.executeStatement(statementRequest);
            return response.id();

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }


    public static void getResults(RedshiftDataClient redshiftDataClient, String statementId) {

        try {

            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                    .id(statementId)
                    .build();

            GetStatementResultResponse response = redshiftDataClient.getStatementResult(resultRequest);

            // Iterate through the List element where each element is a List object.
            List<List<Field>> dataList = response.records();

            // Print out the records.
            for (List list: dataList) {

                for (Object myField:list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();
                    if (value != null)
                          System.out.println("The value of the field is " + value);
                    }
                }

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
