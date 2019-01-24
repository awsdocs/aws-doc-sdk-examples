// snippet-sourcedescription:[Program.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.Scan] 

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
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
    class Program
    {
        static void Main(string[] args)
        {
            // Get an AmazonDynamoDBClient for the local DynamoDB database
            AmazonDynamoDBClient client = GetLocalClient();

            // Get a Table object for the table that you created in Step 1
            Table table = GetTableObject(client, "Movies");
            if (table == null)
            {
                PauseForDebugWindow();
                return;
            }


            /*-----------------------------------------------------------------------
             *  4.2a:  Call Table.Scan to return the movies released in the 1950's,
             *         displaying title, year, lead actor and lead director.
             *-----------------------------------------------------------------------*/
            ScanFilter filter = new ScanFilter();
            filter.AddCondition("year", ScanOperator.Between, new DynamoDBEntry[] { 1950, 1959 });
            ScanOperationConfig config = new ScanOperationConfig
            {
                AttributesToGet = new List<string> { "year, title, info" },
                Filter = filter
            };
            Search search = table.Scan(filter);

            // Display the movie information returned by this query
            Console.WriteLine("\n\n Movies released in the 1950's (Document Model):" +
                       "\n--------------------------------------------------");
            List<Document> docList = new List<Document>();
            Document infoDoc;
            string movieFormatString = "    \"{0}\" ({1})-- lead actor: {2}, lead director: {3}";
            do
            {
                try
                {
                    docList = search.GetNextSet();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("\n Error: Search.GetNextStep failed because: " + ex.Message);
                    break;
                }
                foreach (var doc in docList)
                {
                    infoDoc = doc["info"].AsDocument();
                    Console.WriteLine(movieFormatString,
                               doc["title"],
                               doc["year"],
                               infoDoc["actors"].AsArrayOfString()[0],
                               infoDoc["directors"].AsArrayOfString()[0]);
                }
            } while (!search.IsDone);


            /*-----------------------------------------------------------------------
             *  4.2b:  Call AmazonDynamoDBClient.Scan to return all movies released
             *         in the 1960's, only downloading the title, year, lead
             *         actor and lead director attributes.
             *-----------------------------------------------------------------------*/
            ScanRequest sRequest = new ScanRequest
            {
                TableName = "Movies",
                ExpressionAttributeNames = new Dictionary<string, string>
            {
                { "#yr", "year" }
            },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
            {
                { ":y_a", new AttributeValue {
                      N = "1960"
                  } },
                { ":y_z", new AttributeValue {
                      N = "1969"
                  } },
            },
                FilterExpression = "#yr between :y_a and :y_z",
                ProjectionExpression = "#yr, title, info.actors[0], info.directors[0]"
            };

            ScanResponse sResponse;
            try
            {
                sResponse = client.Scan(sRequest);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: Low-level scan failed, because: " + ex.Message);
                PauseForDebugWindow();
                return;
            }

            // Display the movie information returned by this scan
            Console.WriteLine("\n\n Movies released in the 1960's (low-level):" +
                       "\n-------------------------------------------");
            foreach (Dictionary<string, AttributeValue> item in sResponse.Items)
            {
                Dictionary<string, AttributeValue> info = item["info"].M;
                Console.WriteLine(movieFormatString,
                           item["title"].S,
                           item["year"].N,
                           info["actors"].L[0].S,
                           info["directors"].L[0].S);
            }
        }

        public static AmazonDynamoDBClient GetLocalClient()
        {
            // First, set up a DynamoDB client for DynamoDB Local
            AmazonDynamoDBConfig ddbConfig = new AmazonDynamoDBConfig();
            ddbConfig.ServiceURL = "http://localhost:8000";
            AmazonDynamoDBClient client;
            try
            {
                client = new AmazonDynamoDBClient(ddbConfig);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: failed to create a DynamoDB client; " + ex.Message);
                return (null);
            }
            return (client);
        }


        public static Table GetTableObject(AmazonDynamoDBClient client, string tableName)
        {
            Table table = null;
            try
            {
                table = Table.LoadTable(client, tableName);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: failed to load the 'Movies' table; " + ex.Message);
                return (null);
            }
            return (table);
        }

        public static void PauseForDebugWindow()
        {
            // Keep the console open if in Debug mode...
            Console.Write("\n\n ...Press any key to continue");
            Console.ReadKey();
            Console.WriteLine();
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.Scan]