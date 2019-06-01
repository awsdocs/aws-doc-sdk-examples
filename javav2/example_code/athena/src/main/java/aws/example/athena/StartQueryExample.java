//snippet-sourcedescription:[StartQueryExample.java demonstrates how to submit a query to Athena for execution, wait till results are available, and then process the results.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
//snippet-start:[athena.java2.StartQueryExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.StartQueryExample.import]
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.util.List;
//snippet-end:[athena.java2.StartQueryExample.import]

/**
 * StartQueryExample
 * -------------------------------------
 * This code shows how to submit a query to Athena for execution, wait till results
 * are available, and then process the results.
 */
public class StartQueryExample {
    //snippet-start:[athena.java2.StartQueryExample.main]
    public static void main(String[] args) throws InterruptedException {
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
    private static String submitAthenaQuery(AthenaClient athenaClient) {
        // The QueryExecutionContext allows us to set the Database.
        QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                .database(ExampleConstants.ATHENA_DEFAULT_DATABASE).build();

        // The result configuration specifies where the results of the query should go in S3 and encryption options
        ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                // You can provide encryption options for the output that is written.
                // .withEncryptionConfiguration(encryptionConfiguration)
                .outputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET).build();

        // Create the StartQueryExecutionRequest to send to Athena which will start the query.
        StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                .queryExecutionContext(queryExecutionContext)
                .resultConfiguration(resultConfiguration).build();

        StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);
        return startQueryExecutionResponse.queryExecutionId();
    }

    /**
     * Wait for an Athena query to complete, fail or to be cancelled. This is done by polling Athena over an
     * interval of time. If a query fails or is cancelled, then it will throw an exception.
     */

    private static void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId) throws InterruptedException {
        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId).build();

        GetQueryExecutionResponse getQueryExecutionResponse;
        boolean isQueryStillRunning = true;
        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            String queryState = getQueryExecutionResponse.queryExecution().status().state().toString();
            if (queryState.equals(QueryExecutionState.FAILED.toString())) {
                throw new RuntimeException("Query Failed to run with Error Message: " + getQueryExecutionResponse
                        .queryExecution().status().stateChangeReason());
            } else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
                throw new RuntimeException("Query was cancelled.");
            } else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
                isQueryStillRunning = false;
            } else {
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
    private static void processResultRows(AthenaClient athenaClient, String queryExecutionId) {
        GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                // Max Results can be set but if its not set,
                // it will choose the maximum page size
                // As of the writing of this code, the maximum value is 1000
                // .withMaxResults(1000)
                .queryExecutionId(queryExecutionId).build();

        GetQueryResultsIterable getQueryResultsResults = athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

        for (GetQueryResultsResponse Resultresult : getQueryResultsResults) {
            List<ColumnInfo> columnInfoList = Resultresult.resultSet().resultSetMetadata().columnInfo();
            List<Row> results = Resultresult.resultSet().rows();
            processRow(results, columnInfoList);
        }
    }

    private static void processRow(List<Row> row, List<ColumnInfo> columnInfoList) {
        for (ColumnInfo columnInfo : columnInfoList) {
            switch (columnInfo.type()) {
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
                    throw new RuntimeException("Unexpected Type is not expected" + columnInfo.type());
            }
        }
    }
    //snippet-end:[athena.java2.StartQueryExample.main]
}
//snippet-end:[athena.java2.StartQueryExample.complete]