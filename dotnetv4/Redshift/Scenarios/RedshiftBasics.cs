// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using System.Threading.Tasks;
using Amazon.Redshift;
using Amazon.RedshiftDataAPIService;
using RedshiftActions;

namespace RedshiftBasics;

// snippet-start:[Redshift.dotnetv4.RedshiftScenario]
/// <summary>
/// Amazon Redshift Getting Started Scenario.
/// </summary>
public class RedshiftBasics
{
    private static RedshiftWrapper? _redshiftWrapper;
    private static readonly string MoviesFilePath = "../../../../../../../resources/sample_files/movies.json";

    /// <summary>
    /// Main method for the Amazon Redshift Getting Started scenario.
    /// </summary>
    /// <param name="args">Command line arguments.</param>
    public static async Task Main(string[] args)
    {
        // Initialize the Amazon Redshift clients
        var redshiftClient = new AmazonRedshiftClient();
        var redshiftDataClient = new AmazonRedshiftDataAPIServiceClient();
        _redshiftWrapper = new RedshiftWrapper(redshiftClient, redshiftDataClient);

        Console.WriteLine("================================================================================");
        Console.WriteLine("Welcome to the Amazon Redshift SDK Getting Started scenario.");
        Console.WriteLine("This .NET program demonstrates how to interact with Amazon Redshift by using the AWS SDK for .NET.");
        Console.WriteLine("Amazon Redshift is a fully managed, petabyte-scale data warehouse service hosted in the cloud.");
        Console.WriteLine("The program's primary functionality includes cluster creation, verification of cluster readiness,");
        Console.WriteLine("list databases, table creation, data population within the table, and execution of SQL statements.");
        Console.WriteLine("Furthermore, it demonstrates the process of querying data from the Movie table.");
        Console.WriteLine("Upon completion of the program, all AWS resources are cleaned up.");
        Console.WriteLine("Let's get started...");
        Console.WriteLine("================================================================================");

        try
        {
            await RunScenarioAsync();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred: {ex.Message}");
            Console.WriteLine(ex.StackTrace);
        }
        finally
        {
            redshiftClient.Dispose();
            redshiftDataClient.Dispose();
        }
    }

    /// <summary>
    /// Run the complete Amazon Redshift scenario.
    /// </summary>
    private static async Task RunScenarioAsync()
    {
        // Step 1: Get user credentials
        Console.WriteLine("Please enter your user name (default is awsuser):");
        var userName = Console.ReadLine();
        if (string.IsNullOrEmpty(userName))
            userName = "awsuser";

        Console.WriteLine("================================================================================");
        Console.WriteLine("Please enter your user password (default is AwsUser1000):");
        var userPassword = Console.ReadLine();
        if (string.IsNullOrEmpty(userPassword))
            userPassword = "AwsUser1000";

        Console.WriteLine("================================================================================");
        Console.WriteLine("================================================================================");
        Console.WriteLine("A Redshift cluster refers to the collection of computing resources and storage that work together to process and analyze large volumes of data.");

        // Step 2: Get cluster identifier
        Console.WriteLine("Enter a cluster id value (default is redshift-cluster-movies):");
        var clusterIdentifier = Console.ReadLine();
        if (string.IsNullOrEmpty(clusterIdentifier))
            clusterIdentifier = "redshift-cluster-movies";

        var databaseName = "dev";

        try
        {
            // Step 3: Create Redshift cluster
            await _redshiftWrapper!.CreateClusterAsync(clusterIdentifier, databaseName, userName, userPassword);
            Console.WriteLine("================================================================================");

            // Step 4: Wait for cluster to become available
            Console.WriteLine("================================================================================");
            await _redshiftWrapper.WaitForClusterAvailableAsync(clusterIdentifier);
            Console.WriteLine("================================================================================");

            // Step 5: List databases
            Console.WriteLine("================================================================================");
            Console.WriteLine($" When you created {clusterIdentifier}, the dev database is created by default and used in this scenario.");
            Console.WriteLine(" To create a custom database, you need to have a CREATEDB privilege.");
            Console.WriteLine(" For more information, see the documentation here: https://docs.aws.amazon.com/redshift/latest/dg/r_CREATE_DATABASE.html.");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();
            Console.WriteLine("================================================================================");

            Console.WriteLine("================================================================================");
            Console.WriteLine($"List databases in {clusterIdentifier}");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();
            await _redshiftWrapper.ListDatabasesAsync(clusterIdentifier, userName);
            Console.WriteLine("================================================================================");

            // Step 6: Create Movies table
            Console.WriteLine("================================================================================");
            Console.WriteLine("Now you will create a table named Movies.");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();
            await _redshiftWrapper.CreateTableAsync(clusterIdentifier, databaseName, userName);
            Console.WriteLine("================================================================================");

            // Step 7: Populate the Movies table
            Console.WriteLine("================================================================================");
            Console.WriteLine("Populate the Movies table using the Movies.json file.");
            Console.WriteLine("Specify the number of records you would like to add to the Movies Table.");
            Console.WriteLine("Please enter a value between 50 and 200.");
            Console.Write("Enter a value: ");

            var recordCountInput = Console.ReadLine();
            if (!int.TryParse(recordCountInput, out var recordCount) || recordCount < 50 || recordCount > 200)
            {
                recordCount = 50;
                Console.WriteLine($"Invalid input. Using default value of {recordCount}.");
            }

            await PopulateMoviesTableAsync(clusterIdentifier, databaseName, userName, recordCount);
            Console.WriteLine($"{recordCount} records were added to the Movies table.");
            Console.WriteLine("================================================================================");

            // Step 8 & 9: Query movies by year
            Console.WriteLine("================================================================================");
            Console.WriteLine("Query the Movies table by year. Enter a value between 2012-2014.");
            Console.Write("Enter a year: ");
            var yearInput = Console.ReadLine();
            if (!int.TryParse(yearInput, out var year) || year < 2012 || year > 2014)
            {
                year = 2013;
                Console.WriteLine($"Invalid input. Using default value of {year}.");
            }

            await _redshiftWrapper.QueryMoviesByYearAsync(clusterIdentifier, databaseName, userName, year);
            Console.WriteLine("================================================================================");

            // Step 10: Modify the cluster
            Console.WriteLine("================================================================================");
            Console.WriteLine("Now you will modify the Redshift cluster.");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();
            await _redshiftWrapper.ModifyClusterAsync(clusterIdentifier, "wed:07:30-wed:08:00");
            Console.WriteLine("================================================================================");

            // Step 11 & 12: Delete cluster confirmation
            Console.WriteLine("================================================================================");
            Console.WriteLine("Would you like to delete the Amazon Redshift cluster? (y/n)");
            var deleteResponse = Console.ReadLine();
            if (deleteResponse?.ToLower() == "y" || deleteResponse?.ToLower() == "yes")
            {
                await _redshiftWrapper.DeleteClusterAsync(clusterIdentifier);
            }
            Console.WriteLine("================================================================================");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred during the scenario: {ex.Message}");

            // Attempt cleanup
            Console.WriteLine("Attempting to clean up resources...");
            try
            {
                await _redshiftWrapper!.DeleteClusterAsync(clusterIdentifier);
            }
            catch (Exception cleanupEx)
            {
                Console.WriteLine($"Cleanup failed: {cleanupEx.Message}");
            }
            throw;
        }

        Console.WriteLine("================================================================================");
        Console.WriteLine("This concludes the Amazon Redshift SDK Getting Started scenario.");
        Console.WriteLine("================================================================================");
    }

    /// <summary>
    /// Populate the Movies table with data from the JSON file.
    /// </summary>
    /// <param name="clusterIdentifier">The cluster identifier.</param>
    /// <param name="database">The database name.</param>
    /// <param name="dbUser">The database user.</param>
    /// <param name="recordCount">Number of records to insert.</param>
    private static async Task PopulateMoviesTableAsync(string clusterIdentifier, string database, string dbUser, int recordCount)
    {
        try
        {
            if (!File.Exists(MoviesFilePath))
            {
                Console.WriteLine($"Movies file not found at {MoviesFilePath}. Using sample data instead.");
                await PopulateWithSampleDataAsync(clusterIdentifier, database, dbUser, recordCount);
                return;
            }

            var jsonContent = await File.ReadAllTextAsync(MoviesFilePath);
            var movies = JsonSerializer.Deserialize<List<Movie>>(jsonContent);

            if (movies == null)
            {
                Console.WriteLine("Failed to parse movies JSON file. Using sample data instead.");
                await PopulateWithSampleDataAsync(clusterIdentifier, database, dbUser, recordCount);
                return;
            }

            var insertCount = Math.Min(recordCount, movies.Count);

            for (int i = 0; i < insertCount; i++)
            {
                var movie = movies[i];
                await _redshiftWrapper!.InsertMovieAsync(clusterIdentifier, database, dbUser, movie.Id, movie.Title, movie.Year);
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error populating movies table: {ex.Message}");
            Console.WriteLine("Using sample data instead.");
            await PopulateWithSampleDataAsync(clusterIdentifier, database, dbUser, recordCount);
        }
    }

    /// <summary>
    /// Populate the table with sample movie data when JSON file is not available.
    /// </summary>
    /// <param name="clusterIdentifier">The cluster identifier.</param>
    /// <param name="database">The database name.</param>
    /// <param name="dbUser">The database user.</param>
    /// <param name="recordCount">Number of records to insert.</param>
    private static async Task PopulateWithSampleDataAsync(string clusterIdentifier, string database, string dbUser, int recordCount)
    {
        var sampleMovies = new List<Movie>
        {
            new Movie { Id = 1, Title = "Rush", Year = 2013 },
            new Movie { Id = 2, Title = "Prisoners", Year = 2013 },
            new Movie { Id = 3, Title = "The Hunger Games: Catching Fire", Year = 2013 },
            new Movie { Id = 4, Title = "Thor: The Dark World", Year = 2013 },
            new Movie { Id = 5, Title = "This Is the End", Year = 2013 },
            new Movie { Id = 6, Title = "Despicable Me 2", Year = 2013 },
            new Movie { Id = 7, Title = "Man of Steel", Year = 2013 },
            new Movie { Id = 8, Title = "Gravity", Year = 2013 },
            new Movie { Id = 9, Title = "Pacific Rim", Year = 2013 },
            new Movie { Id = 10, Title = "World War Z", Year = 2013 },
            new Movie { Id = 11, Title = "Iron Man 3", Year = 2013 },
            new Movie { Id = 12, Title = "Star Trek Into Darkness", Year = 2013 },
            new Movie { Id = 13, Title = "Fast & Furious 6", Year = 2013 },
            new Movie { Id = 14, Title = "Monsters University", Year = 2013 },
            new Movie { Id = 15, Title = "Elysium", Year = 2013 },
            new Movie { Id = 16, Title = "The Hobbit: The Desolation of Smaug", Year = 2013 },
            new Movie { Id = 17, Title = "Captain Phillips", Year = 2013 },
            new Movie { Id = 18, Title = "Ender's Game", Year = 2013 },
            new Movie { Id = 19, Title = "The Wolverine", Year = 2013 },
            new Movie { Id = 20, Title = "Now You See Me", Year = 2013 }
        };

        // Generate more movies if needed
        var movieList = new List<Movie>(sampleMovies);
        var random = new Random();
        var years = new[] { 2012, 2013, 2014 };
        var baseMovieTitles = new[] { "Action Movie", "Drama Film", "Comedy Show", "Thriller Movie", "Adventure Film", "Sci-Fi Movie", "Horror Film" };

        while (movieList.Count < recordCount)
        {
            var baseTitle = baseMovieTitles[random.Next(baseMovieTitles.Length)];
            var year = years[random.Next(years.Length)];
            var movie = new Movie
            {
                Id = movieList.Count + 1,
                Title = $"{baseTitle} {movieList.Count + 1}",
                Year = year
            };
            movieList.Add(movie);
        }

        var insertCount = Math.Min(recordCount, movieList.Count);

        for (int i = 0; i < insertCount; i++)
        {
            var movie = movieList[i];
            await _redshiftWrapper!.InsertMovieAsync(clusterIdentifier, database, dbUser, movie.Id, movie.Title, movie.Year);
        }
    }

    /// <summary>
    /// Movie data model.
    /// </summary>
    private class Movie
    {
        public int Id { get; set; }
        public string Title { get; set; } = string.Empty;
        public int Year { get; set; }
    }
}
// snippet-end:[Redshift.dotnetv4.RedshiftScenario]

