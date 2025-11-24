// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Collections.Generic;
using System.IO;
using System.Text.Json;
using System.Threading.Tasks;
using Amazon.Redshift;
using Amazon.RedshiftDataAPIService;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using RedshiftActions;

namespace RedshiftBasics;

// snippet-start:[Redshift.dotnetv4.RedshiftScenario]
/// <summary>
/// Amazon Redshift Getting Started Scenario.
/// </summary>
public class RedshiftBasics
{
    public static bool IsInteractive = true;
    public static RedshiftWrapper? Wrapper = null;
    public static ILogger logger = null!;
    private static readonly string _moviesFilePath = "../../../../../../resources/sample_files/movies.json";

    /// <summary>
    /// Main method for the Amazon Redshift Getting Started scenario.
    /// </summary>
    /// <param name="args">Command line arguments.</param>
    public static async Task Main(string[] args)
    {
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonRedshift>()
                    .AddAWSService<IAmazonRedshiftDataAPIService>()
                    .AddTransient<RedshiftWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<RedshiftBasics>();

        Wrapper = host.Services.GetRequiredService<RedshiftWrapper>();

        await RunScenarioAsync();
    }

    /// <summary>
    /// Run the complete Amazon Redshift scenario.
    /// </summary>
    public static async Task RunScenarioAsync()
    {
        try
        {
            Console.WriteLine(
                "================================================================================");
            Console.WriteLine("Welcome to the Amazon Redshift SDK Getting Started scenario.");
            Console.WriteLine(
                "This .NET program demonstrates how to interact with Amazon Redshift by using the AWS SDK for .NET.");
            Console.WriteLine("Let's get started...");
            Console.WriteLine(
                "================================================================================");

            // Set all variables to default values
            string userName = "awsuser";
            string userPassword = "AwsUser1000";
            string clusterIdentifier = "redshift-cluster-movies";
            var databaseName = "dev";
            int recordCount = 50;
            int year = 2013;

            // Step 1: Get user credentials (if interactive)
            if (IsInteractive)
            {
                Console.WriteLine("Please enter your user name (default is awsuser):");
                var userInput = Console.ReadLine();
                if (!string.IsNullOrEmpty(userInput))
                    userName = userInput;

                Console.WriteLine("================================================================================");
                Console.WriteLine("Please enter your user password (default is AwsUser1000):");
                var passwordInput = Console.ReadLine();
                if (!string.IsNullOrEmpty(passwordInput))
                    userPassword = passwordInput;

                Console.WriteLine("================================================================================");

                // Step 2: Get cluster identifier
                Console.WriteLine("Enter a cluster id value (default is redshift-cluster-movies):");
                var clusterInput = Console.ReadLine();
                if (!string.IsNullOrEmpty(clusterInput))
                    clusterIdentifier = clusterInput;
            }
            else
            {
                Console.WriteLine($"Using default values: userName={userName}, clusterIdentifier={clusterIdentifier}");
            }

            // Step 3: Create Redshift cluster
            await Wrapper!.CreateClusterAsync(clusterIdentifier, databaseName, userName, userPassword);
            Console.WriteLine("================================================================================");

            // Step 4: Wait for cluster to become available
            Console.WriteLine("================================================================================");
            await Wrapper.WaitForClusterAvailableAsync(clusterIdentifier, IsInteractive);
            Console.WriteLine("================================================================================");

            // Step 5: List databases
            Console.WriteLine("================================================================================");
            Console.WriteLine($" When you created {clusterIdentifier}, the dev database is created by default and used in this scenario.");
            Console.WriteLine(" To create a custom database, you need to have a CREATEDB privilege.");
            Console.WriteLine(" For more information, see the documentation here: https://docs.aws.amazon.com/redshift/latest/dg/r_CREATE_DATABASE.html.");
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }
            Console.WriteLine("================================================================================");

            Console.WriteLine("================================================================================");
            Console.WriteLine($"List databases in {clusterIdentifier}");
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }
            await Wrapper.ListDatabasesAsync(clusterIdentifier, userName, databaseName);
            Console.WriteLine("================================================================================");

            // Step 6: Create Movies table
            Console.WriteLine("================================================================================");
            Console.WriteLine("Now you will create a table named Movies.");
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }
            await Wrapper.CreateTableAsync(clusterIdentifier, databaseName, userName);
            Console.WriteLine("================================================================================");

            // Step 7: Populate the Movies table
            Console.WriteLine("================================================================================");
            Console.WriteLine("Populate the Movies table using the Movies.json file.");

            if (IsInteractive)
            {
                Console.WriteLine("Specify the number of records you would like to add to the Movies Table.");
                Console.WriteLine("Please enter a value between 50 and 200.");
                Console.Write("Enter a value: ");

                var recordCountInput = Console.ReadLine();
                if (int.TryParse(recordCountInput, out var inputCount) && inputCount >= 50 && inputCount <= 200)
                {
                    recordCount = inputCount;
                }
                else
                {
                    Console.WriteLine($"Invalid input. Using default value of {recordCount}.");
                }
            }
            else
            {
                Console.WriteLine($"Using default record count: {recordCount}");
            }

            await PopulateMoviesTableAsync(clusterIdentifier, databaseName, userName, recordCount);
            Console.WriteLine($"{recordCount} records were added to the Movies table.");
            Console.WriteLine("================================================================================");

            // Step 8 & 9: Query movies by year
            Console.WriteLine("================================================================================");
            Console.WriteLine("Query the Movies table by year. Enter a value between 2012-2014.");

            if (IsInteractive)
            {
                Console.Write("Enter a year: ");
                var yearInput = Console.ReadLine();
                if (int.TryParse(yearInput, out var inputYear) && inputYear >= 2012 && inputYear <= 2014)
                {
                    year = inputYear;
                }
                else
                {
                    Console.WriteLine($"Invalid input. Using default value of {year}.");
                }
            }
            else
            {
                Console.WriteLine($"Using default year: {year}");
            }

            await Wrapper.QueryMoviesByYearAsync(clusterIdentifier, databaseName, userName, year);
            Console.WriteLine("================================================================================");

            // Step 10: Modify the cluster
            Console.WriteLine("================================================================================");
            Console.WriteLine("Now you will modify the Redshift cluster.");
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }
            await Wrapper.ModifyClusterAsync(clusterIdentifier, "wed:07:30-wed:08:00");
            Console.WriteLine("================================================================================");

            // Step 11 & 12: Delete cluster confirmation
            Console.WriteLine("================================================================================");
            if (IsInteractive)
            {
                Console.WriteLine("Would you like to delete the Amazon Redshift cluster? (y/n)");
                var deleteResponse = Console.ReadLine();
                if (deleteResponse?.ToLower() == "y" || deleteResponse?.ToLower() == "yes")
                {
                    await Wrapper.DeleteClusterAsync(clusterIdentifier);
                }
            }
            else
            {
                Console.WriteLine("Deleting the Amazon Redshift cluster (non-interactive mode)...");
                await Wrapper.DeleteClusterAsync(clusterIdentifier);
            }
            Console.WriteLine("================================================================================");

            Console.WriteLine("================================================================================");
            Console.WriteLine("This concludes the Amazon Redshift SDK Getting Started scenario.");
            Console.WriteLine("================================================================================");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred during the scenario: {ex.Message}");
            throw;
        }
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
        if (!File.Exists(_moviesFilePath))
        {
            throw new FileNotFoundException($"Required movies data file not found at: {_moviesFilePath}");
        }

        var jsonContent = await File.ReadAllTextAsync(_moviesFilePath);
        var options = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        };
        var movies = JsonSerializer.Deserialize<List<Movie>>(jsonContent, options);

        if (movies == null || movies.Count == 0)
        {
            throw new InvalidOperationException("Failed to parse movies JSON file or file is empty.");
        }

        var insertCount = Math.Min(recordCount, movies.Count);

        for (int i = 0; i < insertCount; i++)
        {
            var movie = movies[i];
            await Wrapper!.InsertMovieAsync(clusterIdentifier, database, dbUser, i, movie.Title, movie.Year);
        }
    }

    /// <summary>
    /// Movie data model.
    /// </summary>
    private class Movie
    {
        public string Title { get; set; } = string.Empty;
        public int Year { get; set; }
    }
}
// snippet-end:[Redshift.dotnetv4.RedshiftScenario]