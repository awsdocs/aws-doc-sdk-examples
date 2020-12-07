//snippet-sourcedescription:[SearchTables.java demonstrates how to search a set of tables based on properties.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glue;

//snippet-start:[glue.java2.search_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GlueException;
import software.amazon.awssdk.services.glue.model.SearchTablesRequest;
import software.amazon.awssdk.services.glue.model.SearchTablesResponse;
import software.amazon.awssdk.services.glue.model.Table;
import java.util.List;
//snippet-end:[glue.java2.search_table.import]

/*
    Before running this example, run a crawler to produce a table within a database
 */
public class SearchTables {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    SearchTables <text>\n\n" +
                "Where:\n" +
                "    text - a string used for a text search. \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String text = args[0];
        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        searchGlueTable(glueClient, text);
        glueClient.close();
    }

    //snippet-start:[glue.java2.search_table.main]
    public static void searchGlueTable(GlueClient glueClient, String text) {

        try {
            SearchTablesRequest tablesRequest = SearchTablesRequest.builder()
                .searchText(text)
                .resourceShareType("ALL")
                .maxResults(10)
                .build();

            SearchTablesResponse tablesResponse = glueClient.searchTables(tablesRequest);

            List<Table> tables = tablesResponse.tableList();
            for (Table table: tables) {
                System.out.println("Table name is : "+table.name());
                System.out.println("Database name is : "+table.databaseName());
            }
        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[glue.java2.search_table.main]
}
