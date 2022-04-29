//snippet-sourcedescription:[UpdateTable.java demonstrates how to update a database table.]
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

//snippet-start:[timestream.java2.update_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.RetentionProperties;
import software.amazon.awssdk.services.timestreamwrite.model.TimestreamWriteException;
import software.amazon.awssdk.services.timestreamwrite.model.UpdateTableRequest;
//snippet-end:[timestream.java2.update_table.import]

/**
 * To run this Java V2 code example, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UpdateTable {

    static Long HT_TTL_HOURS = 100L;
    static Long CT_TTL_DAYS = 110L;

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <dbName> <newTable>\n\n" +
                "Where:\n" +
                "   dbName - the name of the database.\n\n" +
                "   newTable - the name of the table.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
         }

        String dbName = args[0];
        String tableName = args[1];
        TimestreamWriteClient timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        updateTable(timestreamWriteClient, dbName, tableName);
        timestreamWriteClient.close();
    }

    //snippet-start:[timestream.java2.update_table.main]
    public static void updateTable( TimestreamWriteClient timestreamWriteClient, String dbName, String tableName ) {

            System.out.println("Updating table");

            try {
                RetentionProperties retentionProperties = RetentionProperties.builder()
                    .memoryStoreRetentionPeriodInHours(HT_TTL_HOURS)
                    .magneticStoreRetentionPeriodInDays(CT_TTL_DAYS)
                    .build();

                UpdateTableRequest updateTableRequest = UpdateTableRequest.builder()
                    .databaseName(dbName)
                    .tableName(tableName)
                    .retentionProperties(retentionProperties)
                    .build();

                timestreamWriteClient.updateTable(updateTableRequest);
                System.out.println("Table updated");

            } catch (TimestreamWriteException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }
        //snippet-end:[timestream.java2.update_table.main]
    }

