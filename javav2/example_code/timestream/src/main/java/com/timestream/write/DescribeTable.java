//snippet-sourcedescription:[DescribeTable.java demonstrates how to return information about a database table.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Timestream]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05-04-2022]
//snippet-sourceauthor:[scmacdon- AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.timestream.write;

//snippet-start:[timestream.java2.desc_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.DescribeTableRequest;
import software.amazon.awssdk.services.timestreamwrite.model.DescribeTableResponse;
import software.amazon.awssdk.services.timestreamwrite.model.TimestreamWriteException;
//snippet-end:[timestream.java2.desc_table.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeTable {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <dbName> <tableName>\n\n" +
                "Where:\n" +
                "   dbName - The name of the database.\n\n" +
                "   tableName - The name of the table.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String dbName = args[0];
        String tableName = args[1];
        TimestreamWriteClient timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        describeSingleTable(timestreamWriteClient,dbName, tableName);
        timestreamWriteClient.close();
    }

    //snippet-start:[timestream.java2.desc_table.main]
    public static void describeSingleTable(TimestreamWriteClient timestreamWriteClient, String dbName, String tableName) {

        try {

            System.out.println("Describing table");
            DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                    .databaseName(dbName)
                    .tableName(tableName)
                    .build();

            DescribeTableResponse response = timestreamWriteClient.describeTable(describeTableRequest);
            String tableId = response.table().arn();
            System.out.println("Table " + tableName + " has id " + tableId);

        } catch (TimestreamWriteException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[timestream.java2.desc_table.main]
}