//snippet-sourcedescription:[ListQueryExecutionsExample.java demonstrates how to obtain a list of query execution Id values.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Athena]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[athena.java2.ListNamedQueryExecutionsExample.complete]
//snippet-start:[athena.java.ListNamedQueryExecutionsExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.ListNamedQueryExecutionsExample.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsRequest;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsResponse;
import software.amazon.awssdk.services.athena.paginators.ListQueryExecutionsIterable;
import java.util.List;
//snippet-end:[athena.java2.ListNamedQueryExecutionsExample.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListQueryExecutionsExample {

    public static void main(String[] args) {

        AthenaClient athenaClient = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        listQueryIds(athenaClient);
        athenaClient.close();
    }

    //snippet-start:[athena.java2.ListNamedQueryExecutionsExample.main]
    public static void listQueryIds(AthenaClient athenaClient) {

        try {
            ListQueryExecutionsRequest listQueryExecutionsRequest = ListQueryExecutionsRequest.builder().build();
            ListQueryExecutionsIterable listQueryExecutionResponses = athenaClient.listQueryExecutionsPaginator(listQueryExecutionsRequest);
            for (ListQueryExecutionsResponse listQueryExecutionResponse : listQueryExecutionResponses) {
                List<String> queryExecutionIds = listQueryExecutionResponse.queryExecutionIds();
                System.out.println("\n" +queryExecutionIds);
            }

        } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    //snippet-end:[athena.java2.ListNamedQueryExecutionsExample.main]
}
//snippet-end:[athena.java.ListNamedQueryExecutionsExample.complete]
//snippet-end:[athena.java2.ListNamedQueryExecutionsExample.complete]