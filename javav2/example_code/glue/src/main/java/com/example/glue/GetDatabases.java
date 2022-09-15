//snippet-sourcedescription:[GetDatabases.java demonstrates how to get databases.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS Glue]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glue;

//snippet-start:[glue.java2.get_databases.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.Database;
import software.amazon.awssdk.services.glue.model.GetDatabasesRequest;
import software.amazon.awssdk.services.glue.model.GetDatabasesResponse;
import software.amazon.awssdk.services.glue.model.GlueException;
import java.util.List;
//snippet-end:[glue.java2.get_databases.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDatabases {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getAllDatabases(glueClient);
        glueClient.close();
    }

    //snippet-start:[glue.java2.get_databases.main]
    public static void getAllDatabases(GlueClient glueClient) {

        try {
            GetDatabasesRequest databasesRequest = GetDatabasesRequest.builder()
                .maxResults(10)
                .build();

            GetDatabasesResponse response = glueClient.getDatabases(databasesRequest);
            List<Database> databases = response.databaseList();
            for (Database database: databases) {
                System.out.println("The Database name is : "+database.name());
            }

        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
     }
    //snippet-end:[glue.java2.get_databases.main]
   }
