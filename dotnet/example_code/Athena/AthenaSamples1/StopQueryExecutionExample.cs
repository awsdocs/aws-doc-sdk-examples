 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


﻿using System;
using Amazon;
using Amazon.Athena;
using Amazon.Athena.Model;

namespace AthenaSamples1
{
    /**
    * StopQueryExecutionExample
    * -------------------------------------
    * This code runs an example query, immediately stops the query, and checks the status of the query to
    * ensure that it was cancelled.
    */
    class StopQueryExecutionExample
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

            String sampleQueryExecutionId = submitAthenaQuery(athenaClient);

            // Submit the stop query Request
            var stopQueryExecutionRequest = new StopQueryExecutionRequest()
            {
                QueryExecutionId = sampleQueryExecutionId
            };

            var stopQueryExecutionResponse = athenaClient.StopQueryExecution(stopQueryExecutionRequest);

            // Ensure that the query was stopped
            var getQueryExecutionRequest = new GetQueryExecutionRequest()
            {
                QueryExecutionId = sampleQueryExecutionId
            };
            

            var getQueryExecutionResponse = athenaClient.GetQueryExecution(getQueryExecutionRequest);
            if (getQueryExecutionResponse.QueryExecution.Status.State == QueryExecutionState.CANCELLED)
                Console.WriteLine("Query has been cancelled");
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
    }
}
