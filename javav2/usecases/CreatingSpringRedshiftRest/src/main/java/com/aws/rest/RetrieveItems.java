/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import java.util.ArrayList ;
import java.util.List;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.Field;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultRequest;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementRequest;

@Component
public class RetrieveItems {

    // Specify the database name, the database user, and the cluster Id value.
    private final String database = "dev";
    private final String dbUser ="awsuser";
    private final String clusterId = "redshift-cluster-1";

    private RedshiftDataClient getClient() {

        Region region = Region.US_WEST_2;
        return RedshiftDataClient.builder()
                  .region(region)
                  .credentialsProvider(ProfileCredentialsProvider.create())
                  .build();
    }

    // Update the work table.
    public void flipItemArchive(String id ) {

        RedshiftDataClient redshiftDataClient = getClient();
        int arc = 1;

        try {
            String sqlStatement = "update work set archive = '" + arc + "' where idwork ='" + id + "' ";
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                        .clusterIdentifier(clusterId)
                        .database(database)
                        .dbUser(dbUser)
                        .sql(sqlStatement)
                        .build();

            redshiftDataClient.executeStatement(statementRequest);

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Return items from the work table.
    public List<WorkItem> getData(int arch) {

        String username = "user";
        String sqlStatement = "Select * FROM work where username = '" +username +"' and archive = " + arch +"";
        RedshiftDataClient redshiftDataClient = getClient() ;
        String id = performSQLStatement(redshiftDataClient, database, dbUser, sqlStatement, clusterId);
        System.out.println("The identifier of the statement is "+id);
        checkStatement(redshiftDataClient,id );
        return getResults(redshiftDataClient, id);
    }

    public String performSQLStatement(RedshiftDataClient redshiftDataClient,
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

    public void checkStatement(RedshiftDataClient redshiftDataClient,String sqlId ) {

        try {
            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                                .id(sqlId)
                                .build() ;

            // Wait until the sql statement processing is finished.
            String status;
            while (true) {
                DescribeStatementResponse response = redshiftDataClient.describeStatement(statementRequest);
                status = response.statusAsString();
                System.out.println("..."+status);

                if (status.compareTo("FINISHED") == 0) {
                    break;
                }
                Thread.sleep(500);
            }

            System.out.println("The statement is finished!");

        } catch (RedshiftDataException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public List<WorkItem> getResults(RedshiftDataClient redshiftDataClient, String statementId) {

        try {

            List<WorkItem>records = new ArrayList<>();
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                        .id(statementId)
                        .build();

            GetStatementResultResponse response = redshiftDataClient.getStatementResult(resultRequest);
            WorkItem workItem ;
            int index;

            // Iterate through the List element where each element is a List object.
            List<List<Field>> dataList = response.records();

            // Get the records.
            for (List<Field> list: dataList) {

                // New WorkItem object.
                workItem = new WorkItem();
                index = 0;
                for (Field field:list) {
                    String value = field.stringValue();
                    if (index == 0)
                        workItem.setId(value);

                    else if (index == 1)
                        workItem.setDate(value);

                    else if (index == 2)
                        workItem.setDescription(value);

                    else if (index == 3)
                        workItem.setGuide(value);

                    else if (index == 4)
                        workItem.setStatus(value);

                    else if (index == 5)
                        workItem.setName(value);

                    // Increment the index.
                    index++;
                }

                // Push the object to the List.
                records.add(workItem);
            }
            return records;

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}

