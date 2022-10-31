/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultRequest;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.model.SqlParameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WorkItemRepository {
    static final String active = "0";
    static final String username = "user";

    // Specify the database name, the database user, and the cluster Id value.
    private static final String database = "dev";
    private static final String dbUser ="awsuser";
    private static final String clusterId = "redshift-cluster-1";

    RedshiftDataClient getClient() {
        Region region = Region.US_WEST_2;
        return RedshiftDataClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
    }

    // Return items from the work table.
    public List<WorkItem> getData(String arch) {
        String sqlStatement;
        List<SqlParameter> parameters;

        // Get all records from the Amazon Redshift table.
        if (arch.compareTo("") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work";
            ExecuteStatementResponse response = executeAll(sqlStatement);
            String id = response.id();
            System.out.println("The identifier of the statement is "+id);
            checkStatement(id);
            return getResults(id);
        } else {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive " +
                "FROM work WHERE username = :username and archive = :arch ;";

            parameters = List.of(
                param("username", username),
                param("arch", arch)
            );
            ExecuteStatementResponse response = execute(sqlStatement,parameters);
            String id = response.id();
            System.out.println("The identifier of the statement is "+id);
            checkStatement(id);
            return getResults(id);
        }
    }

    List<WorkItem> getResults(String statementId) {
        try {
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                .id(statementId)
                .build();

            GetStatementResultResponse response = getClient().getStatementResult(resultRequest);
            return response
                .records()
                .stream()
                .map(WorkItem::from)
                .collect(Collectors.toUnmodifiableList());

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // Update the work table.
    void flipItemArchive(String sqlStatement, List<SqlParameter> parameters ) {
        try {
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .database(database)
                .dbUser(dbUser)
                .sql(sqlStatement)
                .parameters(parameters)
                .build();

            getClient().executeStatement(statementRequest);

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    void checkStatement(String sqlId ) {
        try {
            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                .id(sqlId)
                .build() ;

            // Wait until the sql statement processing is finished.
            String status;
            while (true) {
                DescribeStatementResponse response = getClient().describeStatement(statementRequest);
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

    ExecuteStatementResponse execute(String sqlStatement, List<SqlParameter> parameters) {
        ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .database(database)
            .dbUser(dbUser)
            .sql(sqlStatement)
            .parameters(parameters)
            .build();
        return getClient().executeStatement(sqlRequest);
    }

    ExecuteStatementResponse executeAll(String sqlStatement) {
        ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
            .clusterIdentifier(clusterId)
            .database(database)
            .dbUser(dbUser)
            .sql(sqlStatement)
            .build();
        return getClient().executeStatement(sqlRequest);
    }

    SqlParameter param(String name, String value) {
        return SqlParameter.builder().name(name).value(value).build();
    }

    // Update the work table.
    public void flipItemArchive(String id ) {
        String arc = "1";
        String sqlStatement = "update work set archive = :arc where idwork =:id ";
        List<SqlParameter> parameters = List.of(
            param("arc", arc),
            param("id", id)
        );

        flipItemArchive(sqlStatement,parameters);
    }

    public String injectNewSubmission(WorkItem item) {
        try {
            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();
            String archived = "0";
            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date(date1.getTime());

            String sql = "INSERT INTO work (idwork, username, date, description, guide, status, archive) VALUES" +
                "(:idwork, :username, :date, :description, :guide, :status, :archive);";
            List<SqlParameter> paremeters = List.of(
                param("idwork", workId),
                param("username", name),
                param("date", sqlDate.toString()),
                param("description", description),
                param("guide", guide),
                param("status", status),
                param("archive", archived)
            );

            ExecuteStatementResponse result = execute(sql, paremeters);
            System.out.println(result.toString());
            return workId;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}

