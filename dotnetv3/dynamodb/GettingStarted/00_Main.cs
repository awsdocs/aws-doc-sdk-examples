// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.00_Main]
using System;
using System.Collections.Generic;
using System.Threading;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        // Global variables
        public static bool OperationSucceeded;
        public static bool OperationFailed;
        public static AmazonDynamoDBClient Client;
        public static Table MoviesTable;
        public static TableDescription MoviesTableDescription;
        public static CancellationTokenSource Source = new CancellationTokenSource();
        public static CancellationToken Token = Source.Token;
        public static Document MovieRecord;

        static void Main()
        {
            //  1.  Create a DynamoDB client connected to a DynamoDB-Local instance
            Console.WriteLine(StepString, 1,
              "Create a DynamoDB client connected to a DynamoDB-Local instance");
            if (!CreateClient(true) || !Pause())
                return;

            //  2.  Create a table for movie data asynchronously
            Console.WriteLine(StepString, 2,
              "Create a table for movie data");

            CreatingTable_async(MoviesTableName,
                                 MovieItemsAttributes,
                                 MoviesKeySchema,
                                 MoviesTableProvisionedThroughput).Wait();

            if (!Pause() || OperationFailed)
                return;

            try
            {
                MoviesTable = Table.LoadTable(DdbIntro.Client, MoviesTableName);
            }
            catch (Exception ex)
            {
                OperationFailed = true;
                Console.WriteLine(
                  " Error: Could not access the new '{0}' table after creating it;\n" +
                  "        Reason: {1}.", MoviesTableName, ex.Message);
                Pause();
                return;
            }

            //  3.  Load movie data into the Movies table asynchronously
            if ((MoviesTableDescription != null) &&
                (MoviesTableDescription.ItemCount == 0))
            {
                Console.WriteLine(StepString, 3,
                  "Load movie data into the Movies table");
                LoadingData_async(MoviesTable, MovieDataPath).Wait();

                if (!Pause() || OperationFailed)
                    return;
            }
            else
            {
                Console.WriteLine(StepString, 3,
                  "Skipped: Movie data is already loaded in the Movies table");

                if (!Pause())
                    return;
            }

            //  4.  Add a new movie to the Movies table
            Console.WriteLine(StepString, 4,
              "Add a new movie to the Movies table");
            Document newItemDocument = new Document();
            newItemDocument["year"] = 2018;
            newItemDocument["title"] = "The Big New Movie";
            newItemDocument["info"] = Document.FromJson(
                "{\"plot\" : \"Nothing happens at all.\",\"rating\" : 0}");

            WritingNewMovie_async(newItemDocument).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  5.  Read and display the new movie record that was just added
            Console.WriteLine(StepString, 5,
              "Read and display the new movie record that was just added");

            ReadingMovie_async(2018, "The Big New Movie", true).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  6.  Update the new movie record in various ways
            //-------------------------------------------------
            //  6a.  Create an UpdateItemRequest to:
            //       -- modify the plot and rating of the new movie, and
            //       -- add a list of actors to it
            Console.WriteLine(StepString, "6a",
              "Change the plot and rating for the new movie and add a list of actors");

            UpdateItemRequest updateRequest = new UpdateItemRequest()
            {
                TableName = MoviesTableName,
                Key = new Dictionary<string, AttributeValue>
                {
                    { PartitionKeyName, new AttributeValue { N = "2018" } },
                    { SortKeyName, new AttributeValue { S = "The Big New Movie" } }
                },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
                {
                    { ":r", new AttributeValue { N = "5.5" } },
                    { ":p", new AttributeValue { S = "Everything happens all at once!" } },
                    { ":a", new AttributeValue { L = new List<AttributeValue>
                            { new AttributeValue { S ="Larry" },
                                new AttributeValue { S = "Moe" },
                                new AttributeValue { S = "Curly" } }
                        }
                    }
                },
                UpdateExpression = "SET info.rating = :r, info.plot = :p, info.actors = :a",
                ReturnValues = "NONE"
            };

            UpdatingMovie_async(updateRequest, true).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  6b  Change the UpdateItemRequest so as to increment the rating of the
            //      new movie, and then make the update request asynchronously.
            Console.WriteLine(StepString, "6b",
              "Increment the new movie's rating atomically");
            Console.WriteLine("  -- Incrementing the rating of the new movie by 1...");

            updateRequest.ExpressionAttributeValues = new Dictionary<string, AttributeValue>
            {
                { ":inc", new AttributeValue { N = "1" } }

            };

            updateRequest.UpdateExpression = "SET info.rating = info.rating + :inc";
            UpdatingMovie_async(updateRequest, true).Wait();
            
            if (!Pause() || OperationFailed)
                return;

            //  6c  Change the UpdateItemRequest so as to increment the rating of the
            //      new movie, and then make the update request asynchronously.
            Console.WriteLine(StepString, "6c",
              "Now try the same increment again with a condition that fails... ");
            Console.WriteLine("  -- Now trying to increment the new movie's rating, but this time\n" +
                                "     ONLY ON THE CONDITION THAT the movie has more than 3 actors...");
            updateRequest.ExpressionAttributeValues.Add(":n", new AttributeValue { N = "3" });
            updateRequest.ConditionExpression = "size(info.actors) > :n";

            UpdatingMovie_async(updateRequest, true).Wait();

            if (!Pause() || OperationSucceeded)
                return;


            //  7.  Try conditionally deleting the movie that we added

            //  7a.  Try conditionally deleting the movie that we added
            Console.WriteLine(StepString, "7a",
              "Try deleting the new movie record with a condition that fails");
            Console.WriteLine("  -- Trying to delete the new movie,\n" +
                               "     -- but ONLY ON THE CONDITION THAT its rating is 5.0 or less...");
            Expression condition = new Expression();
            condition.ExpressionAttributeValues[":val"] = 5.0;
            condition.ExpressionStatement = "info.rating <= :val";

            DeletingItem_async(MoviesTable, 2018, "The Big New Movie", condition).Wait();

            if (!Pause() || OperationSucceeded)
                return;

            //  7b.  Now increase the cutoff to 7.0 and try to delete again...
            Console.WriteLine(StepString, "7b",
              "Now increase the cutoff to 7.0 and try to delete the movie again...");
            Console.WriteLine("  -- Now trying to delete the new movie again,\n" +
                               "     -- but this time on the condition that its rating is 7.0 or less...");
            condition.ExpressionAttributeValues[":val"] = 7.0;

            DeletingItem_async(MoviesTable, 2018, "The Big New Movie", condition).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  8.  Query the Movies table in 3 different ways
            Search search;

            //  8a. Just query on the year
            Console.WriteLine(StepString, "8a",
              "Query the Movies table using a Search object for all movies from 1985");
            Console.WriteLine("  -- First, create a Search object...");

            try
            {
                search = MoviesTable.Query(1985, new Expression());
            }
            catch (Exception ex)
            {
                Console.WriteLine("     ERROR: Failed to create the Search object because:\n            " +
                                   ex.Message);
                Pause();
                return;
            }

            Console.WriteLine("     -- Successfully created the Search object,\n" +
                               "        so now we'll display the movies retrieved by the query:");

            if ((search == null) || !Pause())
                return;

            SearchListing_async(search).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  8b. SearchListing_async
            Console.WriteLine(StepString, "8b",
              "Query for 1992 movies with titles from B... to Hzz... using Table.Query");
            Console.WriteLine("  -- Now setting up a QueryOperationConfig for the 'Search'...");

            QueryOperationConfig config = new QueryOperationConfig();

            config.Filter = new QueryFilter();
            config.Filter.AddCondition("year", QueryOperator.Equal, new DynamoDBEntry[] { 1992 });
            config.Filter.AddCondition("title", QueryOperator.Between, new DynamoDBEntry[] { "B", "Hzz" });
            config.AttributesToGet = new List<string> { "year", "title", "info" };
            config.Select = SelectValues.SpecificAttributes;

            Console.WriteLine("     -- Creating the Search object based on the QueryOperationConfig");

            try
            {
                search = MoviesTable.Query(config);
            }
            catch (Exception ex)
            {
                Console.WriteLine("     ERROR: Failed to create the Search object because:\n            " +
                                   ex.Message);

                if (!Pause() || OperationFailed)
                    return;
            }

            Console.WriteLine("     -- Successfully created the Search object,\n" +
                               "        so now we'll display the movies retrieved by the query.");

            if ((search == null) || !Pause())
                return;

            SearchListing_async(search).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  8c. Query using a QueryRequest
            Console.WriteLine(StepString, "8c",
              "Query the Movies table for 1992 movies with titles from M... to Tzz...");
            Console.WriteLine("  -- Next use a low-level query to retrieve a selection of movie attributes");

            QueryRequest qRequest = new QueryRequest
            {
                TableName = "Movies",
                ExpressionAttributeNames = new Dictionary<string, string>
                {
                    { "#yr", "year" }
                },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
                {
                    { ":qYr",   new AttributeValue { N = "1992" } },
                    { ":tSt",   new AttributeValue { S = "M" } },
                    { ":tEn",   new AttributeValue { S = "Tzz" } }
                },
                KeyConditionExpression = "#yr = :qYr and title between :tSt and :tEn",
                ProjectionExpression = "#yr, title, info.actors[0], info.genres, info.running_time_secs"
            };

            Console.WriteLine("     -- Using a QueryRequest to get the lead actor and genres of\n" +
                               "        1992 movies with titles between 'M...' and 'Tzz...'.");

            ClientQuerying_async(qRequest).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  9.  Try scanning the movies table to retrieve movies from several decades
            //  9a. Use Table.Scan with a Search object and a ScanFilter to retrieve movies from the 1950s
            Console.WriteLine(StepString, "9a",
              "Scan the Movies table to retrieve all movies from the 1950's");
            ScanFilter filter = new ScanFilter();
            filter.AddCondition("year", ScanOperator.Between, new DynamoDBEntry[] { 1950, 1959 });

            ScanOperationConfig scanConfig = new ScanOperationConfig
            {
                Filter = filter
            };

            Console.WriteLine("     -- Creating a Search object based on a ScanFilter");

            try
            {
                search = MoviesTable.Scan(scanConfig);
            }
            catch (Exception ex)
            {
                Console.WriteLine("     ERROR: Failed to create the Search object because:\n            " +
                                   ex.Message);
                Pause();
                return;
            }

            Console.WriteLine("     -- Successfully created the Search object");

            if ((search == null) || !Pause())
                return;

            SearchListing_async(search).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  9b. Use AmazonDynamoDBClient.Scan to retrieve movies from the 1960s
            Console.WriteLine(StepString, "9b",
              "Use a low-level scan to retrieve all movies from the 1960's");
            Console.WriteLine("     -- Using a ScanRequest to get movies from between 1960 and 1969");

            ScanRequest sRequest = new ScanRequest
            {
                TableName = "Movies",
                ExpressionAttributeNames = new Dictionary<string, string>
                {
                    { "#yr", "year" }
                },
                ExpressionAttributeValues = new Dictionary<string, AttributeValue>
                {
                    { ":y_a", new AttributeValue { N = "1960" } },
                    { ":y_z", new AttributeValue { N = "1969" } },
                },
                FilterExpression = "#yr between :y_a and :y_z",
                ProjectionExpression = "#yr, title, info.actors[0], info.directors, info.running_time_secs"
            };

            ClientScanning_async(sRequest).Wait();

            if (!Pause() || OperationFailed)
                return;

            //  10.  Delete the Movies table and all its contents
            Console.WriteLine(StepString, 10,
              "Finally, delete the Movies table and all its contents");

            DeletingTable_async(MoviesTableName).Wait();

            Console.WriteLine(
              "\n=================================================================================" +
              "\n            This concludes the DynamoDB Getting-Started demo program" +
              "\n=================================================================================" +
              "\n                      ...Press any key to exit");

            Console.ReadKey();
        }
        
        static bool Pause()
        {
            if (OperationFailed)
                Console.WriteLine("     Operation failed...");
            else if (OperationSucceeded)
                Console.WriteLine("     Completed that step successfully!");

            Console.WriteLine("      ...Press [Esc] to exit, or any other key to continue");
            ConsoleKeyInfo keyInf = Console.ReadKey();
            Console.WriteLine();

            return (keyInf.Key != ConsoleKey.Escape);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.00_Main]