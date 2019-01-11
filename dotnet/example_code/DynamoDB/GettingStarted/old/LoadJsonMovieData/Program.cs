// snippet-sourcedescription:[Program.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.c5d00c17-632c-4c21-86c1-9c641840af96] 

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
}// snippet-end:[dynamodb.dotNET.CodeExample.c5d00c17-632c-4c21-86c1-9c641840af96]