//snippet-sourcedescription:[RetrieveData.java demonstrates how to query data and check the results by using a RedshiftDataClient object.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Redshift]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshiftdata;

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
import java.util.List;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * In addition,  set up a serverless version by following this documentation topic:
 *
 *  https://docs.aws.amazon.com/redshift/latest/mgmt/serverless-getting-started.html
 */
public class RetrieveDataServerless {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    RetrieveData <database> <sqlStatement>  \n\n" +
            "Where:\n" +
            "    database - The name of the database (for example, sample_data_dev). \n" +
            "    sqlStatement - The sql statement to use. \n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String database = "sample_data_dev" ;
        String sqlStatement = "Select * from tickit.sales" ;
        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        String id = performSQLStatement(redshiftDataClient, database, sqlStatement);
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
            String status;
            while (true) {
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
                                             String sqlStatement) {

        try {
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                .database(database)
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
            List<List<Field>> dataList = response.records();
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
