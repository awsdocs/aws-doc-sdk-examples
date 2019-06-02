//snippet-sourcedescription:[StopQueryExecutionExample.java demonstrates how to stop a query and check its status.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder]
//snippet-start:[athena.java2.StopQueryExecutionExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.StopQueryExecutionExample.import]
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
//snippet-end:[athena.java2.StopQueryExecutionExample.import]

/**
 * StopQueryExecutionExample
 * -------------------------------------
 * This code runs an example query, immediately stops the query, and checks the status of the query to
 * ensure that it was cancelled.
 */
public class StopQueryExecutionExample {
    public static void main(String[] args) throws Exception {
        //snippet-start:[athena.java2.StopQueryExecutionExample.main]
        // Build an Athena client
        AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        String sampleQueryExecutionId = submitAthenaQuery(athenaClient);

        // Submit the stop query Request
        StopQueryExecutionRequest stopQueryExecutionRequest = StopQueryExecutionRequest.builder()
                .queryExecutionId(sampleQueryExecutionId).build();

        StopQueryExecutionResponse stopQueryExecutionResponse = athenaClient.stopQueryExecution(stopQueryExecutionRequest);

        // Ensure that the query was stopped
        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(sampleQueryExecutionId).build();

        GetQueryExecutionResponse getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
        if (getQueryExecutionResponse.queryExecution()
                .status()
                .state()
                .equals(QueryExecutionState.CANCELLED)) {
            // Query was cancelled.
            System.out.println("Query has been cancelled");
        }
    }

    /**
     * Submits an example query and returns a query execution ID of a running query to stop.
     */
    public static String submitAthenaQuery(AthenaClient athenaClient) {
        QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                .database(ExampleConstants.ATHENA_DEFAULT_DATABASE).build();

        ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                .outputLocation(ExampleConstants.ATHENA_OUTPUT_BUCKET).build();

        StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                .queryExecutionContext(queryExecutionContext)
                .queryString(ExampleConstants.ATHENA_SAMPLE_QUERY)
                .resultConfiguration(resultConfiguration).build();

        StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);

        return startQueryExecutionResponse.queryExecutionId();

    }
    //snippet-end:[athena.java2.StopQueryExecutionExample.main]
}
//snippet-end:[athena.java2.StopQueryExecutionExample.complete]