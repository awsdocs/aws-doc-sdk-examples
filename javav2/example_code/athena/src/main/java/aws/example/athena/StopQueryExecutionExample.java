//snippet-sourcedescription:[StopQueryExecutionExample.java demonstrates how to stop a query and check its status.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-25]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.StopQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StopQueryExecutionResponse;

/**
* StopQueryExecutionExample
* -------------------------------------
* This code runs an example query, immediately stops the query, and checks the status of the query to
* ensure that it was cancelled.
*/
public class StopQueryExecutionExample
{
  public static void main(String[] args) throws Exception
  {
      // Build an Athena client
      AthenaClientFactory factory = new AthenaClientFactory();
      AthenaClient athenaClient = factory.createClient();

      String sampleQueryExecutionId = submitAthenaQuery(athenaClient);

      // Submit the stop query Request
      StopQueryExecutionRequest stopQueryExecutionRequest = new StopQueryExecutionRequest()
              .withQueryExecutionId(sampleQueryExecutionId);

      StopQueryExecutionResponse stopQueryExecutionResponse = athenaClient.stopQueryExecution(stopQueryExecutionRequest);

      // Ensure that the query was stopped
      GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
              .withQueryExecutionId(sampleQueryExecutionId);

      GetQueryExecutionResponse getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
      if (getQueryExecutionResponse.getQueryExecution().getStatus().getState().equals(QueryExecutionState.CANCELLED)) {
          // Query was cancelled.
          System.out.println("Query has been cancelled");
      }
  }

  /**
   * Submits an example query and returns a query execution ID of a running query to stop.
   */
  public static String submitAthenaQuery(AthenaClient athenaClient)
  {
      QueryExecutionContext queryExecutionContext = new QueryExecutionContext().withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE);

      ResultConfiguration resultConfiguration = new ResultConfiguration()
              .withOutputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET);

      StartQueryExecutionRequest startQueryExecutionRequest = new StartQueryExecutionRequest()
              .withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
              .withQueryExecutionContext(queryExecutionContext)
              .withResultConfiguration(resultConfiguration);

      StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);

      return startQueryExecutionResponse.getQueryExecutionId();
  }
}
