//snippet-sourcedescription:[StartQueryExample.java demonstrates how to submit a query to Amazon Athena for execution, wait until results are available, and then process the results.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-25]
//snippet-sourceauthor:[soo-aws]
package aws.example.athena;

import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.model.ColumnInfo;
import com.amazonaws.services.athena.model.GetQueryExecutionRequest;
import com.amazonaws.services.athena.model.GetQueryExecutionResult;
import com.amazonaws.services.athena.model.GetQueryResultsRequest;
import com.amazonaws.services.athena.model.GetQueryResultsResult;
import com.amazonaws.services.athena.model.QueryExecutionContext;
import com.amazonaws.services.athena.model.QueryExecutionState;
import com.amazonaws.services.athena.model.ResultConfiguration;
import com.amazonaws.services.athena.model.Row;
import com.amazonaws.services.athena.model.StartQueryExecutionRequest;
import com.amazonaws.services.athena.model.StartQueryExecutionResult;

import java.util.List;

/**
* StartQueryExample
* -------------------------------------
* This code shows how to submit a query to Amazon Athena for execution, wait until results
* are available, and then process the results.
*/
public class StartQueryExample
{
  public static void main(String[] args) throws InterruptedException
  {
      // Build an Athena client.
      AthenaClientFactory factory = new AthenaClientFactory();
      AmazonAthena athenaClient = factory.createClient();

      String queryExecutionId = submitAthenaQuery(athenaClient);

      waitForQueryToComplete(athenaClient, queryExecutionId);

      processResultRows(athenaClient, queryExecutionId);
  }

  /**
   * Submits a sample query to Athena and returns the execution ID of the query.
   */
  private static String submitAthenaQuery(AmazonAthena athenaClient)
  {
      // The QueryExecutionContext allows us to set the database.
      QueryExecutionContext queryExecutionContext = new QueryExecutionContext().withDatabase(ExampleConstants.ATHENA_DEFAULT_DATABASE);

      // The result configuration specifies where the results of the query should go in Amazon S3 and encryption options.
      ResultConfiguration resultConfiguration = new ResultConfiguration()
              // You can provide encryption options for the output that is written.
              // .withEncryptionConfiguration(encryptionConfiguration)
              .withOutputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET);

      // Create the StartQueryExecutionRequest to send to Athena, which will start the query.
      StartQueryExecutionRequest startQueryExecutionRequest = new StartQueryExecutionRequest()
              .withQueryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
              .withQueryExecutionContext(queryExecutionContext)
              .withResultConfiguration(resultConfiguration);

      StartQueryExecutionResult startQueryExecutionResult = athenaClient.startQueryExecution(startQueryExecutionRequest);
      return startQueryExecutionResult.getQueryExecutionId();
  }

  /**
   * Wait for an Athena query to complete or fail, or to be canceled. This is done by polling Athena over an
   * interval of time. If a query fails or is canceled, it will throw an exception.
   */

       private static void waitForQueryToComplete(AmazonAthena athenaClient, String queryExecutionId) throws InterruptedException
  {
      GetQueryExecutionRequest getQueryExecutionRequest = new GetQueryExecutionRequest()
              .withQueryExecutionId(queryExecutionId);

      GetQueryExecutionResult getQueryExecutionResult = null;
      boolean isQueryStillRunning = true;
      while (isQueryStillRunning) {
          getQueryExecutionResult = athenaClient.getQueryExecution(getQueryExecutionRequest);
          String queryState = getQueryExecutionResult.getQueryExecution().getStatus().getState();
          if (queryState.equals(QueryExecutionState.FAILED.toString())) {
              throw new RuntimeException("Query failed to run with error message: " + getQueryExecutionResult.getQueryExecution().getStatus().getStateChangeReason());
          }
          else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
              throw new RuntimeException("Query was canceled.");
          }
          else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
              isQueryStillRunning = false;
          }
          else {
              // Sleep an amount of time before retrying.
              Thread.sleep(ExampleConstants.SLEEP_AMOUNT_IN_MS);
          }
          System.out.println("Current state is: " + queryState);
      }
  }

  /**
   * This code calls Athena and retrieves the results of a query.
   * The query must be in a completed state before the results can be retrieved and
   * paginated. The first row of results are the column headers.
   */
  private static void processResultRows(AmazonAthena athenaClient, String queryExecutionId)
  {
      GetQueryResultsRequest getQueryResultsRequest = new GetQueryResultsRequest()
              // MaxResults can be set, but if it is not set,
              // it will choose the maximum page size.
              // As of the writing of this code, the maximum value is 1000
              // .withMaxResults(1000)
              .withQueryExecutionId(queryExecutionId);

      GetQueryResultsResult getQueryResultsResult = athenaClient.getQueryResults(getQueryResultsRequest);
      List<ColumnInfo> columnInfoList = getQueryResultsResult.getResultSet().getResultSetMetadata().getColumnInfo();

      while (true) {
          List<Row> results = getQueryResultsResult.getResultSet().getRows();
          for (Row row : results) {
              // Process the row. The first row of the first page holds the column names.
              processRow(row, columnInfoList);
          }
          // If nextToken is null, there are no more pages to read. Break out of the loop.
          if (getQueryResultsResult.getNextToken() == null) {
              break;
          }
          getQueryResultsResult = athenaClient.getQueryResults(
                  getQueryResultsRequest.withNextToken(getQueryResultsResult.getNextToken()));
      }
  }

  private static void processRow(Row row, List<ColumnInfo> columnInfoList)
  {
      for (int i = 0; i < columnInfoList.size(); ++i) {
          switch (columnInfoList.get(i).getType()) {
              case "varchar":
                  // Convert and process as String.
                  break;
              case "tinyint":
                  // Convert and process as tinyint.
                  break;
              case "smallint":
                  // Convert and process as smallint.
                  break;
              case "integer":
                  // Convert and process as integer.
                  break;
              case "bigint":
                  // Convert and process as bigint.
                  break;
              case "double":
                  // Convert and process as double.
                  break;
              case "boolean":
                  // Convert and process as boolean.
                  break;
              case "date":
                  // Convert and process as date.
                  break;
              case "timestamp":
                  // Convert and process as timestamp.
                  break;
              default:
                  throw new RuntimeException("Unexpected type is not expected" + columnInfoList.get(i).getType());
          }
      }
  }
}
