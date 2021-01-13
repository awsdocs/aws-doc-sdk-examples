// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[S3.dotnet35.CopyObject]

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Threading.Tasks;

namespace CopyObject
{
    // This example copies an object from one Amazon Simple Storage Service
    // (Amazon S3) bucket to another. It uses the AWS SDK for .NET 3.5
    // and .NET 5.0.

    public class CopyObject
    {
        // Specify the AWS Region where your buckets are located (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        // Remember to change these values to refer to your own Amazon S3 objects.
        private const string SOURCE_BUCKET_NAME = "doc-example-bucket1";
        private const string DESTINATION_BUCKET_NAME = "doc-example-bucket2";
        private const string SOURCE_OBJ_KEY = "testfile.txt";
        private const string DESTINATION_OBJ_KEY = "testfilecopy.txt";

        static async Task Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);

            Console.WriteLine($"Copying {SOURCE_OBJ_KEY} from {SOURCE_BUCKET_NAME} to ");
            Console.WriteLine($"{DESTINATION_BUCKET_NAME} as {DESTINATION_OBJ_KEY}");

            var response = await CopyingObjectAsync(
                _s3Client,
                SOURCE_OBJ_KEY,
                DESTINATION_OBJ_KEY,
                SOURCE_BUCKET_NAME,
                DESTINATION_BUCKET_NAME);

            if(response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("\nCopy completed.");
            }
        }

        /// <summary>
        /// This method calls the AWS SDK for .NET to copy an
        /// object from one S3 bucket to another.
        /// </summary>
        /// <param name="client">The S3 client object.</param>
        /// <param name="sourceKey">The name of the object to be copied.</param>
        /// <param name="destinationKey">The name under which to save the copy.</param>
        /// <param name="sourceBucketName">The name of the S3 bucket where the
        /// file is located now.</param>
        /// <param name="destinationBucketName">The name of the S3 bucket where
        /// the copy should be saved.</param>
        /// <returns>Returns a CopyObjectResponse object with the results from
        /// the async call.</returns>
        public static async Task<CopyObjectResponse> CopyingObjectAsync
            (IAmazonS3 client, string sourceKey, string destinationKey,
            string sourceBucketName, string destinationBucketName)
        {
            CopyObjectResponse response = new CopyObjectResponse();
            try
            {
                CopyObjectRequest request = new CopyObjectRequest
                {
                    SourceBucket = sourceBucketName,
                    SourceKey = sourceKey,
                    DestinationBucket = destinationBucketName,
                    DestinationKey = destinationKey
                };
                response = await client.CopyObjectAsync(request);
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine($"Error copying object: '{e.Message}'");
            }

            return response;
        }
    }
}
// snippet-end:[S3.dotnet35.CopyObject]