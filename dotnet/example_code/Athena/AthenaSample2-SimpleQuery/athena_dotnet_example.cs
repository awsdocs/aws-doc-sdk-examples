/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[athena_dotnet_example.cs demonstrates how to query for information from Amazon Athena.]
// snippet-service:[athena]
// snippet-keyword:[csharp]
// snippet-keyword:[Athena]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

// For more information about Amazon Athena, see the user guide and API reference at:
// https://docs.aws.amazon.com/athena

// snippet-start:[athena.csharp.athena_dotnet_example.complete]
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Collections.Generic;
using Amazon;
using Amazon.Athena;
using Amazon.Athena.Model;

namespace athena_api
{
    class Program
    {
        private const String ATHENA_TEMP_PATH = "s3://my-bucket/athena-temp/";
        private const String ATHENA_DB = "default";

        static void Main(string[] args)
        {
            using (var client = new AmazonAthenaClient(Amazon.RegionEndpoint.USEast1)) {
                QueryExecutionContext qContext = new QueryExecutionContext();
                qContext.Database = ATHENA_DB;
                ResultConfiguration resConf = new ResultConfiguration();
                resConf.OutputLocation = ATHENA_TEMP_PATH;

                Console.WriteLine("Created Athena Client");
                run(client, qContext, resConf).Wait();
            }
        }

        async static Task run(IAmazonAthena client, QueryExecutionContext qContext, ResultConfiguration resConf)
        {
            /* Execute a simple query on a table */
            StartQueryExecutionRequest qReq = new StartQueryExecutionRequest() {
                QueryString = "SELECT * FROM cloudtrail_logs limit 10;",
                QueryExecutionContext = qContext,
                ResultConfiguration = resConf
            };

            try {
                /* Executes the query in an async manner */
                StartQueryExecutionResponse qRes = await client.StartQueryExecutionAsync(qReq);
                /* Call internal method to parse the results and return a list of key/value dictionaries */
                List<Dictionary<String, String>> items = await getQueryExecution(client, qRes.QueryExecutionId);
                foreach(var item in items)
                {
                    foreach(KeyValuePair<String, String> pair in item) 
                    {
                        Console.WriteLine("Col: {0}", pair.Key);
                        Console.WriteLine("Val: {0}", pair.Value);
                    }
                }
            }
            catch (InvalidRequestException e) {
                Console.WriteLine("Run Error: {0}", e.Message);
            }
        }
        async static Task<List<Dictionary<String, String>>> getQueryExecution(IAmazonAthena client, String id)
        {
            List<Dictionary<String, String>> items = new List<Dictionary<String, String>>();
            GetQueryExecutionResponse results = null;
            QueryExecution q = null;
            /* Declare query execution request object */
            GetQueryExecutionRequest qReq = new GetQueryExecutionRequest() {
                QueryExecutionId = id
            };
            /* Poll API to determine when the query completed */
            do {
                try {
                    results = await client.GetQueryExecutionAsync(qReq);
                    q = results.QueryExecution;
                    Console.WriteLine("Status: {0}... {1}", q.Status.State, q.Status.StateChangeReason);

                    await Task.Delay(5000); //Wait for 5sec before polling again
                }
                catch (InvalidRequestException e)
                {
                    Console.WriteLine("GetQueryExec Error: {0}", e.Message);
                }
            } while(q.Status.State == "RUNNING" || q.Status.State == "QUEUED");

            Console.WriteLine("Data Scanned for {0}: {1} Bytes", id, q.Statistics.DataScannedInBytes);
            
            /* Declare query results request object */
            GetQueryResultsRequest resReq = new GetQueryResultsRequest() {
                QueryExecutionId = id,
                MaxResults = 10
            };

            GetQueryResultsResponse resResp = null;
            /* Page through results and request additional pages if available */
            do {
                resResp = await client.GetQueryResultsAsync(resReq);
                /* Loop over result set and create a dictionary with column name for key and data for value */
                foreach(Row row in resResp.ResultSet.Rows)
                {
                    Dictionary<String, String> dict = new Dictionary<String, String>();
                    for (var i=0; i < resResp.ResultSet.ResultSetMetadata.ColumnInfo.Count; i++)
                    {
                        dict.Add(resResp.ResultSet.ResultSetMetadata.ColumnInfo[i].Name, row.Data[i].VarCharValue);
                    }
                    items.Add(dict);
                }

                if (resResp.NextToken != null) {
                    resReq.NextToken = resResp.NextToken;
                }
            } while(resResp.NextToken != null);

            /* Return List of dictionary per row containing column name and value */
            return items;
        }
    }
}

// snippet-end:[athena.csharp.athena_dotnet_example.complete]