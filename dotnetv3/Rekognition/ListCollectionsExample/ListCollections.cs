// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ListCollectionsExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.Rekognition;
    using Amazon.Rekognition.Model;

    /// <summary>
    /// Uses the Amazpon Rekognition Service to list the collection IDs in the
    /// default user account. This example was crated using the AWS SDK for
    /// .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class ListCollections
    {
        // snippet-start:[Rekognition.dotnetv3.ListCollectionsExample]
        public static async Task Main()
        {
            var rekognitionClient = new AmazonRekognitionClient();

            Console.WriteLine("Listing collections");
            int limit = 10;

            var listCollectionsRequest = new ListCollectionsRequest
            {
                MaxResults = limit,
            };

            var listCollectionsResponse = new ListCollectionsResponse();

            do
            {
                if (listCollectionsResponse is not null)
                {
                    listCollectionsRequest.NextToken = listCollectionsResponse.NextToken;
                }

                listCollectionsResponse = await rekognitionClient.ListCollectionsAsync(listCollectionsRequest);

                listCollectionsResponse.CollectionIds.ForEach(id =>
                {
                    Console.WriteLine(id);
                });
            }
            while (listCollectionsResponse.NextToken is not null);
        }

        // snippet-end:[Rekognition.dotnetv3.ListCollectionsExample]
    }
}
