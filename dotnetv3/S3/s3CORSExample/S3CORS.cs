// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/// <summary>
/// This example shows how to configure your Amazon Simple Storage Service
/// (Amazon S3) bucket to allow cross-origin requests by creating a CORS
/// configuration. The CORS configuration is a document with rules that
/// identify the origins that you will allow to access your bucket, the
/// operations (HTTP methods) supported for each origin, and other operation-
/// specific information. This example was created using the AWS SDK for
/// .NET 3.7 and .NET Core 5.0.
/// </summary>
namespace S3CORSExample
{
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class S3CORS
    {
        // Remember to change the bucket name to the name of an Amazon Simple
        // Storage Service (Amazon S3) bucket that exists on your account.
        private const string BucketName = "doc-example-bucket";

        public static async Task Main()
        {
            // Change the region endpoint to the AWS Region used to create
            // the Amazon S3 bucket.
            var s3Client = new AmazonS3Client(RegionEndpoint.USWest2);
            await CORSConfigTestAsync(s3Client);
        }

        /// <summary>
        /// Create the Amazon S3 CORS configuration.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used
        /// to create the CORS configuration</param>
        private static async Task CORSConfigTestAsync(AmazonS3Client client)
        {
            try
            {
                // Create a new configuration request and add two rules.
                CORSConfiguration configuration = new()
                {
                    Rules = new List<CORSRule>
                        {
                          new CORSRule
                          {
                            Id = "CORSRule1",
                            AllowedMethods = new List<string> { "PUT", "POST", "DELETE" },
                            AllowedOrigins = new List<string> { "http://*.example.com" },
                          },
                          new CORSRule
                          {
                            Id = "CORSRule2",
                            AllowedMethods = new List<string> { "GET" },
                            AllowedOrigins = new List<string> { "*" },
                            MaxAgeSeconds = 3000,
                            ExposeHeaders = new List<string> { "x-amz-server-side-encryption" },
                          },
                        },
                };

                await PutCORSConfigurationAsync(client, configuration);

                configuration = await RetrieveCORSConfigurationAsync(client);

                // Add a new rule.
                configuration.Rules.Add(new CORSRule
                {
                    Id = "CORSRule3",
                    AllowedMethods = new List<string> { "HEAD" },
                    AllowedOrigins = new List<string> { "http://www.example.com" },
                });

                await PutCORSConfigurationAsync(client, configuration);

                // Verify that there are now three rules.
                configuration = await RetrieveCORSConfigurationAsync(client);
                Console.WriteLine();
                Console.WriteLine("Expected # of rulest=3; found:{0}", configuration.Rules.Count);
                Console.WriteLine();
                Console.WriteLine("Pause before configuration delete. To continue, click Enter...");
                Console.ReadKey();

                // Delete the configuration.
                await DeleteCORSConfigurationAsync(client);

                // Retrieve a nonexistent configuration.
                configuration = await RetrieveCORSConfigurationAsync(client);
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine("Error encountered on server. Message:'{0}' when writing an object", e.Message);
            }
            catch (Exception e)
            {
                Console.WriteLine("Unknown encountered on server. Message:'{0}' when writing an object", e.Message);
            }
        }

        // snippet-start:[S3.dotnetv3.PutCORS]

        /// <summary>
        /// Add CORS configuration to the Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used
        /// to apply the CORS configuration to an Amazon S3 bucket.</param>
        /// <param name="configuration">The CORS configuration to apply.</param>
        private static async Task PutCORSConfigurationAsync(AmazonS3Client client, CORSConfiguration configuration)
        {
            PutCORSConfigurationRequest request = new()
            {
                BucketName = BucketName,
                Configuration = configuration,
            };

            _ = await client.PutCORSConfigurationAsync(request);
        }

        // snippet-end:[S3.dotnetv3.PutCORS]

        // snippet-start:[S3.dotnetv3.GetCORS]

        /// <summary>
        /// Retrieve the CORS configuration applied to the Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used
        /// to retrieve the CORS configuration.</param>
        /// <returns>The created CORS configuration object.</returns>
        private static async Task<CORSConfiguration> RetrieveCORSConfigurationAsync(AmazonS3Client client)
        {
            GetCORSConfigurationRequest request = new()
            {
                BucketName = BucketName,
            };
            var response = await client.GetCORSConfigurationAsync(request);
            var configuration = response.Configuration;
            PrintCORSRules(configuration);
            return configuration;
        }

        // snippet-end:[S3.dotnetv3.GetCORS]

        // snippet-start:[S3.dotnetv3.DeleteCORS]

        /// <summary>
        /// Deletes a CORS configuration from an Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used
        /// to delete the CORS configuration from the bucket.</param>
        private static async Task DeleteCORSConfigurationAsync(AmazonS3Client client)
        {
            DeleteCORSConfigurationRequest request = new()
            {
                BucketName = BucketName,
            };
            await client.DeleteCORSConfigurationAsync(request);
        }

        // snippet-end:[S3.dotnetv3.DeleteCORS]

        /// <summary>
        /// Displays the list of CORS rules on the console.
        /// </summary>
        /// <param name="configuration">The list of CORS rules to display.</param>
        private static void PrintCORSRules(CORSConfiguration configuration)
        {
            Console.WriteLine();

            if (configuration is null)
            {
                Console.WriteLine("\nConfiguration is null");
                return;
            }

            Console.WriteLine($"Configuration has {configuration.Rules.Count} rules:");
            foreach (CORSRule rule in configuration.Rules)
            {
                Console.WriteLine($"Rule ID: {rule.Id}");
                Console.WriteLine($"MaxAgeSeconds: {rule.MaxAgeSeconds}");
                Console.WriteLine($"AllowedMethod: {string.Join(", ", rule.AllowedMethods.ToArray())}");
                Console.WriteLine($"AllowedOrigins: {string.Join(", ", rule.AllowedOrigins.ToArray())}");
                Console.WriteLine($"AllowedHeaders: {string.Join(", ", rule.AllowedHeaders.ToArray())}");
                Console.WriteLine($"ExposeHeader: {string.Join(", ", rule.ExposeHeaders.ToArray())}");
            }
        }
    }
}
