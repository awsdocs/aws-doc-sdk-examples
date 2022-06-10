// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[PartiQL.dotnetv3.PartiQLBasicsScenario]

// Before you run this example, download 'movies.json' from
// https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Js.02.html,
// and put it in the same folder as the example.

// Separator for the console display.
var SepBar = new string('-', 80);
const string tableName = "movie_table";
const string movieFileName = "moviedata.json";

var client = new AmazonDynamoDBClient();

DisplayInstructions();

// Create the table and wait for it to be active.
Console.WriteLine($"Creating the movie table: {tableName}");

var success = await DynamoDBMethods.CreateMovieTableAsync(tableName);
if (success)
{
    Console.WriteLine($"Successfully created table: {tableName}.");
}

WaitForEnter();

// Add movie information to the table from moviedata.json. See the
// instructions at the top of this file to download the JSON file.
Console.WriteLine($"Inserting movies into the new table. Please wait...");
success = await PartiQLMethods.InsertMovies(tableName, movieFileName);

WaitForEnter();

// Get a movie using a SELECT statement.
var movies = await PartiQLMethods.GetSingleMovie(tableName, "Escape Plan");
DisplayMovies(movies);

WaitForEnter();

// Add a single movie to the table.
var movieTitle = "Spider-Man: No Way Home";
var year = 2021;

success = await PartiQLMethods.InsertSingleMovie(tableName, movieTitle, year);
if (success)
{
    Console.WriteLine($"Successfully inserted {movieTitle} into the table.");
    movies = await PartiQLMethods.GetSingleMovie(tableName, movieTitle);
    DisplayMovies(movies);
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

// Now delete the movie that was just added.
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

/// <summary>
/// Displays a collection that contains information about one or more movies.
/// </summary>
/// <param name="items">A collection of movie information.</param>
void DisplayMovies(List<Dictionary<string, AttributeValue>> items)
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
void DisplayInstructions()
{
    Console.Clear();
    Console.WriteLine();
    Console.Write(new string(' ', 24));
    Console.WriteLine("DynamoDB PartQL Basics Example");
    Console.WriteLine(SepBar);
    Console.WriteLine("This demo application shows the basics of using DynamoDB with the AWS SDK for");
    Console.WriteLine(".NET version 3.7 and .NET 6.");
    Console.WriteLine(SepBar);
    Console.WriteLine("Creates a table using the CreateTable method.");
    Console.WriteLine("Inserts a movie using the PartiQL INSERT statement.");
    Console.WriteLine("Gets a single movie from the database using a PartiQL SELECT statement.");
    Console.WriteLine("Updates a movie using a PartiQL UPDATE statement.");
    Console.WriteLine("Deletes a movie using a PartiQL DELETE statement.");
    Console.WriteLine("Cleans up the resources created for the demo by deleting the table.");
    Console.WriteLine(SepBar);

    WaitForEnter();
}

/// <summary>
/// Simple method to wait for the <Enter> key to be pressed.
/// </summary>
void WaitForEnter()
{
    Console.WriteLine("\nPress <Enter> to continue.");
    Console.Write(SepBar);
    _ = Console.ReadLine();
}
