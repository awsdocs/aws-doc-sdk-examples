// snippet-sourcedescription:[Program.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.08a973cd-805f-4eb3-8f1f-f59f2ba5a691] 

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
            // Get a Table object for the table that you created in Step 1
            Table table = GetTableObject("Movies");
            if (table == null)
                return;

            // Create the condition
            DeleteItemOperationConfig opConfig = new DeleteItemOperationConfig();
            opConfig.ConditionalExpression = new Expression();
            opConfig.ConditionalExpression.ExpressionAttributeValues[":val"] = 5.0;
            opConfig.ConditionalExpression.ExpressionStatement = "info.rating <= :val";

            // Delete this item
            try
            {
                table.DeleteItem(2015, "The Big New Movie", opConfig);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: Could not delete the movie item with year={0}, title=\"{1}\"\n   Reason: {2}.",
                           2015, "The Big New Movie", ex.Message);
            }

            // Try to retrieve it, to see if it has been deleted
            Document document = table.GetItem(2015, "The Big New Movie");
            if (document == null)
                Console.WriteLine("\n The movie item with year={0}, title=\"{1}\" has been deleted.",
                           2015, "The Big New Movie");
            else
                Console.WriteLine("\nRead back the item: \n" + document.ToJsonPretty());

            // Keep the console open if in Debug mode...
            Console.Write("\n\n ...Press any key to continue");
            Console.ReadKey();
            Console.WriteLine();
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
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.08a973cd-805f-4eb3-8f1f-f59f2ba5a691]