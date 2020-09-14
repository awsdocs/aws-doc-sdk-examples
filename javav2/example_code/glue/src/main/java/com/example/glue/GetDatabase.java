//snippet-sourcedescription:[GetDatabase.java demonstrates how to get a database.]
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

//snippet-start:[glue.java2.get_database.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GetDatabaseRequest;
import software.amazon.awssdk.services.glue.model.GetDatabaseResponse;
import software.amazon.awssdk.services.glue.model.GlueException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
//snippet-end:[glue.java2.get_database.import]

public class GetDatabase {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the database.  \n" +
                "\n" +
                "Example: GetDatabase <databaseName>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String databaseName = args[0];

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .build();

        getSpecificDatabase(glueClient, databaseName);
    }

    //snippet-start:[glue.java2.get_database.main]
    public static void getSpecificDatabase(GlueClient glueClient, String databaseName) {

      try {
            GetDatabaseRequest databasesRequest = GetDatabaseRequest.builder()
                .name(databaseName)
                .build();

            GetDatabaseResponse response = glueClient.getDatabase(databasesRequest);
            Instant createDate = response.database().createTime();

            // Convert the Instant to readable date
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                            .withLocale( Locale.US)
                            .withZone( ZoneId.systemDefault() );

            formatter.format( createDate );
            System.out.println("The create date of the database is " + createDate );

          } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[glue.java2.get_database.main]
}
