// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.DeleteTable] 
using System;
using System.Text;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
    class Program
    {
        static void Main(string[] args)
        {
            // Get an AmazonDynamoDBClient for the local DynamoDB database
            AmazonDynamoDBClient client = GetLocalClient();

            try
            {
                client.DeleteTable("Movies");
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: the \'Movies\" table could not be deleted!\n    Reason: " + ex.Message);
                Console.Write("\n\n ...Press any key to continue");
                Console.ReadKey();
                Console.WriteLine();
                return;
            }
            Console.WriteLine("\n Deleted the \'Movies\" table successfully!");
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
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.DeleteTable]