// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.03_LoadingData] 
using System;
using System.IO;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace DynamoDB_intro
{
    public static partial class DdbIntro
    {
        public static async Task<bool> LoadingData_async(Table table, string filePath)
        {
            var movieArray = await ReadJsonMovieFile_async(filePath);

            if (movieArray != null)
                await LoadJsonMovieData_async(table, movieArray);

            return true;
        }
        
        public static async Task<JArray> ReadJsonMovieFile_async(string jsonMovieFilePath)
        {
            StreamReader sr = null;
            JsonTextReader jtr = null;
            JArray movieArray = null;

            Console.WriteLine("  -- Reading the movies data from a JSON file...");
           
            try
            {
                sr = new StreamReader(jsonMovieFilePath);
                jtr = new JsonTextReader(sr);
                movieArray = (JArray)await JToken.ReadFromAsync(jtr);
            }
            catch (Exception ex)
            {
                Console.WriteLine("     ERROR: could not read the file!\n          Reason: {0}.", ex.Message);
            }
            finally
            {
                jtr?.Close();
                sr?.Close();
            }
            
            return movieArray;
        }

        public static async Task<bool> LoadJsonMovieData_async(Table moviesTable, JArray moviesArray)
        {
            int n = moviesArray.Count;
            Console.Write("     -- Starting to load {0:#,##0} movie records into the Movies table asynchronously...\n" + "" +
              "        Wrote: ", n);
            for (int i = 0, j = 99; i < n; i++)
            {
                try
                {
                    string itemJson = moviesArray[i].ToString();
                    Document doc = Document.FromJson(itemJson);
                    Task putItem = moviesTable.PutItemAsync(doc);
                    if (i >= j)
                    {
                        j++;
                        Console.Write("{0,5:#,##0}, ", j);
                        if (j % 1000 == 0)
                            Console.Write("\n               ");
                        j += 99;
                    }
                    await putItem;
                }
                catch (Exception)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.03_LoadingData]