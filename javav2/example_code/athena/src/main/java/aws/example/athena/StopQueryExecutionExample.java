//snippet-sourcedescription:[StopQueryExecutionExample.java demonstrates how to stop a query and check its status.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon - aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[athena.java2.StopQueryExecutionExample.complete]
//snippet-start:[athena.java.StopQueryExecutionExample.complete]
package aws.example.athena;

//snippet-start:[athena.java2.StopQueryExecutionExample.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.StopQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StopQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.AthenaException;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
//snippet-end:[athena.java2.StopQueryExecutionExample.import]

public class StopQueryExecutionExample {
    public static void main(String[] args) throws Exception {

        AthenaClient athenaClient = AthenaClient.builder()
                .region(Region.US_WEST_2)
                .build();

        String sampleQueryExecutionId = submitAthenaQuery(athenaClient);
        stopAthenaQuery(athenaClient, sampleQueryExecutionId);
        athenaClient.close();
    }

    //snippet-start:[athena.java2.StopQueryExecutionExample.main]
    public static void stopAthenaQuery(AthenaClient athenaClient, String sampleQueryExecutionId){

       try {
            // Submit the stop query request
            StopQueryExecutionRequest stopQueryExecutionRequest = StopQueryExecutionRequest.builder()
                    .queryExecutionId(sampleQueryExecutionId)
                    .build();

            StopQueryExecutionResponse stopQueryExecutionResponse = athenaClient.stopQueryExecution(stopQueryExecutionRequest);

            // Ensure that the query was stopped
            GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                    .queryExecutionId(sampleQueryExecutionId)
                    .build();

            GetQueryExecutionResponse getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            if (getQueryExecutionResponse.queryExecution()
                .status()
                .state()
                .equals(QueryExecutionState.CANCELLED)) {

                System.out.println("The Amazon Athena query has been cancelled!");
            }

         } catch (AthenaException e) {
            e.printStackTrace();
            System.exit(1);
         }
    }

    /**
     * Submits an example query and returns a query execution ID of a running query to stop.
     */
    public static String submitAthenaQuery(AthenaClient athenaClient) {

        try {
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

        } catch (AthenaException e) {
            e.printStackTrace();
         System.exit(1);
        }
        return null;

    }
    //snippet-end:[athena.java2.StopQueryExecutionExample.main]
}

//snippet-end:[athena.java.StopQueryExecutionExample.complete]
//snippet-end:[athena.java2.StopQueryExecutionExample.complete]