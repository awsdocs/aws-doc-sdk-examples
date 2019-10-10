// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.Query] 

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
        static string commaSep = ", ";
        static string movieFormatString = "    \"{0}\", lead actor: {1}, genres: {2}";

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
             *  4.1.1:  Call Table.Query to initiate a query for all movies with
             *          year == 1985, using an empty filter expression.
             *-----------------------------------------------------------------------*/
            Search search;
            try
            {
                search = table.Query(1985, new Expression());
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: 1985 query failed because: " + ex.Message);
                PauseForDebugWindow();
                return;
            }

            // Display the titles of the movies returned by this query
            List<Document> docList = new List<Document>();
            Console.WriteLine("\n All movies released in 1985:" +
                       "\n-----------------------------------------------");
            do
            {
                try { docList = search.GetNextSet(); }
                catch (Exception ex)
                {
                    Console.WriteLine("\n Error: Search.GetNextStep failed because: " + ex.Message);
                    break;
                }
                foreach (var doc in docList)
                    Console.WriteLine("    " + doc["title"]);
            } while (!search.IsDone);


            /*-----------------------------------------------------------------------
             *  4.1.2a:  Call Table.Query to initiate a query for all movies where
             *           year equals 1992 AND title is between "B" and "Hzzz",
             *           returning the lead actor and genres of each.
             *-----------------------------------------------------------------------*/
            Primitive y_1992 = new Primitive("1992", true);
            QueryOperationConfig config = new QueryOperationConfig();
            config.Filter = new QueryFilter();
            config.Filter.AddCondition("year", QueryOperator.Equal, new DynamoDBEntry[] { 1992 });
            config.Filter.AddCondition("title", QueryOperator.Between, new DynamoDBEntry[] { "B", "Hzz" });
            config.AttributesToGet = new List<string> { "title", "info" };
            config.Select = SelectValues.SpecificAttributes;

            try
            {
                search = table.Query(config);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: 1992 query failed because: " + ex.Message);
                PauseForDebugWindow();
                return;
            }

            // Display the movie information returned by this query
            Console.WriteLine("\n\n Movies released in 1992 with titles between \"B\" and \"Hzz\" (Document Model):" +
                       "\n-----------------------------------------------------------------------------");
            docList = new List<Document>();
            Document infoDoc;
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
                               infoDoc["actors"].AsArrayOfString()[0],
                               string.Join(commaSep, infoDoc["genres"].AsArrayOfString()));
                }
            } while (!search.IsDone);


            /*-----------------------------------------------------------------------
             *  4.1.2b:  Call AmazonDynamoDBClient.Query to initiate a query for all
             *           movies where year equals 1992 AND title is between M and Tzz,
             *           returning the genres and the lead actor of each.
             *-----------------------------------------------------------------------*/
            QueryRequest qRequest = new QueryRequest
            {
                TableName = "Movies",
                ExpressionAttributeNames = new Dictionary<string, string>
            {
                { "#yr", "year" }
            },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
            {
                { ":y_1992",  new AttributeValue {
                      N = "1992"
                  } },
                { ":M",       new AttributeValue {
                      S = "M"
                  } },
                { ":Tzz",     new AttributeValue {
                      S = "Tzz"
                  } }
            },
                KeyConditionExpression = "#yr = :y_1992 and title between :M and :Tzz",
                ProjectionExpression = "title, info.actors[0], info.genres"
            };

            QueryResponse qResponse;
            try
            {
                qResponse = client.Query(qRequest);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: Low-level query failed, because: " + ex.Message);
                PauseForDebugWindow();
                return;
            }

            // Display the movie information returned by this query
            Console.WriteLine("\n\n Movies released in 1992 with titles between \"M\" and \"Tzz\" (low-level):" +
                       "\n-------------------------------------------------------------------------");
            foreach (Dictionary<string, AttributeValue> item in qResponse.Items)
            {
                Dictionary<string, AttributeValue> info = item["info"].M;
                Console.WriteLine(movieFormatString,
                           item["title"].S,
                           info["actors"].L[0].S,
                           GetDdbListAsString(info["genres"].L));
            }
        }

        public static string GetDdbListAsString(List<AttributeValue> strList)
        {
            StringBuilder sb = new StringBuilder();
            string str = null;
            AttributeValue av;
            for (int i = 0; i < strList.Count; i++)
            {
                av = strList[i];
                if (av.S != null)
                    str = av.S;
                else if (av.N != null)
                    str = av.N;
                else if (av.SS != null)
                    str = string.Join(commaSep, av.SS.ToArray());
                else if (av.NS != null)
                    str = string.Join(commaSep, av.NS.ToArray());
                if (str != null)
                {
                    if (i > 0)
                        sb.Append(commaSep);
                    sb.Append(str);
                }
            }
            return (sb.ToString());
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
// snippet-end:[dynamodb.dotNET.CodeExample.Query]