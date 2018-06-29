using System;
using System.Collections.Generic;
using System.Threading;
using Amazon;
using Amazon.Athena.Model;
using Amazon.Athena;

namespace AthenaSamples1
{
    /**
    * StartQueryExample
    * -------------------------------------
    * This code shows how to submit a query to Athena for execution, wait till results
    * are available, and then process the results.
    */
    class StartQueryExample
    {
        public static void Example()
        {
            // Create an Amazon Athena client
            var athenaConfig = new AmazonAthenaConfig
            {
                RegionEndpoint = RegionEndpoint.USEast1,
                Timeout = TimeSpan.FromMilliseconds(ExampleConstants.CLIENT_EXECUTION_TIMEOUT)
            };
            var athenaClient = new AmazonAthenaClient(config: athenaConfig);

            String queryExecutionId = submitAthenaQuery(athenaClient);

            waitForQueryToComplete(athenaClient, queryExecutionId);

            processResultRows(athenaClient, queryExecutionId);
        }

        /**
         * Submits a sample query to Athena and returns the execution ID of the query.
         */
        private static String submitAthenaQuery(AmazonAthenaClient athenaClient)
        {
            // The QueryExecutionContext allows us to set the Database.
            var queryExecutionContext = new QueryExecutionContext()
            {
                Database = ExampleConstants.ATHENA_DEFAULT_DATABASE
            };

            // The result configuration specifies where the results of the query should go in S3 and encryption options
            var resultConfiguration = new ResultConfiguration()
            {
                // You can provide encryption options for the output that is written.
                // EncryptionConfiguration = encryptionConfiguration
                OutputLocation = ExampleConstants.ATHENA_OUTPUT_BUCKET
            };

            // Create the StartQueryExecutionRequest to send to Athena which will start the query.
            var startQueryExecutionRequest = new StartQueryExecutionRequest()
            {
                QueryString = ExampleConstants.ATHENA_SAMPLE_QUERY,
                QueryExecutionContext = queryExecutionContext,
                ResultConfiguration = resultConfiguration
            };

            var startQueryExecutionResponse = athenaClient.StartQueryExecution(startQueryExecutionRequest);
            return startQueryExecutionResponse.QueryExecutionId;
        }

        /**
         * Wait for an Athena query to complete, fail or to be cancelled. This is done by polling Athena over an
         * interval of time. If a query fails or is cancelled, then it will throw an exception.
         */
        private static void waitForQueryToComplete(AmazonAthenaClient athenaClient, String queryExecutionId)
        {
            var getQueryExecutionRequest = new GetQueryExecutionRequest()
            {
                QueryExecutionId = queryExecutionId
            };

            GetQueryExecutionResponse getQueryExecutionResponse = null;
            bool isQueryStillRunning = true;

            while (isQueryStillRunning)
            {
                getQueryExecutionResponse = athenaClient.GetQueryExecution(getQueryExecutionRequest);
                var queryState = getQueryExecutionResponse.QueryExecution.Status.State;
                if (queryState == QueryExecutionState.FAILED)
                    throw new Exception("Query Failed to run with Error Message: " + getQueryExecutionResponse.QueryExecution.Status.StateChangeReason);
                else if (queryState == QueryExecutionState.CANCELLED)
                    throw new Exception("Query was cancelled.");
                else if (queryState == QueryExecutionState.SUCCEEDED)
                    isQueryStillRunning = false;
                else
                {
                    // Sleep an amount of time before retrying again.
                    Thread.Sleep(TimeSpan.FromMilliseconds(ExampleConstants.SLEEP_AMOUNT_IN_MS));
                }
                Console.WriteLine("Current Status is: " + queryState);
            }
        }

        /**
         * This code calls Athena and retrieves the results of a query.
         * The query must be in a completed state before the results can be retrieved and
         * paginated. The first row of results are the column headers.
         */
        private static void processResultRows(AmazonAthenaClient athenaClient, String queryExecutionId)
        {
            GetQueryResultsRequest getQueryResultsRequest = new GetQueryResultsRequest()
            {
                // Max Results can be set but if its not set, it will choose the maximum page size
                // As of the writing of this code, the maximum value is 1000
                // MaxResults = 1000
                QueryExecutionId = queryExecutionId
            };

            var getQueryResultsResponse = athenaClient.GetQueryResults(getQueryResultsRequest);

            while (true)
            {
                var results = getQueryResultsResponse.ResultSet.Rows;
                foreach (Row row in results)
                {
                    // Process the row. The first row of the first page holds the column names.
                    processRow(row, getQueryResultsResponse.ResultSet.ResultSetMetadata.ColumnInfo);
                }
                // If nextToken is null, there are no more pages to read. Break out of the loop.
                if (String.IsNullOrEmpty(getQueryResultsResponse.NextToken))
                    break;
                getQueryResultsRequest.NextToken = getQueryResultsResponse.NextToken;
                getQueryResultsResponse = athenaClient.GetQueryResults(getQueryResultsRequest);
            }
        }

        private static void processRow(Row row, List<ColumnInfo> columnInfoList)
        {
            for (int i = 0; i < columnInfoList.Count; ++i)
            {
                switch (columnInfoList[i].Type)
                {
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
                        throw new Exception("Unexpected Type is not expected" + columnInfoList[i].Type);
                }
            }
        }
    }
}