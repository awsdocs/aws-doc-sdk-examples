//snippet-sourcedescription:[DeleteDatabase.java demonstrates how to delete a database.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Timestream]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.timestream.write;

//snippet-start:[timestream.java2.del_db.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.DeleteDatabaseRequest;
import software.amazon.awssdk.services.timestreamwrite.model.DeleteDatabaseResponse;
import software.amazon.awssdk.services.timestreamwrite.model.TimestreamWriteException;
//snippet-end:[timestream.java2.del_db.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteDatabase {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <dbName> \n\n" +
                "Where:\n" +
                "   dbName - The name of the database.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
         }

        String dbName = args[0];
        TimestreamWriteClient timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        delDatabase(timestreamWriteClient, dbName);
        timestreamWriteClient.close();
    }

    //snippet-start:[timestream.java2.del_db.main]
    public static void delDatabase(TimestreamWriteClient timestreamWriteClient, String dbName) {

        try {
            System.out.println("Deleting database");
            DeleteDatabaseRequest deleteDatabaseRequest = DeleteDatabaseRequest.builder()
                    .databaseName(dbName)
                    .build();

            DeleteDatabaseResponse result =  timestreamWriteClient.deleteDatabase(deleteDatabaseRequest);
            System.out.println("Delete database status: " + result.sdkHttpResponse().statusCode());

        } catch (TimestreamWriteException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[timestream.java2.del_db.main]
}