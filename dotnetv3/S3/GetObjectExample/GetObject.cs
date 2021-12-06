// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows how to use the Amazon Simple Storage Service
/// (Amazon S3) client to copy an object from an Amazon S3 bucket to
/// another location such as your local system. The code uses the AWS
/// SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace GetObjectExample
{
    using System;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.S3;
    using Amazon.S3.Model;

    class GetObject
    {
        public static async Task Main()
        {
            const string bucketName = "doc-example-bucket";
            const string keyName = "filetodownload";

            // If the Amazon Region where the Amazon S3 bucket was created is not
            // the same as the region defined for the default user, specify
            // the region as a parameter to the client constructor.
            // For example: RegionEndpoint.USWest2;
            IAmazonS3 client = new AmazonS3Client(RegionEndpoint.USWest2);
            await ReadObjectDataAsync(client, bucketName, keyName);
        }

        /// <summary>
        /// This method copies the contents of the object keyName to another
        /// location, for example, to your local system.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client used to call
        /// GetObjectAsync.</param>
        /// <param name="bucketName">The name of the Amazon S3 bucket which contains
        /// the object to copy.</param>
        /// <param name="keyName">The name of the object you want to copy.</param>
        public static async Task ReadObjectDataAsync(IAmazonS3 client, string bucketName, string keyName)
        {
            // snippet-start:[S3.dotnetv3.GetObjectExample]
            string responseBody = string.Empty;

            try
            {
                GetObjectRequest request = new GetObjectRequest
                {
                    BucketName = bucketName,
                    Key = keyName,
                };

                using (GetObjectResponse response = await client.GetObjectAsync(request))
                using (Stream responseStream = response.ResponseStream)
                using (StreamReader reader = new StreamReader(responseStream))
                {
                    // Assume you have "title" as medata added to the object.
                    string title = response.Metadata["x-amz-meta-title"];
                    string contentType = response.Headers["Content-Type"];

                    Console.WriteLine($"Object metadata, Title: {title}");
                    Console.WriteLine($"Content type: {contentType}");

                    // Retrieve the contents of the file.
                    responseBody = reader.ReadToEnd();

                    // Write the contents of the file to disk.
                    string filePath = $"C:\\Temp\\copy_of_{keyName}";
                }
            }
            catch (AmazonS3Exception e)
            {
                // If the bucket or the object do not exist
                Console.WriteLine($"Error: '{e.Message}'");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.GetObjectExample]
}
