// snippet-sourcedescription:[Program.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.8e5970f0-729d-4cf2-b2f0-19913449e805] 

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
            // Get a Table object for the table that you created in Step 1
            Table table = GetTableObject("Movies");
            if (table == null)
            {
                PauseForDebugWindow();
                return;
            }

            // Create a Document representing the movie item to be written to the table
            Document document = new Document();
            document["year"] = 2015;
            document["title"] = "The Big New Movie";
            document["info"] = Document.FromJson("{\"plot\" : \"Nothing happens at all.\",\"rating\" : 0}");

            // Use Table.PutItem to write the document item to the table
            try
            {
                table.PutItem(document);
                Console.WriteLine("\nPutItem succeeded.\n");
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: Table.PutItem failed because: " + ex.Message);
                PauseForDebugWindow();
                return;
            }
        }

        public static Table GetTableObject(string tableName)
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

            // Now, create a Table object for the specified table
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
// snippet-end:[dynamodb.dotNET.CodeExample.8e5970f0-729d-4cf2-b2f0-19913449e805]