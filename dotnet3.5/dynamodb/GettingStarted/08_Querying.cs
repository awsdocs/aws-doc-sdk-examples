// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.08_Querying]
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;
using System.Collections.Generic;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        /*--------------------------------------------------------------------------
         *                             SearchListing_async
         *--------------------------------------------------------------------------*/
        public static async Task<bool> SearchListing_async(Search search)
        {
            int i = 0;

            Console.WriteLine("         Here are the movies retrieved:\n" +
                              "         --------------------------------------------------------------------------");
            Task<List<Document>> getNextBatch;
            OperationSucceeded = false;
            OperationFailed = false;

            do
            {
                List<Document> docList;
                try
                {
                    getNextBatch = search.GetNextSetAsync();
                    docList = await getNextBatch;
                }
                catch (Exception ex)
                {
                    Console.WriteLine("        FAILED to get the next batch of movies from Search! Reason:\n          " +
                                       ex.Message);
                    OperationFailed = true;
                    return (false);
                }

                foreach (Document doc in docList)
                {
                    i++;
                    ShowMovieDocShort(doc);
                }
            } while (!search.IsDone);

            Console.WriteLine("     -- Retrieved {0} movies.", i);
            OperationSucceeded = true;
            return (true);
        }

        /*--------------------------------------------------------------------------
         *                             ClientQuerying_async
         *--------------------------------------------------------------------------*/
        public static async Task<bool> ClientQuerying_async(QueryRequest qRequest)
        {
            OperationSucceeded = false;
            OperationFailed = false;

            QueryResponse qResponse;

            try
            {
                Task<QueryResponse> clientQueryTask = Client.QueryAsync(qRequest);
                qResponse = await clientQueryTask;
            }
            catch (Exception ex)
            {
                Console.WriteLine("      The low-level query FAILED, because:\n       {0}.", ex.Message);
                OperationFailed = true;
                return (false);
            }

            Console.WriteLine("     -- The low-level query succeeded, and returned {0} movies!", qResponse.Items.Count);

            if (!Pause())
            {
                OperationFailed = true;
                return (false);
            }

            Console.WriteLine("         Here are the movies retrieved:" +
                               "         --------------------------------------------------------------------------");
            foreach (Dictionary<string, AttributeValue> item in qResponse.Items)
                ShowMovieAttrsShort(item);

            Console.WriteLine("     -- Retrieved {0} movies.", qResponse.Items.Count);
            OperationSucceeded = true;
            return (true);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.08_Querying]