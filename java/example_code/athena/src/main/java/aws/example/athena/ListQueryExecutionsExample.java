//snippet-sourcedescription:[ListQueryExecutionsExample.java demonstrates how to obtain a list of query execution IDs.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.ListQueryExecutionsRequest;
import com.amazonaws.services.athena.model.ListQueryExecutionsResult;

import java.util.List;

/**
* ListQueryExecutionsExample
* -------------------------------------
* This code shows how to obtain a list of query execution IDs.
*/
public class ListQueryExecutionsExample
{
  public static void main(String[] args) throws Exception
  {
      // Build an Athena client
      AthenaClientFactory factory = new AthenaClientFactory();
      AmazonAthena athenaClient = factory.createClient();

      // Build the request
      ListQueryExecutionsRequest listQueryExecutionsRequest = new ListQueryExecutionsRequest();

      // Get the list results.
      ListQueryExecutionsResult listQueryExecutionsResult = athenaClient.listQueryExecutions(listQueryExecutionsRequest);

      // Process the results.
      boolean hasMoreResults = true;
      while (hasMoreResults) {
          List<String> queryExecutionIds = listQueryExecutionsResult.getQueryExecutionIds();
          // process queryExecutionIds.

          System.out.println(queryExecutionIds);

          //If nextToken is not null, then there are more results. Get the next page of results.
          if (listQueryExecutionsResult.getNextToken() != null) {
              listQueryExecutionsResult = athenaClient.listQueryExecutions(
                      listQueryExecutionsRequest.withNextToken(listQueryExecutionsResult.getNextToken()));
          }
          else {
              hasMoreResults = false;
          }
      }
  }
}
