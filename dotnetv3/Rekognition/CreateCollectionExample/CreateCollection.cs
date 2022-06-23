// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreateCollectionExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.Rekognition;
    using Amazon.Rekognition.Model;

    /// <summary>
    /// Uses Amazon Rekognition to create a collection to which you can add
    /// faces using the IndexFaces operation. The example was created using
    /// the AWS SDK for .NET 3.7 and .NET Core 5.0.
    /// </summary>
    public class CreateCollection
    {
        // snippet-start:[Rekognition.dotnetv3.CreateCollectionExample]
        public static async Task Main()
        {
            var rekognitionClient = new AmazonRekognitionClient();

            string collectionId = "MyCollection";
            Console.WriteLine("Creating collection: " + collectionId);

            var createCollectionRequest = new CreateCollectionRequest
            {
                CollectionId = collectionId,
            };

            CreateCollectionResponse createCollectionResponse = await rekognitionClient.CreateCollectionAsync(createCollectionRequest);
            Console.WriteLine($"CollectionArn : {createCollectionResponse.CollectionArn}");
            Console.WriteLine($"Status code : {createCollectionResponse.StatusCode}");
        }

        // snippet-end:[Rekognition.dotnetv3.CreateCollectionExample]
    }
}
