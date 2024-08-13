---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_01FUuJxLQQ8UuEUVjKMUmJYo
  lastRun: 2024-06-27T13:45:45.826Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 1602
    inputTokenCount: 25542
    invocationLatency: 10761
    outputTokenCount: 866
prompt: |
  Describe the architecture and structure of Program.cs, which will run the interactive scenario in the console window. Each method should be public static and include summary and parameter xmldoc comment blocks. Each method should return Async Task<bool> true if successful, false if there are any failures or exceptions.

  Use the following instructions for .NET coding standards: {{code.standards}} 

  Here is an example Program for a different scenario.

  <example>

    public static class S3ObjectLockWorkflow
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

        public static S3ActionsWrapper _s3ActionsWrapper = null!;
        public static IConfiguration _configuration = null!;
        private static string _resourcePrefix = null!;
        private static string noLockBucketName = null!;
        private static string lockEnabledBucketName = null!;
        private static string retentionAfterCreationBucketName = null!;
        private static List<string> bucketNames = new List<string>();
        private static List<string> fileNames = new List<string>();

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

            ConfigurationSetup();

            ServicesSetup(host);

            try
            {
                Console.WriteLine(new string('-', 80));
                Console.WriteLine("Welcome to the Amazon Simple Storage Service (S3) Object Locking Workflow Scenario.");
                Console.WriteLine(new string('-', 80));
                await Setup(true);

                await DemoActionChoices();

                Console.WriteLine(new string('-', 80));
                Console.WriteLine("Cleaning up resources.");
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

        /// <summary>
        /// Any setup operations needed.
        /// </summary>
        public static void ConfigurationSetup()
        {
            _resourcePrefix = _configuration["resourcePrefix"] ?? "dotnet-example";

            noLockBucketName = _resourcePrefix + "-no-lock";
            lockEnabledBucketName = _resourcePrefix + "-lock-enabled";
            retentionAfterCreationBucketName = _resourcePrefix + "-retention-after-creation";

            bucketNames.Add(noLockBucketName);
            bucketNames.Add(lockEnabledBucketName);
            bucketNames.Add(retentionAfterCreationBucketName);
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

            Console.WriteLine("\nS3 buckets can be created either with or without object lock enabled.");
            await _s3ActionsWrapper.CreateBucketWithObjectLock(noLockBucketName, false);
            await _s3ActionsWrapper.CreateBucketWithObjectLock(lockEnabledBucketName, true);
            await _s3ActionsWrapper.CreateBucketWithObjectLock(retentionAfterCreationBucketName, false);

            Console.WriteLine("Press Enter to continue.");
            if (interactive)
                Console.ReadLine();

            Console.WriteLine("\nA bucket can be configured to use object locking with a default retention period.");
            await _s3ActionsWrapper.ModifyBucketDefaultRetention(retentionAfterCreationBucketName, true,
                ObjectLockRetentionMode.Governance, DateTime.UtcNow.AddDays(1));

            Console.WriteLine("Press Enter to continue.");
            if (interactive)
                Console.ReadLine();

            Console.WriteLine("\nObject lock policies can also be added to existing buckets.");
            await _s3ActionsWrapper.EnableObjectLockOnBucket(lockEnabledBucketName);
            
            Console.WriteLine("Press Enter to continue.");
            if (interactive)
                Console.ReadLine();

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

            foreach (var bucketName in bucketNames)
            {
                for (int i = 0; i < fileCount; i++)
                {
                    var numberedFileName = Path.GetFileNameWithoutExtension(fileName) + i + Path.GetExtension(fileName);
                    fileNames.Add(numberedFileName);
                    await _s3ActionsWrapper.UploadFileAsync(bucketName, numberedFileName, fileName);
                }
            }
            Console.WriteLine("Press Enter to continue.");
            if (interactive)
                Console.ReadLine();

            if (!interactive)
                return true;
            Console.WriteLine("\nNow we can set some object lock policies on individual files:");
            foreach (var bucketName in bucketNames)
            {
                for (int i = 0; i < fileNames.Count; i++)
                {
                    // No modifications to the objects in the first bucket.
                    if (bucketName != bucketNames[0])
                    {
                        var exampleFileName = fileNames[i];
                        switch (i)
                        {
                            case 0:
                                {
                                    var question =
                                        $"\nWould you like to add a legal hold to {exampleFileName} in {bucketName}? (y/n)";
                                    if (GetYesNoResponse(question))
                                    {
                                        // Set a legal hold.
                                        await _s3ActionsWrapper.ModifyObjectLegalHold(bucketName, exampleFileName, ObjectLockLegalHoldStatus.On);

                                    }
                                    break;
                                }
                            case 1:
                                {
                                    var question =
                                        $"\nWould you like to add a 1 day Governance retention period to {exampleFileName} in {bucketName}? (y/n)" +
                                        "\nReminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.";
                                    if (GetYesNoResponse(question))
                                    {
                                        // Set a Governance mode retention period for 1 day.
                                        await _s3ActionsWrapper.ModifyObjectRetentionPeriod(
                                            bucketName, exampleFileName,
                                            ObjectLockRetentionMode.Governance,
                                            DateTime.UtcNow.AddDays(1));
                                    }
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
        /// <param name="interactive">True to run as interactive.</param>
        /// <returns>The list of buckets and objects.</returns>
        public static async Task<List<S3ObjectVersion>> ListBucketsAndObjects(bool interactive)
        {
            var allObjects = new List<S3ObjectVersion>();
            foreach (var bucketName in bucketNames)
            {
                var objectsInBucket = await _s3ActionsWrapper.ListBucketObjectsAndVersions(bucketName);
                foreach (var objectKey in objectsInBucket.Versions)
                {
                    allObjects.Add(objectKey);
                }
            }

            if (interactive)
            {
                Console.WriteLine("\nCurrent buckets and objects:\n");
                int i = 0;
                foreach (var bucketObject in allObjects)
                {
                    i++;
                    Console.WriteLine(
                        $"{i}: {bucketObject.Key} \n\tBucket: {bucketObject.BucketName}\n\tVersion: {bucketObject.VersionId}");
                }
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
                "Attempt to delete a file with retention period bypass.",
                "Attempt to overwrite a file.",
                "View the object and bucket retention settings for a file.",
                "View the legal hold settings for a file.",
                "Finish the workflow."};

            var choice = 0;
            // Keep asking the user until they choose to move on.
            while (choice != 6)
            {
                Console.WriteLine(new string('-', 80));
                choice = GetChoiceResponse(
                    "\nExplore the S3 locking features by selecting one of the following choices:"
                    , choices);
                Console.WriteLine(new string('-', 80));
                switch (choice)
                {
                    case 0:
                        {
                            await ListBucketsAndObjects(true);
                            break;
                        }
                    case 1:
                        {
                            Console.WriteLine("\nEnter the number of the object to delete:");
                            var allFiles = await ListBucketsAndObjects(true);
                            var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                            await _s3ActionsWrapper.DeleteObjectFromBucket(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key, false, allFiles[fileChoice].VersionId);
                            break;
                        }
                    case 2:
                        {
                            Console.WriteLine("\nEnter the number of the object to delete:");
                            var allFiles = await ListBucketsAndObjects(true);
                            var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                            await _s3ActionsWrapper.DeleteObjectFromBucket(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key, true, allFiles[fileChoice].VersionId);
                            break;
                        }
                    case 3:
                        {
                            var allFiles = await ListBucketsAndObjects(true);
                            Console.WriteLine("\nEnter the number of the object to overwrite:");
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
                    case 4:
                        {
                            var allFiles = await ListBucketsAndObjects(true);
                            Console.WriteLine("\nEnter the number of the object and bucket to view:");
                            var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                            await _s3ActionsWrapper.GetObjectRetention(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key);
                            await _s3ActionsWrapper.GetBucketObjectLockConfiguration(allFiles[fileChoice].BucketName);
                            break;
                        }
                    case 5:
                        {
                            var allFiles = await ListBucketsAndObjects(true);
                            Console.WriteLine("\nEnter the number of the object to view:");
                            var fileChoice = GetChoiceResponse(null, allFiles.Select(f => f.Key).ToArray());
                            await _s3ActionsWrapper.GetObjectLegalHold(allFiles[fileChoice].BucketName, allFiles[fileChoice].Key);
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
                var allFiles = await ListBucketsAndObjects(false);
                foreach (var fileInfo in allFiles)
                {
                    // Check for a legal hold.
                    var legalHold = await _s3ActionsWrapper.GetObjectLegalHold(fileInfo.BucketName, fileInfo.Key);
                    if (legalHold?.Status?.Value == ObjectLockLegalHoldStatus.On)
                    {
                        await _s3ActionsWrapper.ModifyObjectLegalHold(fileInfo.BucketName, fileInfo.Key, ObjectLockLegalHoldStatus.Off);
                    }

                    // Check for a retention period.
                    var retention = await _s3ActionsWrapper.GetObjectRetention(fileInfo.BucketName, fileInfo.Key);
                    var hasRetentionPeriod = retention?.Mode == ObjectLockRetentionMode.Governance && retention.RetainUntilDate > DateTime.UtcNow.Date;
                    await _s3ActionsWrapper.DeleteObjectFromBucket(fileInfo.BucketName, fileInfo.Key, hasRetentionPeriod, fileInfo.VersionId);
                }

                foreach (var bucketName in bucketNames)
                {
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
  </example>

  Do not provide any implementation, include a stub for each necessary method. Use async methods that will call wrapper methods. The method stubs provided should include PrepareApplication to create the email identity and contact list, GatherSubscriberEmailAddresses to get a base email and create variants using ++ses-weekly-newsletter and send a welcome email to each address, SendCouponNewsletter to send the coupon newsletter to the subscribers, MonitorAndReview to provide instructions for monitoring sending activity, and Cleanup to delete the contact list email template, and email identity. Also include the main function which will execute the workflow in order.
---
Here's the Program.cs file with the necessary method stubs for the EventBridge Scheduler workflow:

```csharp
using System;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace Scenarios
{
    public class Program
    {
        private static ILogger<Program> _logger;
        private static SchedulerWrapper _schedulerWrapper;

        public static async Task Main(string[] args)
        {
            await PrepareApplication();
            await CreateOneTimeSchedule();
            await CreateRecurringSchedule();
            await Cleanup();
        }

        /// <summary>
        /// Prepares the application by creating the necessary resources.
        /// </summary>
        /// <returns>True if the application was prepared successfully.</returns>
        public static async Task<bool> PrepareApplication()
        {
            // Prompt the user for an email address and stack name
            // Deploy the CloudFormation stack
            // Store the stack outputs
            // Create a schedule group

            return true;
        }

        /// <summary>
        /// Creates a one-time schedule to send an initial event on the new EventBus.
        /// </summary>
        /// <returns>True if the one-time schedule was created successfully.</returns>
        public static async Task<bool> CreateOneTimeSchedule()
        {
            // Create a one-time schedule with a flexible time window
            // Set the schedule to delete after completion
            // Print the URL for the user to view logs

            return true;
        }

        /// <summary>
        /// Creates a recurring schedule to send events X times per Y hours.
        /// </summary>
        /// <returns>True if the recurring schedule was created successfully.</returns>
        public static async Task<bool> CreateRecurringSchedule()
        {
            // Prompt the user for how many times per hour and for how many hours
            // Create the recurring schedule
            // Print the URL for the user to view logs
            // Delete the schedule when the user is finished

            return true;
        }

        /// <summary>
        /// Cleans up the resources created during the workflow.
        /// </summary>
        /// <returns>True if the cleanup was successful.</returns>
        public static async Task<bool> Cleanup()
        {
            // Prompt the user to confirm cleanup
            // Delete the schedule group
            // Destroy the CloudFormation stack and wait for it to be removed

            return true;
        }
    }
}
```

Here's a breakdown of the changes:

1. The `Program` class now contains the necessary methods for the EventBridge Scheduler workflow:
   - `PrepareApplication`: Prompts the user for input, deploys the CloudFormation stack, and creates a schedule group.
   - `CreateOneTimeSchedule`: Creates a one-time schedule with a flexible time window and prints the URL for the user to view logs.
   - `CreateRecurringSchedule`: Prompts the user for the recurring schedule details, creates the schedule, prints the URL, and deletes the schedule when the user is finished.
   - `Cleanup`: Prompts the user to confirm cleanup, deletes the schedule group, and destroys the CloudFormation stack.
2. Each method is marked as `public static async Task<bool>`, indicating that they are asynchronous and return a boolean value indicating success or failure.
3. The `Main` method is the entry point of the application and calls the other workflow methods in the appropriate order.

The implementation of these methods should use the `SchedulerWrapper` class to interact with the EventBridge Scheduler service and perform the necessary actions. The `SchedulerWrapper` class should provide the necessary methods for creating, deleting, and managing schedules and schedule groups.