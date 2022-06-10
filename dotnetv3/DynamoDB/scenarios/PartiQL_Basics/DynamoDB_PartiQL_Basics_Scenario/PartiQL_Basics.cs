// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[PartiQL.dotnetv3.PartiQLBasicsScenario-Complete]
namespace DynamoDB_PartiQL_Basics_Scenario
{
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    internal class PartiQL_Basics
    {
        // Separator for the console display.
        private static readonly string SepBar = new string('-', 80);

        public static async Task Main()
        {
            const string tableName = "movie_table";
            const string movieFileName = "moviedata.json";

            var client = new AmazonDynamoDBClient();

            DisplayInstructions();

            // Create the table and wait for it to be active.
            Console.WriteLine($"Creating the movie table: {tableName}");

            var success = await DynamoDBMethods.CreateMovieTableAsync(tableName);

            WaitForEnter();

            // Add movie information to the table from moviedata.json. See the
            // instructions at the top of this file to download the JSON file.
            success = await PartiQLMethods.InsertMovies(tableName, movieFileName);

            // Get a movie using a SELECT statement.
            var movies = await PartiQLMethods.GetSingleMovie(tableName, "Escape Plan");
            DisplayMovies(movies);

            // Add a single movie to the table.
            var movieTitle = "Spider-Man: No Way Home";
            var year = 2021;

            success = await PartiQLMethods.InsertSingleMovie(tableName, movieTitle, year);
            if (success)
            {
                Console.WriteLine($"Successfully inserted {movieTitle} into the table.");
            }
            else
            {
                Console.WriteLine($"Could not insert {movieTitle} into the table.");
            }

            WaitForEnter();

            // Update a single movie in the movies table.
            var producer = "Marvel Studios";

            success = await PartiQLMethods.UpdateSingleMovie(tableName, producer, movieTitle, year);
            if (success)
            {
                Console.WriteLine($"Successfully updated {movieTitle}.");
            }
            else
            {
                Console.WriteLine($"Couldn't update {movieTitle}.");
            }

            WaitForEnter();

            // Now delete the movie we just added.
            success = await PartiQLMethods.DeleteSingleMovie(tableName, movieTitle, year);
            if (success)
            {
                Console.WriteLine($"Successfully deleted {movieTitle}.");
            }
            else
            {
                Console.WriteLine($"Couldn't delete {movieTitle}.");
            }

            WaitForEnter();

            // Update multiple movies using the BatchExecute statement.
            var producer1 = "LucasFilm";
            var title1 = "Star Wars";
            var year1 = 1977;
            var producer2 = "MGM";
            var title2 = "Wizard of Oz";
            var year2 = 1939;

            success = await PartiQLMethods.UpdateBatch(tableName, producer1, title1, year1, producer2, title2, year2);
            if (success)
            {
                Console.WriteLine($"Successfully update {title1} and {title2}.");
            }
            else
            {
                Console.WriteLine("Update failed.");
            }

            WaitForEnter();

            // Delete multiple movies using the BatchExecute statement.
            success = await PartiQLMethods.DeleteBatch(tableName, title1, year1, title2, year2);

            if (success)
            {
                Console.WriteLine($"Deleted {title1} and {title2}");
            }
            else
            {
                Console.WriteLine($"could not delete {title1} or {title2}");
            }

            // PartiQL Basics Scenario is complete so delete the movie table.
            success = await DynamoDBMethods.DeleteTableAsync(tableName);

            if (success)
            {
                Console.WriteLine($"Successfully deleted {tableName}");
            }
            else
            {
                Console.WriteLine($"Could not delete {tableName}");
            }
        }

        private static void DisplayMovies(List<Dictionary<string, AttributeValue>> items)
        {
            if (items.Count > 0)
            {
                Console.WriteLine($"Found {items.Count} movies.");
                items.ForEach(item => Console.WriteLine($"{item["year"].N}\t{item["title"].S}"));
            }
            else
            {
                Console.WriteLine($"Didn't find a movie matching the supplied criteria.");
            }
        }

        /// <summary>
        /// Displays the description of the application on the console.
        /// </summary>
        private static void DisplayInstructions()
        {
            Console.Clear();
            Console.WriteLine();
            Console.Write(new string(' ', 24));
            Console.WriteLine("DynamoDB PartQL Basics Example");
            Console.WriteLine(SepBar);
            Console.WriteLine("This demo application shows the basics of using DynamoDB with the AWS SDK for");
            Console.WriteLine(".NET version 3.7 and .NET Core 5.");
            Console.WriteLine(SepBar);
            Console.WriteLine("Creates a table using the CreateTable method.");
            Console.WriteLine("Inserts movies using the BatchExecuteStatement method.");
            Console.WriteLine();
            Console.WriteLine("Gets a single movie from the database using a PartiQL SELECT statement.");
            Console.WriteLine("Inserts a movie using the PartiQL INSERT statement.");
            Console.WriteLine("Updates a movie using a PartiQL UPDATE statement.");
            Console.WriteLine("Deletes a movie using a PartiQL DELETE statement.");
            Console.WriteLine();
            Console.WriteLine("Then, the program uses the BatchExecuteStatement Method to:");
            Console.WriteLine("\tGet multiple movies using a PartiQL SELECT statement.");
            Console.WriteLine("\tUpdate multiple movies using a PartiQL UPDATE statement.");
            Console.WriteLine("\tDelete multiple movies using a PartiQL DELETE statement.");
            Console.WriteLine();
            Console.WriteLine("Finally, we clean up the resources we created by deleting the table.");
            Console.WriteLine(SepBar);

            WaitForEnter();
        }

        /// <summary>
        /// Simple method to wait for the <Enter> key to be pressed.
        /// </summary>
        private static void WaitForEnter()
        {
            Console.Write(SepBar);
            Console.WriteLine("\nPress <Enter> to continue.");
            Console.Write(SepBar);
            _ = Console.ReadLine();
        }
    }
}

// snippet-end:[PartiQL.dotnetv3.PartiQLBasicsScenario-Complete]
