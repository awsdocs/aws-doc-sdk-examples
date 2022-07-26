//snippet-sourcedescription:[DeleteNamedQueryExample.java demonstrates how to delete a named query by using the named query Id value.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Athena]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


//snippet-start:[athena.java2.DeleteNamedQueryExample.complete]
//snippet-start:[athena.java.DeleteNamedQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.DeleteNamedQueryExample.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.DeleteNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryRequest;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
//snippet-end:[athena.java2.DeleteNamedQueryExample.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteNamedQueryExample {

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
