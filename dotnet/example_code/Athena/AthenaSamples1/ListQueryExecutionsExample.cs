//snippet-sourceauthor: [ebattalio]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

﻿using System;
using Amazon;
using Amazon.Athena;
using Amazon.Athena.Model;

namespace AthenaSamples1
{
    /**
    * ListQueryExecutionsExample
    * -------------------------------------
    * This code shows how to obtain a list of query execution IDs.
    */
    class ListQueryExecutionsExample
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

            // Build the request
            var ListQueryExecutionsRequest = new ListQueryExecutionsRequest();

            // Get the list results.
            var ListQueryExecutionsResponse = athenaClient.ListQueryExecutions(ListQueryExecutionsRequest);

            // Process the results.
            bool hasMoreResults = true;

            while (hasMoreResults)
            {
                var queryExecutionIds = ListQueryExecutionsResponse.QueryExecutionIds;
                // process query execution IDs

                // If nextToken is not null,  there are more results. Get the next page of results.
                if (!String.IsNullOrEmpty(ListQueryExecutionsResponse.NextToken))
                {
                    ListQueryExecutionsRequest.NextToken = ListQueryExecutionsResponse.NextToken;
                    ListQueryExecutionsResponse = athenaClient.ListQueryExecutions(ListQueryExecutionsRequest);
                }
                else
                    hasMoreResults = false;
            }
        }
    }
}
