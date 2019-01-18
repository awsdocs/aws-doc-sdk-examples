// snippet-sourcedescription:[Program.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.UpdateItem_C] 

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
            // Get an AmazonDynamoDBClient for the local database
            AmazonDynamoDBClient client = GetLocalClient();
            if (client == null)
            {
                PauseForDebugWindow();
                return;
            }

            // Create an UpdateItemRequest to modify two existing nested attributes
            // and add a new one
            UpdateItemRequest updateRequest = new UpdateItemRequest()
            {
                TableName = "Movies",
                Key = new Dictionary<string, AttributeValue>
            {
                { "year",  new AttributeValue {
                      N = "2015"
                  } },
                { "title", new AttributeValue {
                      S = "The Big New Movie"
                  } }
            },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
            {
                { ":n", new AttributeValue {
                      N = "3"
                  } }
            },
                ConditionExpression = "size(info.actors) > :n",
                UpdateExpression = "REMOVE info.actors",
                ReturnValues = "UPDATED_NEW"
            };

            // Use AmazonDynamoDBClient.UpdateItem to update the specified attributes
            UpdateItemResponse uir = null;
            try
            {
                uir = client.UpdateItem(updateRequest);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\nError: UpdateItem failed, because:\n   " + ex.Message);
                if (uir != null)
                    Console.WriteLine("    Status code was " + uir.HttpStatusCode.ToString());
                PauseForDebugWindow();
                return;
            }
            if (uir.HttpStatusCode != System.Net.HttpStatusCode.OK)
            {
                PauseForDebugWindow();
                return;
            }

            // Get the item from the table and display it to validate that the update succeeded
            DisplayMovieItem(client, "2015", "The Big New Movie");
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

        public static void DisplayMovieItem(AmazonDynamoDBClient client, string year, string title)
        {
            // Create Primitives for the HASH and RANGE portions of the primary key
            Primitive hash = new Primitive(year, true);
            Primitive range = new Primitive(title, false);

            Table table = null;
            try
            {
                table = Table.LoadTable(client, "Movies");
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: failed to load the 'Movies' table; " + ex.Message);
                return;
            }
            Document document = table.GetItem(hash, range);
            Console.WriteLine("\n The movie record looks like this: \n" + document.ToJsonPretty());
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
// snippet-end:[dynamodb.dotNET.CodeExample.UpdateItem_C]