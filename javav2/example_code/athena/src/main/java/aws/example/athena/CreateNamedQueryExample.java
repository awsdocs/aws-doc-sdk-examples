//snippet-sourcedescription:[CreateNamedQueryExample.java demonstrates how to create a named query]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/15/2020]
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

//snippet-start:[athena.java2.CreateNamedQueryExample.complete]
//snippet-start:[athena.java.CreateNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.CreateNamedQueryExample.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
//snippet-end:[athena.java2.CreateNamedQueryExample.import]

/**
 * CreateNamedQueryExample
 * -------------------------------------
 * This code shows how to create a named query.
 */
public class CreateNamedQueryExample {
    public static void main(String[] args) throws Exception {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateNamedQueryExample <name>\n\n" +
                "Where:\n" +
                "    name - the name of the query \n\n" +
                "Example:\n" +
                "    DescribeTable SampleQuery\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String name = args[0];

        // Build an Athena client
       AthenaClient athenaClient = AthenaClient.builder()
                .region(Region.US_WEST_2)
                .build();

        createNamedQuery(athenaClient, name);
    }

    //snippet-start:[athena.java2.CreateNamedQueryExample.main]
    public static void createNamedQuery(AthenaClient athenaClient, String name) {

        try {
            // Create the named query request.
            CreateNamedQueryRequest createNamedQueryRequest = CreateNamedQueryRequest.builder()
                    .database(ExampleConstants.ATHENA_DEFAULT_DATABASE)
                    .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                    .description("Sample Description")
                    .name(name)
                    .build();

            // Call Athena to create the named query. If it fails, an exception is thrown.
            CreateNamedQueryResponse createNamedQueryResult = athenaClient.createNamedQuery(createNamedQueryRequest);
            System.out.println("Done");
        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
        //snippet-end:[athena.java2.CreateNamedQueryExample.main]
    }
}
//snippet-end:[athena.java.CreateNamedQueryExample.complete]
//snippet-end:[athena.java2.CreateNamedQueryExample.complete]
