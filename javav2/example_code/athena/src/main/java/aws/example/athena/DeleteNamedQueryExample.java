//snippet-sourcedescription:[DeleteNamedQueryExample.java demonstrates how to delete a named query by using the named query ID.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/24/2020]
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

//snippet-start:[athena.java2.DeleteNamedQueryExample.complete]
//snippet-start:[athena.java.DeleteNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.DeleteNamedQueryExample.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
//snippet-end:[athena.java2.DeleteNamedQueryExample.import]

/**
 * DeleteNamedQueryExample
 * -------------------------------------
 * This code shows how to delete a named query by using the named query ID.
 */
public class DeleteNamedQueryExample {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteNamedQueryExample <name>\n\n" +
                "Where:\n" +
                "    name - the name of the query \n\n" +
                "Example:\n" +
                "    DeleteNamedQueryExample SampleQuery\n";

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
        String sampleNamedQueryId = getNamedQueryId(athenaClient, name);
        deleteQueryName(athenaClient, sampleNamedQueryId);
         }

    //snippet-start:[athena.java2.DeleteNamedQueryExample.main]
   public static void deleteQueryName(AthenaClient athenaClient, String sampleNamedQueryId) {

       try {
            // Create the delete named query request
            DeleteNamedQueryRequest deleteNamedQueryRequest = DeleteNamedQueryRequest.builder()
               .namedQueryId(sampleNamedQueryId).build();

            // Delete the named query
            DeleteNamedQueryResponse deleteNamedQueryResponse = athenaClient.deleteNamedQuery(deleteNamedQueryRequest);
       } catch (AthenaException e) {
           e.printStackTrace();
           System.exit(1);
       }
     }

    public static String getNamedQueryId(AthenaClient athenaClient, String name) {
        try {
            // Create the NameQuery Request.
            CreateNamedQueryRequest createNamedQueryRequest = CreateNamedQueryRequest.builder()
                .database(ExampleConstants.ATHENA_DEFAULT_DATABASE)
                .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                .name(name)
                .description("Sample Description").build();

            // Create the named query. If it fails, an exception is thrown.
            CreateNamedQueryResponse createNamedQueryResponse = athenaClient.createNamedQuery(createNamedQueryRequest);
            return createNamedQueryResponse.namedQueryId();

    } catch (AthenaException e) {
        e.printStackTrace();
        System.exit(1);
    }
        return null;
    }
    //snippet-end:[athena.java2.DeleteNamedQueryExample.main]
}

//snippet-end:[athena.java.DeleteNamedQueryExample.complete]
//snippet-end:[athena.java2.DeleteNamedQueryExample.complete]
