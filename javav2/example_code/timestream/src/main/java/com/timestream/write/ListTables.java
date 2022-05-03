//snippet-sourcedescription:[ListTables.java demonstrates how to return a list of your tables.]
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

//snippet-start:[timestream.java2.list_tables.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import java.util.List;
import software.amazon.awssdk.services.timestreamwrite.model.ListTablesRequest;
import software.amazon.awssdk.services.timestreamwrite.model.ListTablesResponse;
import software.amazon.awssdk.services.timestreamwrite.model.Table;
import software.amazon.awssdk.services.timestreamwrite.model.TimestreamWriteException;
import software.amazon.awssdk.services.timestreamwrite.paginators.ListTablesIterable;
//snippet-end:[timestream.java2.list_tables.import]

/**
 * Before running this SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListTables {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <dbName>\n\n" +
                "Where:\n" +
                "   dbName - The name of the database.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String dbName = args[0];
        TimestreamWriteClient timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllTables(timestreamWriteClient, dbName);
        timestreamWriteClient.close();
    }

    //snippet-start:[timestream.java2.list_tables.main]
    public static void listAllTables(TimestreamWriteClient timestreamWriteClient, String dbName) {
     try {

         System.out.println("Listing tables");
         ListTablesRequest request = ListTablesRequest.builder()
                 .databaseName(dbName)
                 .maxResults(10)
                 .build();

         ListTablesIterable listTablesIterable = timestreamWriteClient.listTablesPaginator(request);
         for(ListTablesResponse listTablesResponse : listTablesIterable) {
             final List<Table> tables = listTablesResponse.tables();
             tables.forEach(table -> System.out.println(table.tableName()));
         }

     } catch (TimestreamWriteException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
   }
    //snippet-end:[timestream.java2.list_tables.main]
}