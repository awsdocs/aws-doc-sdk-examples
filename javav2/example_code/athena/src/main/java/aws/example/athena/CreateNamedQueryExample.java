//snippet-sourcedescription:[CreateNamedQueryExample.java demonstrates how to create a named query.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[athena.java2.CreateNamedQueryExample.complete]
//snippet-start:[athena.java.CreateNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.CreateNamedQueryExample.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
//snippet-end:[athena.java2.CreateNamedQueryExample.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateNamedQueryExample {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    <name>\n\n" +
                "Where:\n" +
                "    name - the name of the Amazon Athena query. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        AthenaClient athenaClient = AthenaClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createNamedQuery(athenaClient, name);
        athenaClient.close();
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

            athenaClient.createNamedQuery(createNamedQueryRequest);
            System.out.println("Done");

        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    //snippet-end:[athena.java2.CreateNamedQueryExample.main]
}
//snippet-end:[athena.java.CreateNamedQueryExample.complete]
//snippet-end:[athena.java2.CreateNamedQueryExample.complete]