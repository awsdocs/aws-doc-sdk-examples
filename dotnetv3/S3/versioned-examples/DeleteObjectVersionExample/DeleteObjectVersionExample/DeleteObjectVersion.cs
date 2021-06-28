// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Threading.Tasks;

namespace DeleteObjectVersionExample
{
    class DeleteObjectVersion
    {
        static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            string keyName = "example.txt";

            // Specify your bucket region (an example region is shown).
            RegionEndpoint bucketRegion = RegionEndpoint.USWest2;
            IAmazonS3 client = new AmazonS3Client();

            // Add a sample object which has a version.
            var versionID = await PutAnObject(client, bucketName, keyName);

            // Now that the object has been created, we will delete the version.
            await DeleteObjectVersionAsync(client, bucketName, keyName, versionID);
        }

        /// <summary>
        /// Removes the temporary version created by the call to PutAnObject.
        /// </summary>
        /// <param name="client">The initialized S3 client object used to delete
        /// the object version.</param>
        /// <param name="bucketName">The name of the bucket where the versioned
        /// object is stored.</param>
        /// <param name="keyName">The name of the object for which we will
        /// delete a version.</param>
        /// <param name="versionID">The ID of the version to delete.</param>
        static async Task DeleteObjectVersionAsync(IAmazonS3 client, string bucketName, string keyName, string versionID)
        {
            try
            {
                // Delete the object by specifying an object key and a version ID.
                DeleteObjectRequest request = new DeleteObjectRequest
                {
                    BucketName = bucketName,
                    Key = keyName,
                    VersionId = versionID,
                };
                Console.WriteLine("Deleting an object");
                await client.DeleteObjectAsync(request);
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error:'{ex.Message}'");
            }
        }

        /// <summary>
        /// This method creates a new version of an Amazon S3 object using
        /// PutObject on a bucket which has versioning enabled.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client used to
        /// create the temporary object.</param>
        /// <param name="bucketName">The name of the bucket where the temporary
        /// object version will be created.</param>
        /// <param name="objectKey">The name of the object for which we will
        /// create a temporary version.</param>
        /// <returns>A string that represents the version ID we just created.</returns>
        public static async Task<string> PutAnObject(IAmazonS3 client, string bucketName, string objectKey)
        {
            PutObjectRequest request = new PutObjectRequest
            {
                BucketName = bucketName,
                Key = objectKey,
                ContentBody = "This is the content body!",
            };

            PutObjectResponse response = await client.PutObjectAsync(request);
            return response.VersionId;
        }
    }
}
