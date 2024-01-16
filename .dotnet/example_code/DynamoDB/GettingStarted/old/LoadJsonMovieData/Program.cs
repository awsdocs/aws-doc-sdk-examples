// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.LoadJsonMovieData] 
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;

using Newtonsoft;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace DynamoDB_intro
{
    class Program
    {
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
            Table table;
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

        public static void Main(string[] args)
        {
            // First, read in the JSON data from the moviedate.json file
            StreamReader sr = null;
            JsonTextReader jtr = null;
            JArray movieArray = null;
            try
            {
                sr = new StreamReader("moviedata.json");
                jtr = new JsonTextReader(sr);
                movieArray = (JArray)JToken.ReadFrom(jtr);
            }
            catch (Exception ex)
            {
                Console.WriteLine("\n Error: could not read from the 'moviedata.json' file, because: " + ex.Message);
                PauseForDebugWindow();
                return;
            }
            finally
            {
                if (jtr != null)
                    jtr.Close();
                if (sr != null)
                    sr.Close();
            }

            // Get a Table object for the table that you created in Step 1
            Table table = GetTableObject("Movies");
            if (table == null)
            {
                PauseForDebugWindow();
                return;
            }

            // Load the movie data into the table (this could take some time)
            Console.Write("\n   Now writing {0:#,##0} movie records from moviedata.json (might take 15 minutes)...\n   ...completed: ", movieArray.Count);
            for (int i = 0, j = 99; i < movieArray.Count; i++)
            {
                try
                {
                    string itemJson = movieArray[i].ToString();
                    Document doc = Document.FromJson(itemJson);
                    table.PutItem(doc);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("\nError: Could not write the movie record #{0:#,##0}, because {1}", i, ex.Message);
                    PauseForDebugWindow();
                    return;
                }
                if (i >= j)
                {
                    j++;
                    Console.Write("{0,5:#,##0}, ", j);
                    if (j % 1000 == 0)
                        Console.Write("\n                 ");
                    j += 99;
                }
            }
            Console.WriteLine("\n   Finished writing all movie records to DynamoDB!");
            PauseForDebugWindow();
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
// snippet-end:[dynamodb.dotNET.CodeExample.LoadJsonMovieData]