//snippet-sourcedescription:[DescribeDatabase.java demonstrates how to return information about the database.]
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

//snippet-start:[timestream.java2.desc_databases.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.Database;
import software.amazon.awssdk.services.timestreamwrite.model.DescribeDatabaseRequest;
import software.amazon.awssdk.services.timestreamwrite.model.DescribeDatabaseResponse;
import software.amazon.awssdk.services.timestreamwrite.model.TimestreamWriteException;
//snippet-end:[timestream.java2.desc_databases.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeDatabase {

    public static void main(String[] args){

        final String USAGE = "\n" +
                "Usage: " +
                "   <dbName>\n\n" +
                "Where:\n" +
                "   dbName - The name of the database.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String dbName = args[0];
        TimestreamWriteClient timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        DescribeSingleDatabases(timestreamWriteClient, dbName);
        timestreamWriteClient.close();
    }

    //snippet-start:[timestream.java2.desc_databases.main]
    public static void DescribeSingleDatabases(TimestreamWriteClient timestreamWriteClient, String dbName) {

        System.out.println("Describing database");
        DescribeDatabaseRequest describeDatabaseRequest = DescribeDatabaseRequest.builder()
                .databaseName(dbName)
                .build();
        try {
            DescribeDatabaseResponse response = timestreamWriteClient.describeDatabase(describeDatabaseRequest);
            final Database databaseRecord = response.database();
            final String databaseId = databaseRecord.arn();
            System.out.println("Database " + dbName + " has id " + databaseId);

        } catch (TimestreamWriteException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[timestream.java2.desc_databases.main]
}
