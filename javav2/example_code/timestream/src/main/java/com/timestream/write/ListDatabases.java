//snippet-sourcedescription:[ListDatabases.java demonstrates how to return a list of your databases.]
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

//snippet-start:[timestream.java2.listdatabases.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.Database;
import software.amazon.awssdk.services.timestreamwrite.model.ListDatabasesRequest;
import software.amazon.awssdk.services.timestreamwrite.model.ListDatabasesResponse;
import software.amazon.awssdk.services.timestreamwrite.model.TimestreamWriteException;
import software.amazon.awssdk.services.timestreamwrite.paginators.ListDatabasesIterable;
//snippet-end:[timestream.java2.listdatabases.import]

import java.util.List;

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDatabases {

    public static void main(String[] args){

        TimestreamWriteClient timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllDatabases(timestreamWriteClient);
        timestreamWriteClient.close();
    }
    //snippet-start:[timestream.java2.listdatabases.main]
    public static void listAllDatabases(TimestreamWriteClient timestreamWriteClient) {

        try {
            System.out.println("Listing databases:");
            ListDatabasesRequest request = ListDatabasesRequest.builder().maxResults(2).build();
            ListDatabasesIterable listDatabasesIterable = timestreamWriteClient.listDatabasesPaginator(request);
            for (ListDatabasesResponse listDatabasesResponse : listDatabasesIterable) {
                final List<Database> databases = listDatabasesResponse.databases();
                databases.forEach(database -> System.out.println(database.databaseName()));
            }

        } catch (TimestreamWriteException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[timestream.java2.listdatabases.main]
}
