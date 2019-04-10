//snippet-sourcedescription:[ListQueryExecutionsExample.java demonstrates how to obtain a list of query execution IDs.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-25]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsRequest;
import software.amazon.awssdk.services.athena.model.ListQueryExecutionsResponse;

import java.util.List;

/**
 * ListQueryExecutionsExample
 * -------------------------------------
 * This code shows how to obtain a list of query execution IDs.
 */
public class ListQueryExecutionsExample {
    public static void main(String[] args) throws Exception {
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        // Build the request
        ListQueryExecutionsRequest listQueryExecutionsRequest = ListQueryExecutionsRequest.builder().build();

        // Get the list results.
        ListQueryExecutionsResponse listQueryExecutionResponse = athenaClient.listQueryExecutions(listQueryExecutionsRequest);

        // Process the results.
        boolean hasMoreResults = true;
        while (hasMoreResults) {
            List<String> queryExecutionIds = listQueryExecutionResponse.queryExecutionIds();
            // process queryExecutionIds.

            System.out.println(queryExecutionIds);

            //If nextToken is not null, then there are more results. Get the next page of results.
            if (listQueryExecutionResponse.getNextToken() != null) {
                listQueryExecutionResponse = athenaClient.listQueryExecutions(
                        listQueryExecutionsRequest.withNextToken(listQueryExecutionResponse.getNextToken()));
            } else {
                hasMoreResults = false;
            }
        }
    }
}
