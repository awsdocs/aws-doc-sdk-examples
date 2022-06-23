// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example gets a security token to access the contents of an
/// Amazon Simple Storage Service (Amazon S3) bucket. The example was
/// created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace TempFederatedCredentialsExample
{
    // snippet-start:[S3.dotnetv3.TempFederatedCredentialsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.Runtime;
    using Amazon.S3;
    using Amazon.S3.Model;
    using Amazon.SecurityToken;
    using Amazon.SecurityToken.Model;

    public class TempFederatedCredentials
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";

            // Change the example region below to the region where
            // your bucket is located.
            RegionEndpoint bucketRegion = RegionEndpoint.USWest2;

            await ListObjectsAsync(bucketRegion, bucketName);
        }

        /// <summary>
        /// Lists the objects in an Amazon S3 bucket using temporary federated
        /// credentials.
        /// </summary>
        /// <param name="bucketRegion">The AWS Region where the Amazon S3 bucket is
        /// located.</param>
        /// <param name="bucketName">The name of the Amazon S3 bucket whose objects we
        /// will list.</param>
        public static async Task ListObjectsAsync(RegionEndpoint bucketRegion, string bucketName)
        {
            try
            {
                Console.WriteLine("Listing objects stored in a bucket");

                // Credentials use the default AWS SDK for .NET credential search chain.
                // On local development machines, this is your default profile.
                SessionAWSCredentials tempCredentials =
                    await GetTemporaryFederatedCredentialsAsync(bucketName);

                // Create a client by providing temporary security credentials.
                using var client = new AmazonS3Client(bucketRegion);
                ListObjectsRequest listObjectRequest = new();
                listObjectRequest.BucketName = bucketName;

                ListObjectsResponse response = await client.ListObjectsAsync(listObjectRequest);
                List<S3Object> objects = response.S3Objects;
                Console.WriteLine($"Object count = {objects.Count}", objects.Count);

                Console.WriteLine("Press any key to continue...");
                Console.ReadKey();
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine($"Error: {e.Message}");
            }
        }

        public static async Task<SessionAWSCredentials> GetTemporaryFederatedCredentialsAsync(string bucketName)
        {
            var config = new AmazonSecurityTokenServiceConfig();
            var stsClient = new AmazonSecurityTokenServiceClient(config);

            var federationTokenRequest = new GetFederationTokenRequest();
            federationTokenRequest.DurationSeconds = 7200;
            federationTokenRequest.Name = "User1";
            federationTokenRequest.Policy = @"{
               ""Statement"":
               [
                 {
                   ""Sid"":""Stmt1311212314284"",
                   ""Action"":[""s3:ListBucket""],
                   ""Effect"":""Allow"",
                   ""Resource"":""arn:aws:s3:::" + bucketName + @"""
                  }
               ]
             }
            ";

            GetFederationTokenResponse federationTokenResponse =
                        await stsClient.GetFederationTokenAsync(federationTokenRequest);
            Credentials credentials = federationTokenResponse.Credentials;

            var sessionCredentials = new SessionAWSCredentials(
                credentials.AccessKeyId,
                credentials.SecretAccessKey,
                credentials.SessionToken);

            return sessionCredentials;
        }
    }

    // snippet-end:[S3.dotnetv3.TempFederatedCredentialsExample]
}
