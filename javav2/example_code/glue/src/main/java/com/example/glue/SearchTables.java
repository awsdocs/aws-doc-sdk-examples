//snippet-sourcedescription:[SearchTables.java demonstrates how to search a set of tables based on properties.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-service:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/3/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
                "    text - A string used for a text search. \n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String text = args[0];

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        searchGlueTable(glueClient, text);
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
