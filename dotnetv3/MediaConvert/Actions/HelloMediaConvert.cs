// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[MediaConvert.dotnetv3.HelloMediaConvert]
using Amazon.MediaConvert;
using Amazon.MediaConvert.Model;

namespace MediaConvertActions;

public static class HelloMediaConvert
{
    static async Task Main(string[] args)
    {
        // Create the client using the default profile.
        var mediaConvertClient = new AmazonMediaConvertClient();

        Console.WriteLine($"Hello AWS Elemental MediaConvert! Your MediaConvert Endpoints are:");
        Console.WriteLine();

        // You can use await and any of the async methods to get a response.
        // Let's get the MediaConvert endpoints.
        var response = await mediaConvertClient.DescribeEndpointsAsync(
            new DescribeEndpointsRequest()
            );

        foreach (var endPoint in response.Endpoints)
        {
            Console.WriteLine($"\tEndPoint: {endPoint.Url}");
            Console.WriteLine();
        }
    }
}
// snippet-end:[MediaConvert.dotnetv3.HelloMediaConvert]