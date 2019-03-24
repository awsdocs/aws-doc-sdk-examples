//snippet-sourcedescription:[StartQueryExample.java demonstrates how to submit a query to Athena for execution, wait till results are available, and then process the results.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-25]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.ColumnInfo;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;

import java.util.List;

/**
* StartQueryExample
* -------------------------------------
* This code shows how to submit a query to Athena for execution, wait till results
* are available, and then process the results.
*/
public class StartQueryExample
{
  public static void main(String[] args) throws InterruptedException
  {
      // Build an AthenaClient client
      AthenaClientFactory factory = new AthenaClientFactory();
      AthenaClient athenaClient = factory.createClient();

      String queryExecutionId = submitAthenaQuery(athenaClient);

      waitForQueryToComplete(athenaClient, queryExecutionId);

      processResultRows(athenaClient, queryExecutionId);
  }

  /**
   * Submits a sample query to Athena and returns the execution ID of the query.
   */
  private static String submitAthenaQuery(AthenaClient athenaClient)
  {
      // The QueryExecutionContext allows us to set the Database.
      QueryExecutionContext queryExecutionContext = new QueryExecutionContext().withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE);

      // The result configuration specifies where the results of the query should go in S3 and encryption options
      ResultConfiguration resultConfiguration = new ResultConfiguration()
              // You can provide encryption options for the output that is written.
              // .withEncryptionConfiguration(encryptionConfiguration)
              .withOutputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET);

      // Create the StartQueryExecutionRequest to send to Athena which will start the query.
      StartQueryExecutionRequest startQueryExecutionRequest = new StartQueryExecutionRequest()
              .withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
              .withQueryExecutionContext(queryExecutionContext)
              .withResultConfiguration(resultConfiguration);

      StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);
      return startQueryExecutionResponse.queryExecutionId();
  }

  /**
   * Wait for an Athena query to complete, fail or to be cancelled. This is done by polling Athena over an
   * interval of time. If a query fails or is cancelled, then it will throw an exception.
   */

       private static void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId) throws InterruptedException
  {
      GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
              .withQueryExecutionId(queryExecutionId);

      GetQueryExecutionResponse getQueryExecutionResponse = null;
      boolean isQueryStillRunning = true;
      while (isQueryStillRunning) {
          getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
          String queryState = getQueryExecutionResponse.getQueryExecution().getStatus().getState();
          if (queryState.equals(QueryExecutionState.FAILED.toString())) {
              throw new RuntimeException("Query Failed to run with Error Message: " + getQueryExecutionResponse.getQueryExecution().getStatus().getStateChangeReason());
          }
          else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
              throw new RuntimeException("Query was cancelled.");
          }
          else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
              isQueryStillRunning = false;
          }
          else {
              // Sleep an amount of time before retrying again.
              Thread.sleep(ExampleConstants.SLEEP_AMOUNT_IN_MS);
          }
          System.out.println("Current Status is: " + queryState);
      }
  }

  /**
   * This code calls Athena and retrieves the results of a query.
   * The query must be in a completed state before the results can be retrieved and
   * paginated. The first row of results are the column headers.
   */
  private static void processResultRows(AthenaClient athenaClient, String queryExecutionId)
  {
      GetQueryResultsRequest GetQueryResultsRequest = new GetQueryResultsRequest()
              // Max Results can be set but if its not set,
              // it will choose the maximum page size
              // As of the writing of this code, the maximum value is 1000
              // .withMaxResults(1000)
              .withQueryExecutionId(queryExecutionId);

      GetQueryResultsResponse GetQueryResultsResult = athenaClient.getQueryResults(GetQueryResultsRequest);
      List<ColumnInfo> columnInfoList = GetQueryResultsResult.getResultSet().getResultSetMetadata().getColumnInfo();

      while (true) {
          List<Row> results = GetQueryResultsResult.getResultSet().getRows();
          for (Row row : results) {
              // Process the row. The first row of the first page holds the column names.
              processRow(row, columnInfoList);
          }
          // If nextToken is null, there are no more pages to read. Break out of the loop.
          if (GetQueryResultsResult.getNextToken() == null) {
              break;
          }
          GetQueryResultsResult = athenaClient.getQueryResults(
                  GetQueryResultsRequest.withNextToken(GetQueryResultsResult.getNextToken()));
      }
  }

  private static void processRow(Row row, List<ColumnInfo> columnInfoList)
  {
      for (int i = 0; i < columnInfoList.size(); ++i) {
          switch (columnInfoList.get(i).getType()) {
              case "varchar":
                  // Convert and Process as String
                  break;
              case "tinyint":
                  // Convert and Process as tinyint
                  break;
              case "smallint":
                  // Convert and Process as smallint
                  break;
              case "integer":
                  // Convert and Process as integer
                  break;
              case "bigint":
                  // Convert and Process as bigint
                  break;
              case "double":
                  // Convert and Process as double
                  break;
              case "boolean":
                  // Convert and Process as boolean
                  break;
              case "date":
                  // Convert and Process as date
                  break;
              case "timestamp":
                  // Convert and Process as timestamp
                  break;
              default:
                  throw new RuntimeException("Unexpected Type is not expected" + columnInfoList.get(i).getType());
          }
      }
  }
}
