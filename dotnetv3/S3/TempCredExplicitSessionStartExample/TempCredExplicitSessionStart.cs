// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace TempCredExplicitSessionStartExample
{
    // snippet-start:[S3.dotnetv3.TempCredExplicitSessionStartExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.Runtime;
    using Amazon.S3;
    using Amazon.S3.Model;
    using Amazon.SecurityToken;
    using Amazon.SecurityToken.Model;

    /// <summary>
    /// This example shows how to use temporary credentials to access an Amazon
    /// Simple Storage Service (Amazon S3) bucket. The example was created
    /// using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class TempCredExplicitSessionStart
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            RegionEndpoint bucketRegion = RegionEndpoint.USEast2;

            // Create the temporary credentials.
            var credentials = await GetTemporaryCredentialsAsync();

            // Now list the objects in the bucket using the temporary credentials.
            await ListObjectsAsync(credentials, bucketRegion, bucketName);
        }

        /// <summary>
        /// Gets a session token from the Amazon Security Token Service.
        /// </summary>
        /// <returns>The requested Session Credentials.</returns>
        public static async Task<SessionAWSCredentials> GetTemporaryCredentialsAsync()
        {
            using var stsClient = new AmazonSecurityTokenServiceClient();
            var getSessionTokenRequest = new GetSessionTokenRequest
            {
                DurationSeconds = 7200,
            };

            GetSessionTokenResponse sessionTokenResponse =
                          await stsClient.GetSessionTokenAsync(getSessionTokenRequest);

            Credentials credentials = sessionTokenResponse.Credentials;

            var sessionCredentials =
                new SessionAWSCredentials(
                    credentials.AccessKeyId,
                    credentials.SecretAccessKey,
                    credentials.SessionToken);

            return sessionCredentials;
        }

        /// <summary>
        /// Uses temporary credentials to list the objects in an Amazon S3
        /// bucket.
        /// </summary>
        /// <param name="credentials">Temporary AWS Credentials.</param>
        /// <param name="bucketRegion">The Region where the bucket is located.</param>
        /// <param name="bucketName">The name of the bucket to access.</param>
        public static async Task ListObjectsAsync(SessionAWSCredentials credentials, RegionEndpoint bucketRegion, string bucketName)
        {
            try
            {
                // Credentials use the default AWS SDK for .NET credential search chain.
                // On local development machines, this is your default profile.
                Console.WriteLine("Listing objects stored in a bucket");

                // Create a client by providing temporary security credentials.
                using var client = new AmazonS3Client(credentials, bucketRegion);
                var listObjectRequest = new ListObjectsRequest
                {
                    BucketName = bucketName,
                };

                // Send request to Amazon S3.
                ListObjectsResponse response = await client.ListObjectsAsync(listObjectRequest);
                List<S3Object> objects = response.S3Objects;
                Console.WriteLine("Object count = {0}", objects.Count);
            }
            catch (AmazonS3Exception s3Exception)
            {
                Console.WriteLine(s3Exception.Message, s3Exception.InnerException);
            }
            catch (AmazonSecurityTokenServiceException stsException)
            {
                Console.WriteLine(stsException.Message, stsException.InnerException);
            }
        }
    }

    // snippet-end:[S3.dotnetv3.TempCredExplicitSessionStartExample]
}
