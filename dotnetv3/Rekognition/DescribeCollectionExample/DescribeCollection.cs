// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DescribeCollectionExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.Rekognition;
    using Amazon.Rekognition.Model;

    /// <summary>
    /// Uses the Amazon Rekognition Service to describe the contents of a
    /// collection. The example was created using the AWS SDK for .NET version
    /// 3.7 and .NET Core 5.0.
    /// </summary>
    public class DescribeCollection
    {
        // snippet-start:[Rekognition.dotnetv3.DescribeCollectionExample]
        public static async Task Main()
        {
            var rekognitionClient = new AmazonRekognitionClient();

            string collectionId = "MyCollection";
            Console.WriteLine($"Describing collection: {collectionId}");

            var describeCollectionRequest = new DescribeCollectionRequest()
            {
                CollectionId = collectionId,
            };

            var describeCollectionResponse = await rekognitionClient.DescribeCollectionAsync(describeCollectionRequest);
            Console.WriteLine($"Collection ARN: {describeCollectionResponse.CollectionARN}");
            Console.WriteLine($"Face count: {describeCollectionResponse.FaceCount}");
            Console.WriteLine($"Face model version: {describeCollectionResponse.FaceModelVersion}");
            Console.WriteLine($"Created: {describeCollectionResponse.CreationTimestamp}");
        }

        // snippet-end:[Rekognition.dotnetv3.DescribeCollectionExample]
    }
}
