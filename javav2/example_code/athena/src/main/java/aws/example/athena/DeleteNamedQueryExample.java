//snippet-sourcedescription:[DeleteNamedQueryExample.java demonstrates how to delete a named query by using the named query Id value.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


//snippet-start:[athena.java2.DeleteNamedQueryExample.complete]
//snippet-start:[athena.java.DeleteNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.DeleteNamedQueryExample.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
//snippet-end:[athena.java2.DeleteNamedQueryExample.import]

public class DeleteNamedQueryExample {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteNamedQueryExample <name>\n\n" +
                "Where:\n" +
                "    name - the name of the Amazon Athena query. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String name = args[0];
        AthenaClient athenaClient = AthenaClient.builder()
                .region(Region.US_WEST_2)
                .build();

        String sampleNamedQueryId = getNamedQueryId(athenaClient, name);
        deleteQueryName(athenaClient, sampleNamedQueryId);
        athenaClient.close();
    }

    //snippet-start:[athena.java2.DeleteNamedQueryExample.main]
   public static void deleteQueryName(AthenaClient athenaClient, String sampleNamedQueryId) {

       try {
            DeleteNamedQueryRequest deleteNamedQueryRequest = DeleteNamedQueryRequest.builder()
                    .namedQueryId(sampleNamedQueryId)
                    .build();

            athenaClient.deleteNamedQuery(deleteNamedQueryRequest);

       } catch (AthenaException e) {
           e.printStackTrace();
           System.exit(1);
       }
     }

    public static String getNamedQueryId(AthenaClient athenaClient, String name) {
        try {
            CreateNamedQueryRequest createNamedQueryRequest = CreateNamedQueryRequest.builder()
                    .database(ExampleConstants.ATHENA_DEFAULT_DATABASE)
                    .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                    .name(name)
                    .description("Sample description")
                    .build();

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
