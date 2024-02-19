// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[S3LockWorkflow.dotnetv3.ObjectLockWorkflow]
using Amazon.S3;
using Amazon.S3.Model;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace ObjectLockScenario;

public class ObjectLockWorkflow
{
    /*
    Before running this .NET code example, set up your development environment, including your credentials.

    This .NET example performs the following tasks:
        1. Create test Amazon Simple Storage Service (S3) buckets with different lock policies.
        2. Upload sample objects to each bucket.
        3. Set some Legal Hold and Retention Periods on objects and buckets.
        4. Investigate lock policies by viewing settings or attempting to delete or overwrite objects.
        5. Clean up objects and buckets.
   */

    private static S3ActionsWrapper _s3ActionsWrapper = null!;
    private static IConfiguration _configuration = null!;
    private static string _resourcePrefix;
    private static List<string> bucketNameList = new List<string>();
    private static List<string> fileNameList = new List<string>();

    public static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonS3>()
                    .AddTransient<S3ActionsWrapper>()
            )
            .Build();

        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally, load local settings.
            .Build();

        _resourcePrefix = _configuration["resourcePrefix"] ?? "dotnet-example";

        ServicesSetup(host);

        try
        {
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("Welcome to the Amazon Simple Storage Service (S3) Object Locking Workflow Scenario.");
            Console.WriteLine(new string('-', 80));
            await Setup(true);

            Console.WriteLine(new string('-', 80));
            await DemoActionChoices();
            Console.WriteLine(new string('-', 80));

            Console.WriteLine(new string('-', 80));
            Console.WriteLine("Finally, let's clean up.");
            Console.WriteLine(new string('-', 80));
            await Cleanup(true);

            Console.WriteLine(new string('-', 80));
            Console.WriteLine("Amazon S3 Object Locking Workflow is complete.");
            Console.WriteLine(new string('-', 80));
        }
        catch (Exception ex)
        {
            Console.WriteLine(new string('-', 80));
            Console.WriteLine($"There was a problem: {ex.Message}");
            await Cleanup(true);
            Console.WriteLine(new string('-', 80));
        }
    }

    /// <summary>
    /// Populate the services for use within the console application.
    /// </summary>
    /// <param name="host">The services host.</param>
    private static void ServicesSetup(IHost host)
    {
        _s3ActionsWrapper = host.Services.GetRequiredService<S3ActionsWrapper>();
    }

    // <summary>
    /// Deploy necessary resources for the scenario.
    /// </summary>
    /// <param name="interactive">True to run as interactive.</param>
    /// <returns>True if successful.</returns>
    public static async Task<bool> Setup(bool interactive)
    {
        Console.WriteLine(
            "\nFor this workflow, we will use the AWS SDK for .NET to create several S3\n" +
            "buckets and files to demonstrate working with S3 locking features.\n");

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Press Enter when you are ready to start.");
        if (interactive)
            Console.ReadLine();

        var noLockBucketName = _resourcePrefix + "-no-lock";
        var lockEnabledBucketName = _resourcePrefix + "-lock-enabled";
        var retentionOnCreationBucketName = _resourcePrefix + "-retention-on-creation";
        var retentionAfterCreationBucketName = _resourcePrefix + "-retention-after-creation";

        bucketNameList.Add(noLockBucketName);
        bucketNameList.Add(lockEnabledBucketName);
        bucketNameList.Add(retentionOnCreationBucketName);
        bucketNameList.Add(retentionAfterCreationBucketName);

        Console.WriteLine("\nS3 buckets can be created either with or without object lock enabled.");
        await _s3ActionsWrapper.CreateBucketByName(noLockBucketName, false);
        await _s3ActionsWrapper.CreateBucketByName(lockEnabledBucketName, true);
        await _s3ActionsWrapper.CreateBucketByName(retentionAfterCreationBucketName, false);

        Console.WriteLine("\nA bucket can also have object locking with a default retention period.");
        await _s3ActionsWrapper.CreateBucketByName(retentionOnCreationBucketName, true);

        Console.WriteLine("\nObject lock policies can also be added to existing buckets.");
        await _s3ActionsWrapper.EnableObjectLockOnBucket(lockEnabledBucketName);
        await _s3ActionsWrapper.ModifyBucketDefaultRetention(retentionAfterCreationBucketName, true,
            ObjectLockRetentionMode.Governance, DateTime.UtcNow.AddDays(1));

        // Upload some files to the buckets.
        Console.WriteLine("\nNow let's add some test files:");
        var fileName = _configuration["exampleFileName"] ?? "exampleFile.txt";
        int fileCount = 2;
        // Create the file if it does not already exist.
        if (!File.Exists(fileName))
        {
            await using StreamWriter sw = File.CreateText(fileName);
            await sw.WriteLineAsync(
                "This is a sample file for uploading to a bucket.");
        }

        foreach (var bucketName in bucketNameList)
        {
            for (int i = 0; i < fileCount; i++)
            {
                var numberedFileName = Path.GetFileNameWithoutExtension(fileName)+i+Path.GetExtension(fileName);
                fileNameList.Add(numberedFileName);
                await _s3ActionsWrapper.UploadFileAsync(bucketName, numberedFileName, fileName);
            }
        }

        Console.WriteLine("\nNow we will set some object lock policies on individual files:");
        foreach (var bucketName in bucketNameList)
        {
            for (int i = 0; i < fileNameList.Count; i++)
            {
                // No modifications to the objects in the first bucket.
                if (bucketName != bucketNameList[0])
                {
                    var exampleFileName = fileNameList[i];
                    switch (i)
                    {
                        case 0:
                            {
                                // Set a legal hold.
                                await _s3ActionsWrapper.ModifyObjectLegalHold(bucketName,
                                    exampleFileName,
                                    true);
                                break;
                            }
                        case 1:
                            {
                                // Set a Governance mode retention period for 1 day.
                                await _s3ActionsWrapper.ModifyObjectRetentionPeriod(
                                    bucketName, exampleFileName,
                                    ObjectLockRetentionMode.Governance,
                                    DateTime.UtcNow.AddDays(1));
                                break;
                            }
                    }
                }
            }
        }
        Console.WriteLine(new string('-', 80));
        return true;
    }

    // <summary>
    /// List all of the current buckets and objects.
    /// </summary>
    /// <returns>The list of buckets and objects.</returns>
    public static async Task<List<S3ObjectVersion>> ListBucketsAndObjects()
    {
        Console.WriteLine("\nCurrent buckets and objects:\n");
        var allObjects = new List<S3ObjectVersion>();
        foreach (var bucketName in bucketNameList)
        {
            var objectsInBucket = await _s3ActionsWrapper.ListBucketObjectsAndVersions(bucketName);
            foreach (var objectKey in objectsInBucket.Versions)
            {
                allObjects.Add(objectKey);
            }
        }

        int i = 0;
        foreach (var bucketObject in allObjects)
        {
            i++;
            Console.WriteLine($"{i}: {bucketObject.Key} Delete marker: {bucketObject.IsDeleteMarker}\n\tBucket: {bucketObject.BucketName}\n\tVersion: {bucketObject.VersionId}");
        }

        return allObjects;
    }

    /// <summary>
    /// Present the user with the demo action choices.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task<bool> DemoActionChoices()
    {
        var choices = new string[]{
            "List all files in buckets.",
            "Attempt to delete a file.",
            "Attempt to overwrite a file.",
            "View the object and bucket retention settings for a file.",
            "View the legal hold settings for a file.",
            "Finish the workflow."};

        var choice = 0;
        // Keep asking the user until they choose to move on.
        while (choice != 5)
        {
            choice = GetChoiceResponse(
                "\nExplore the S3 locking features by selecting one of the following choices:"
                , choices);

            switch (choice)
            {
                case 0:
                    {
                        await ListBucketsAndObjects();
                        break;
                    }
                case 1:
                    {
                        Console.WriteLine("Enter the number of the object to delete:");
                        var allFiles = await ListBucketsAndObjects();
                        var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                        await _s3ActionsWrapper.DeleteObjectFromBucket(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key, allFiles[fileChoice].VersionId);
                        break;
                    }
                case 2:
                    {
                        var allFiles = await ListBucketsAndObjects();
                        Console.WriteLine("Enter the number of the object to overwrite:");
                        var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                        // Create the file if it does not already exist.
                        if (!File.Exists(allFiles[fileChoice].Key))
                        {
                            await using StreamWriter sw = File.CreateText(allFiles[fileChoice].Key);
                            await sw.WriteLineAsync(
                                "This is a sample file for uploading to a bucket.");
                        }
                        await _s3ActionsWrapper.UploadFileAsync(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key, allFiles[fileChoice].Key);
                        break;
                    }
                case 3:
                    {
                        var allFiles = await ListBucketsAndObjects();
                        Console.WriteLine("Enter the number of the object and bucket to view:");
                        var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                        await _s3ActionsWrapper.GetObjectRetention(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key);
                        await _s3ActionsWrapper.GetBucketObjectLockConfiguration(allFiles[fileChoice].BucketName);
                        break;
                    }
                case 4:
                    {
                        var allFiles = await ListBucketsAndObjects();
                        Console.WriteLine("Enter the number of the object to view:");
                        var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                        await _s3ActionsWrapper.GetObjectLegalHold(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key);
                        break;
                    }
                default:
                    {
                        Console.WriteLine("Ok, let's finish the workflow.");
                        break;
                    }
            }
        }
        return true;
    }

    // <summary>
    /// Clean up the resources from the scenario.
    /// </summary>
    /// <param name="interactive">True to run as interactive.</param>
    /// <returns>True if successful.</returns>
    public static async Task<bool> Cleanup(bool interactive)
    {
        Console.WriteLine(new string('-', 80));

        if (!interactive || GetYesNoResponse("Do you want to clean up all files and buckets? (y/n) "))
        {
            // Remove all locks and delete all buckets and objects.
            foreach (var bucketName in bucketNameList)
            {
                for (int i = 0; i < fileNameList.Count; i++)
                {
                    var exampleFileName = fileNameList[i];
                    if (bucketName != bucketNameList[0])
                    {
                        switch (i)
                        {
                            case 0:
                                {
                                    await _s3ActionsWrapper.ModifyObjectLegalHold(
                                        bucketName, exampleFileName,
                                        false);
                                    break;
                                }
                        }
                    }

                    await _s3ActionsWrapper.DeleteObjectFromBucket(bucketName,
                        exampleFileName);
                }

                await _s3ActionsWrapper.DeleteBucketByName(bucketName);
            }

        }
        else
        {
            Console.WriteLine(
                "Ok, we'll leave the resources intact.\n" +
                "Don't forget to delete them when you're done with them or you might incur unexpected charges."
            );
        }

        Console.WriteLine(new string('-', 80));
        return true;
    }

    /// <summary>
    /// Helper method to get a yes or no response from the user.
    /// </summary>
    /// <param name="question">The question string to print on the console.</param>
    /// <returns>True if the user responds with a yes.</returns>
    private static bool GetYesNoResponse(string question)
    {
        Console.WriteLine(question);
        var ynResponse = Console.ReadLine();
        var response = ynResponse != null && ynResponse.Equals("y", StringComparison.InvariantCultureIgnoreCase);
        return response;
    }

    /// <summary>
    /// Helper method to get a choice response from the user.
    /// </summary>
    /// <param name="question">The question string to print on the console.</param>
    /// <param name="choices">The choices to print on the console.</param>
    /// <returns>The index of the selected choice</returns>
    private static int GetChoiceResponse(string? question, string[] choices)
    {
        if (question != null)
        {
            Console.WriteLine(question);

            for (int i = 0; i < choices.Length; i++)
            {
                Console.WriteLine($"\t{i + 1}. {choices[i]}");
            }
        }

        var choiceNumber = 0;
        while (choiceNumber < 1 || choiceNumber > choices.Length)
        {
            var choice = Console.ReadLine();
            Int32.TryParse(choice, out choiceNumber);
        }

        return choiceNumber - 1;
    }
}
// snippet-end:[S3LockWorkflow.dotnetv3.ObjectLockWorkflow]