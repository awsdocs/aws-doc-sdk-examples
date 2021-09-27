// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System;
using System.Threading.Tasks;
using Amazon.Lambda.Core;
using Amazon.Lambda.RuntimeSupport;
using Amazon.Lambda.Serialization.SystemTextJson;
using Amazon.S3;
using Amazon.S3.Model;

// This project specifies the serializer used to convert Lambda event into .NET
// classes in the project's main main function. This assembly register a
// serializer for use when the project is being debugged using the AWS .NET
// Mock Lambda Test Tool.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace CreateDynamoDBTableExample
{
    /// <summary>
    /// Shows how to use an AWS Lambda function to create a new Amazon Simple
    /// Storage Service (Amazon S3) bucket. The example was created using the
    /// AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class Function
    {
        public static async Task Main()
        {
            Func<string, ILambdaContext, Task> func = FunctionHandler;
            using var handlerWrapper = HandlerWrapper.GetHandlerWrapper(func, new DefaultLambdaJsonSerializer());
            using var bootstrap = new LambdaBootstrap(handlerWrapper);
            await bootstrap.RunAsync();
        }

        /// <summary>
        /// A simple function that takes a string and creates a new Amazon
        /// Simple Storage Service (Amazon S3) bucket.
        /// </summary>
        /// <param name="input">A string representing the name of the Amazon S3
        /// bucket to create.</param>
        public static async Task FunctionHandler(string input, ILambdaContext context)
        {
            if (input is not null)
            {
                IAmazonS3 client = new AmazonS3Client();
                var bucketName = input;

                var putBucketRequest = new PutBucketRequest
                {
                    BucketName = bucketName,
                    UseClientRegion = true,
                };
                _ = await client.PutBucketAsync(putBucketRequest);
            }
        }
    }
}
