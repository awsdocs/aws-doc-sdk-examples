/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import java.util.List;

@SpringBootApplication
public class App {
    static final String database = "jobs";
    static final String username = "User";
    static final String archived = "1";
    public static final Region region = Region.US_EAST_1;
    private static String secretArn = "arn:aws:secretsmanager:us-east-1:814548047983:secret:sqlscott2-WEJX1b" ;
    private static String resourceArn = "arn:aws:rds:us-east-1:814548047983:cluster:database-4" ;
    static RdsDataClient getClient() {
        return RdsDataClient.builder().region(App.region).build();
    }

    static ExecuteStatementResponse execute(String sqlStatement, List<SqlParameter> parameters) {
        ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
            .resourceArn(resourceArn)
            .secretArn(secretArn)
            .database(database)
            .sql(sqlStatement)
            .parameters(parameters)
            .build();
        return App.getClient().executeStatement(sqlRequest);
    }

    static SqlParameter param(String name, String value) {
        return SqlParameter.builder().name(name).value(Field.builder().stringValue(value).build()).build();
    }

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(App.class, args);
    }
}