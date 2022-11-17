// <copyright file="DynamoDB_Basics.cs" company="PlaceholderCompany">
// Copyright (c) PlaceholderCompany. All rights reserved.
// </copyright>

// snippet-start:[DynamoDB.dotnetv3.DynamoDB_Basics_Scenario]
// This example application performs the following basic Amazon DynamoDB
// functions:
//
//     CreateTableAsync
//     PutItemAsync
//     UpdateItemAsync
//     BatchWriteItemAsync
//     GetItemAsync
//     DeleteItemAsync
//     Query
//     Scan
//     DeleteItemAsync
//
// The code in this example uses the AWS SDK for .NET version 3.7 and .NET 5.
// Before you run this example, download 'movies.json' from
// https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/sample_files,
// and put it in the same folder as the example.
namespace DynamoDB_Basics_Scenario
{
    public class DynamoDB_Basics
    {
        // Separator for the console display.
        private static readonly string SepBar = new string('-', 80);

        public static async Task Main()
        {
            var client = new AmazonDynamoDBClient();

            var tableName = "movie_table";

            // relative path to moviedata.json in the local repository.
            var movieFileName = @"..\..\..\..\..\..\..\..\resources\sample_files\movies.json";

            DisplayInstructions();

            // Create a new table and wait for it to be active.
            Console.WriteLine($"Creating the new table: {tableName}");

            var success = await DynamoDbMethods.CreateMovieTableAsync(client, tableName);

            if (success)
            {
                Console.WriteLine($"\nTable: {tableName} successfully created.");
            }
            else
            {
                Console.WriteLine($"\nCould not create {tableName}.");
            }

            WaitForEnter();

            // Add a single new movie to the table.
            var newMovie = new Movie
            {
                Year = 2021,
                Title = "Spider-Man: No Way Home",
            };

            success = await DynamoDbMethods.PutItemAsync(client, newMovie, tableName);
            if (success)
            {
                Console.WriteLine($"Added {newMovie.Title} to the table.");
            }
            else
            {
                Console.WriteLine("Could not add movie to table.");
            }

            WaitForEnter();

            // Update the new movie by adding a plot and rank.
            var newInfo = new MovieInfo
            {
                Plot = "With Spider-Man's identity now revealed, Peter asks" +
                "Doctor Strange for help. When a spell goes wrong, dangerous" +
                "foes from other worlds start to appear, forcing Peter to" +
                "discover what it truly means to be Spider-Man.",
                Rank = 9,
            };

            success = await DynamoDbMethods.UpdateItemAsync(client, newMovie, newInfo, tableName);
            if (success)
            {
                Console.WriteLine($"Successfully updated the movie: {newMovie.Title}");
            }
            else
            {
                Console.WriteLine("Could not update the movie.");
            }

            WaitForEnter();

            // Add a batch of movies to the DynamoDB table from a list of
            // movies in a JSON file.
            var itemCount = await DynamoDbMethods.BatchWriteItemsAsync(client, movieFileName);
            Console.WriteLine($"Added {itemCount} movies to the table.");

            WaitForEnter();

            // Get a movie by key. (partition + sort)
            var lookupMovie = new Movie
            {
                Title = "Jurassic Park",
                Year = 1993,
            };

            Console.WriteLine("Looking for the movie \"Jurassic Park\".");
            var item = await DynamoDbMethods.GetItemAsync(client, lookupMovie, tableName);
            if (item.Count > 0)
            {
                DynamoDbMethods.DisplayItem(item);
            }
            else
            {
                Console.WriteLine($"Couldn't find {lookupMovie.Title}");
            }

            WaitForEnter();

            // Delete a movie.
            var movieToDelete = new Movie
            {
                Title = "The Town",
                Year = 2010,
            };

            success = await DynamoDbMethods.DeleteItemAsync(client, tableName, movieToDelete);

            if (success)
            {
                Console.WriteLine($"Successfully deleted {movieToDelete.Title}.");
            }
            else
            {
                Console.WriteLine($"Could not delete {movieToDelete.Title}.");
            }

            WaitForEnter();

            // Use Query to find all the movies released in 2010.
            int findYear = 2010;
            Console.WriteLine($"Movies released in {findYear}");
            var queryCount = await DynamoDbMethods.QueryMoviesAsync(client, tableName, findYear);
            Console.WriteLine($"Found {queryCount} movies released in {findYear}");

            WaitForEnter();

            // Use Scan to get a list of movies from 2001 to 2011.
            int startYear = 2001;
            int endYear = 2011;
            var scanCount = await DynamoDbMethods.ScanTableAsync(client, tableName, startYear, endYear);
            Console.WriteLine($"Found {scanCount} movies released between {startYear} and {endYear}");

            WaitForEnter();

            // Delete the table.
            success = await DynamoDbMethods.DeleteTableAsync(client, tableName);

            if (success)
            {
                Console.WriteLine($"Successfully deleted {tableName}");
            }
            else
            {
                Console.WriteLine($"Could not delete {tableName}");
            }

            Console.WriteLine("The DynamoDB Basics example application is done.");

            WaitForEnter();
        }

        /// <summary>
        /// Displays the description of the application on the console.
        /// </summary>
        private static void DisplayInstructions()
        {
            Console.Clear();
            Console.WriteLine();
            Console.Write(new string(' ', 28));
            Console.WriteLine("DynamoDB Basics Example");
            Console.WriteLine(SepBar);
            Console.WriteLine("This demo application shows the basics of using DynamoDB with the AWS SDK for");
            Console.WriteLine(".NET version 3.7 and .NET Core 5.");
            Console.WriteLine(SepBar);
            Console.WriteLine("The application does the following:");
            Console.WriteLine("\t1. Creates a table with partition: year and sort:title.");
            Console.WriteLine("\t2. Adds a single movie to the table.");
            Console.WriteLine("\t3. Adds movies to the table from moviedata.json.");
            Console.WriteLine("\t4. Updates the rating and plot of the movie that was just added.");
            Console.WriteLine("\t5. Gets a movie using its key (partition + sort).");
            Console.WriteLine("\t6. Deletes a movie.");
            Console.WriteLine("\t7. Uses QueryAsync to return all movies released in a given year.");
            Console.WriteLine("\t8. Uses ScanAsync to return all movies released within a range of years.");
            Console.WriteLine("\t9. Finally, it deletes the table that was just created.");
            WaitForEnter();
        }

        /// <summary>
        /// Simple method to wait for the Enter key to be pressed.
        /// </summary>
        private static void WaitForEnter()
        {
            Console.WriteLine("\nPress <Enter> to continue.");
            Console.WriteLine(SepBar);
            _ = Console.ReadLine();
        }
    }
}

// snippet-end:[DynamoDB.dotnetv3.DynamoDB_Basics_Scenario]