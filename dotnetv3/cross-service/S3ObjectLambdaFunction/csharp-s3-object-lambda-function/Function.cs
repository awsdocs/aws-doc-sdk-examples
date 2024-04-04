// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Text;
using Amazon.Lambda.Core;
using Amazon.Lambda.S3Events;
using Amazon.S3;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace csharp_s3_object_lambda_function;

public class Function
{
    IAmazonS3 S3Client { get; set; }

    public Function()
    {
        S3Client = new AmazonS3Client();
    }
    public Function(IAmazonS3 s3Client)
    {
        this.S3Client = s3Client;
    }

    public async Task<HttpResponseMessage> FunctionHandler(S3ObjectLambdaEvent s3Event, ILambdaContext context)
    {
        try
        {
            var objectContext = s3Event.GetObjectContext;
            var s3Url = objectContext.InputS3Url;
            var requestRoute = objectContext.OutputRoute;
            var requestToken = objectContext.OutputToken;
            context.Logger.LogInformation(objectContext.InputS3Url.ToString());

            var httpClient = new HttpClient();

            var response = await httpClient.GetAsync(s3Url);
            var responseBody = await response.Content.ReadAsStringAsync();
            var transformedObject = responseBody.ToUpper();

            var writeGetObjectResponseRequest = new Amazon.S3.Model.WriteGetObjectResponseRequest
            {
                Body = GenerateStreamFromString(transformedObject),
                RequestRoute = requestRoute,
                RequestToken = requestToken
            };

            await S3Client.WriteGetObjectResponseAsync(writeGetObjectResponseRequest);

            return new HttpResponseMessage(System.Net.HttpStatusCode.OK);
        }
        catch (Exception e)
        {
            context.Logger.LogError(e.Message);
            context.Logger.LogError(e.StackTrace);
            throw;
        }
    }

    public static MemoryStream GenerateStreamFromString(string value)
    {
        return new MemoryStream(Encoding.UTF8.GetBytes(value ?? ""));
    }
}
