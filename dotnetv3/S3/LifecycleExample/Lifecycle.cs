// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows how to work with Amazon Simple Storage Service
/// (Amazon S3) bucket lifecycle settings. It was created with the AWS SDK
/// for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace LifecycleExample
{
    // snippet-start:[S3.dotnetv3.LifecycleExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class Lifecycle
    {
        public static async Task Main()
        {
            // If the AWS Region associated with the default user is different
            // from the region where the Amazon S3 bucket is located, pass the
            // bucket region to the AmaS3Client constructor. The parameter
            // should look like this:
            //      RegionEndpoint.USWest2
            var client = new AmazonS3Client();
            const string BucketName = "doc-example-bucket";

            await AddUpdateDeleteLifecycleConfigAsync(client, BucketName);
        }

        /// <summary>
        /// This method creates, adds, and then removes lifecycle information
        /// to the S3 bucket named in the bucketName parameter.
        /// </summary>
        /// <param name="client">An S3 client object used to call methods
        /// to add, check, and update configuration settings for the supplied
        /// S3 bucket.</param>
        /// <param name="bucketName">A string representing the name of the
        /// bucket to add configuration settings.</param>
        public static async Task AddUpdateDeleteLifecycleConfigAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                var lifeCycleConfiguration = new LifecycleConfiguration()
                {
                    Rules = new List<LifecycleRule>
                    {
                        new LifecycleRule
                        {
                            Id = "Archive immediately rule",
                            Filter = new LifecycleFilter()
                            {
                                LifecycleFilterPredicate = new LifecyclePrefixPredicate()
                                {
                                    Prefix = "glacierobjects/",
                                },
                            },
                            Status = LifecycleRuleStatus.Enabled,
                            Transitions = new List<LifecycleTransition>
                            {
                                new LifecycleTransition
                                {
                                    Days = 0,
                                    StorageClass = S3StorageClass.Glacier,
                                },
                            },
                        },
                        new LifecycleRule
                        {
                            Id = "Archive and then delete rule",
                            Filter = new LifecycleFilter()
                            {
                                LifecycleFilterPredicate = new LifecyclePrefixPredicate()
                                {
                                    Prefix = "projectdocs/",
                                },
                            },
                            Status = LifecycleRuleStatus.Enabled,
                            Transitions = new List<LifecycleTransition>
                            {
                                new LifecycleTransition
                                {
                                    Days = 30,
                                    StorageClass = S3StorageClass.StandardInfrequentAccess,
                                },
                                new LifecycleTransition
                                {
                                    Days = 365,
                                    StorageClass = S3StorageClass.Glacier,
                                },
                            },
                            Expiration = new LifecycleRuleExpiration()
                            {
                                Days = 3650,
                            },
                        },
                    },
                };

                // Add the configuration to the bucket.
                await AddExampleLifecycleConfigAsync(client, bucketName, lifeCycleConfiguration);

                // Retrieve the existing configuration to show that the configuration
                // was added.
                lifeCycleConfiguration = await RetrieveLifecycleConfigAsync(client, bucketName);

                // Add another rule to the existing lifecycle rule.
                lifeCycleConfiguration.Rules.Add(new LifecycleRule
                {
                    Id = "NewRule",
                    Filter = new LifecycleFilter()
                    {
                        LifecycleFilterPredicate = new LifecyclePrefixPredicate()
                        {
                            Prefix = "YearlyDocuments/",
                        },
                    },
                    Expiration = new LifecycleRuleExpiration()
                    {
                        Days = 3650,
                    },
                });

                // Add the lifecycle configuration to the S3 bucket.
                await AddExampleLifecycleConfigAsync(client, bucketName, lifeCycleConfiguration);

                // Verify that the the bucket now has three rules in its lifecycle configuration.
                lifeCycleConfiguration = await RetrieveLifecycleConfigAsync(client, bucketName);
                Console.WriteLine($"Expected # of rulest: 3; # of rules found: {lifeCycleConfiguration.Rules.Count}");

                // Now delete the configuration from the S3 bucket.
                await RemoveLifecycleConfigAsync(client, bucketName);

                // Show that the lifecycle configuration is no longer associated with
                // the S3 bucket.
                lifeCycleConfiguration = await RetrieveLifecycleConfigAsync(client, bucketName);
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error encountered ***. Message:'{ex.Message}' when writing an object.");
            }
        }

        // snippet-start:[S3.dotnetv3.PutLifecycleExample]

        /// <summary>
        /// Adds lifecycle configuration information to the S3 bucket named in
        /// the bucketName parameter.
        /// </summary>
        /// <param name="client">The S3 client used to call the
        /// PutLifecycleConfigurationAsync method.</param>
        /// <param name="bucketName">A string representing the S3 bucket to
        /// which configuration information will be added.</param>
        /// <param name="configuration">A LifecycleConfiguration object that
        /// will be applied to the S3 bucket.</param>
        public static async Task AddExampleLifecycleConfigAsync(IAmazonS3 client, string bucketName, LifecycleConfiguration configuration)
        {
            var request = new PutLifecycleConfigurationRequest()
            {
                BucketName = bucketName,
                Configuration = configuration,
            };
            var response = await client.PutLifecycleConfigurationAsync(request);
        }

        // snippet-end:[S3.dotnetv3.PutLifecycleExample]

        // snippet-start:[S3.dotnetv3.GetLifecycleExample]

        /// <summary>
        /// Returns a configuration object for the supplied bucket name.
        /// </summary>
        /// <param name="client">The S3 client object used to call
        /// the GetLifecycleConfigurationAsync method.</param>
        /// <param name="bucketName">The name of the S3 bucket for which a
        /// configuration will be created.</param>
        /// <returns>Returns a new LifecycleConfiguration object.</returns>
        public static async Task<LifecycleConfiguration> RetrieveLifecycleConfigAsync(IAmazonS3 client, string bucketName)
        {
            var request = new GetLifecycleConfigurationRequest()
            {
                BucketName = bucketName,
            };
            var response = await client.GetLifecycleConfigurationAsync(request);
            var configuration = response.Configuration;
            return configuration;
        }

        // snippet-end:[S3.dotnetv3.GetLifecycleExample]

        // snippet-start:[S3.dotnetv3.DeleteLifecycleExample]

        /// <summary>
        /// This method removes the Lifecycle configuration from the named
        /// S3 bucket.
        /// </summary>
        /// <param name="client">The S3 client object used to call
        /// the RemoveLifecycleConfigAsync method.</param>
        /// <param name="bucketName">A string representing the name of the
        /// S3 bucket from which the configuration will be removed.</param>
        public static async Task RemoveLifecycleConfigAsync(IAmazonS3 client, string bucketName)
        {
            var request = new DeleteLifecycleConfigurationRequest()
            {
                BucketName = bucketName,
            };
            await client.DeleteLifecycleConfigurationAsync(request);
        }

        // snippet-end:[S3.dotnetv3.DeleteLifecycleExample]
    }

    // snippet-end:[S3.dotnetv3.LifecycleExample]
}
