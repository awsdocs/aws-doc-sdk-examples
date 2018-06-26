package aws.example.athena;

import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.GetQueryExecutionRequest;
import com.amazonaws.services.athena.model.GetQueryExecutionResult;
import com.amazonaws.services.athena.model.QueryExecutionContext;
import com.amazonaws.services.athena.model.ResultConfiguration;
import com.amazonaws.services.athena.model.StartQueryExecutionRequest;
import com.amazonaws.services.athena.model.StartQueryExecutionResult;
import com.amazonaws.services.athena.model.StopQueryExecutionRequest;
import com.amazonaws.services.athena.model.StopQueryExecutionResult;

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
      AmazonAthena client = factory.createClient();

      String sampleQueryExecutionId = getExecutionId(client);

      // Submit the stop query Request
      StopQueryExecutionRequest stopQueryExecutionRequest = new StopQueryExecutionRequest()
              .withQueryExecutionId(sampleQueryExecutionId);

      StopQueryExecutionResult stopQueryExecutionResult = client.stopQueryExecution(stopQueryExecutionRequest);

      // Ensure that the query was stopped
      GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
              .withQueryExecutionId(sampleQueryExecutionId);

      GetQueryExecutionResult getQueryExecutionResult = client.getQueryExecution(getQueryExecutionRequest);
      if (getQueryExecutionResult.getQueryExecution().getStatus().getState().equals(ExampleConstants.QUERY_STATE_CANCELLED)) {
          // Query was cancelled.
          System.out.println("Query has been cancelled");
      }
  }

  /**
   * Submits an example query and returns a query execution ID of a running query to stop.
   */
  public static String getExecutionId(AmazonAthena client)
  {
      QueryExecutionContext queryExecutionContext = new QueryExecutionContext().withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE);

      ResultConfiguration resultConfiguration = new ResultConfiguration()
              .withOutputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET);

      StartQueryExecutionRequest startQueryExecutionRequest = new StartQueryExecutionRequest()
              .withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
              .withQueryExecutionContext(queryExecutionContext)
              .withResultConfiguration(resultConfiguration);

      StartQueryExecutionResult startQueryExecutionResult = client.startQueryExecution(startQueryExecutionRequest);

      return startQueryExecutionResult.getQueryExecutionId();
  }
}
