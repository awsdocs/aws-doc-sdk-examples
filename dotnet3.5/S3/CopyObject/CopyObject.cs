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
    public class CopyObject
    {
        // Specify your bucket region (an example region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        // Specify the names for the Simple Storage Service objects
        private const string SOURCE_BUCKET_NAME = "*** provide the name of the bucket with source object ***";
        private const string DESTINATION_BUCKET_NAME = "*** provide the name of the bucket to copy the object to ***";
        private const string SOURCE_OBJ_KEY = "*** provide the name of object to copy ***";
        private const string DESTINATION_OBJ_KEY = "*** provide the destination object key name ***";

        static void Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);

            Console.WriteLine($"Copying {SOURCE_OBJ_KEY} from {SOURCE_BUCKET_NAME} to ");
            Console.WriteLine($"{DESTINATION_BUCKET_NAME} as {DESTINATION_OBJ_KEY}");

            var response = CopyingObjectAsync(
                _s3Client,
                SOURCE_OBJ_KEY,
                DESTINATION_OBJ_KEY,
                SOURCE_BUCKET_NAME,
                DESTINATION_BUCKET_NAME);

            Console.WriteLine("Copy complete!");

        }

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
                return response;
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine($"Error encountered on server. Message:'{e.Message}'");
            }

            return response;
        }
    }
}
// snippet-end:[S3.dotnet35.CopyObject]