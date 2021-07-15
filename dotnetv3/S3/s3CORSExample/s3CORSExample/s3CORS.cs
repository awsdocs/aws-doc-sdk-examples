// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace S3CORSExample
{
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.S3;
    using Amazon.S3.Model;

    /// <summary>
    /// This example shows how to configure your bucket to allow cross-origin
    /// requests by creating a CORS configuration. The CORS configuration is a
    /// document with rules that identify the origins that you will allow to
    /// access your bucket, the operations (HTTP methods) supported for each
    /// origin, and other operation-specific information. This example was
    /// created using the AWS SDK for .NET 3.7 and .NET Core 5.0.
    /// </summary>
    public class S3CORS
    {
        // Remember to change the bucket name to the name of an Amazon Simple
        // Storage Service (Amazon S3) bucket that exists on your account.
        private const string BucketName = "doc-example-bucket";

        /// <summary>
        /// The Main method creates the the bucket to be able to accept CORS
        /// requests.
        /// </summary>
        public static async Task Main()
        {
            // Change the region endpoint to the region used to create
            // the Amazon S3 bucket.
            var s3Client = new AmazonS3Client(RegionEndpoint.USWest2);
            await CORSConfigTestAsync(s3Client);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="client"></param>
        /// <returns></returns>
        private static async Task CORSConfigTestAsync(AmazonS3Client client)
        {
            try
            {
                // Create a new configuration request and add two rules.
                CORSConfiguration configuration = new ()
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

        /// <summary>
        /// 
        /// </summary>
        /// <param name="client"></param>
        /// <param name="configuration"></param>
        /// <returns></returns>
        private static async Task PutCORSConfigurationAsync(AmazonS3Client client, CORSConfiguration configuration)
        {
            PutCORSConfigurationRequest request = new ()
            {
                BucketName = BucketName,
                Configuration = configuration,
            };

            _ = await client.PutCORSConfigurationAsync(request);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="client"></param>
        /// <returns></returns>
        private static async Task<CORSConfiguration> RetrieveCORSConfigurationAsync(AmazonS3Client client)
        {
            GetCORSConfigurationRequest request = new ()
            {
                BucketName = BucketName,
            };
            var response = await client.GetCORSConfigurationAsync(request);
            var configuration = response.Configuration;
            PrintCORSRules(configuration);
            return configuration;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="client"></param>
        /// <returns></returns>
        private static async Task DeleteCORSConfigurationAsync(AmazonS3Client client)
        {
            DeleteCORSConfigurationRequest request = new ()
            {
                BucketName = BucketName,
            };
            await client.DeleteCORSConfigurationAsync(request);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="configuration"></param>
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
