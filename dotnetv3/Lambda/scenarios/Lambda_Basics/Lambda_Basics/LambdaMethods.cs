// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace Lambda_Basics
{
    public class LambdaMethods
    {
        // snippet-start:[lambda.dotnetv3.Lambda_Basics.DeleteLambdaFunction]

        /// <summary>
        /// Deletes a Lambda function.
        /// </summary>
        /// <param name="client">An initialized Lambda client object.</param>
        /// <param name="functionName">The name of the Lambda function to
        /// delete.</param>
        /// <returns>A Boolean value that indicates where the function was
        /// successfully deleted.</returns>
        public async Task<bool> DeleteLambdaFunction(AmazonLambdaClient client, string functionName)
        {
            var request = new DeleteFunctionRequest
            {
                FunctionName = functionName,
            };

            var response = await client.DeleteFunctionAsync(request);
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.DeleteLambdaFunction]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.UpdateFunctionCode]

        /// <summary>
        /// Updates an existing Lambda function.
        /// </summary>
        /// <param name="client">An initialized Lambda client object.</param>
        /// <param name="functionName">The name of the Lambda function to update.</param>
        /// <param name="bucketName">The bucket where the zip file containing
        /// the Lambda function code is stored.</param>
        /// <param name="key">The key name of the source code file.</param>
        /// <returns>A System Threading Task.</returns>
        public async Task UpdateFunctionCode(
            AmazonLambdaClient client,
            string functionName,
            string bucketName,
            string key)
        {
            var functionCodeRequest = new UpdateFunctionCodeRequest
            {
                FunctionName = functionName,
                Publish = true,
                S3Bucket = bucketName,
                S3Key = key,
            };

            var response = await client.UpdateFunctionCodeAsync(functionCodeRequest);
            Console.WriteLine($"The Function was last modified at {response.LastModified}.");
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.UpdateFunctionCode]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.InvokeFunction]

        /// <summary>
        /// Invokes a Lambda function.
        /// </summary>
        /// <param name="client">An initialized Lambda client object.</param>
        /// <param name="functionName">The name of the Lambda function to
        /// invoke.</param>
        /// <returns>A System Threading Task.</returns>
        public async Task<string> InvokeFunctionAsync(
            AmazonLambdaClient client,
            string functionName,
            string parameters)
        {
            var payload = parameters;
            var request = new InvokeRequest
            {
                FunctionName = functionName,
                Payload = payload,
            };

            var response = await client.InvokeAsync(request);
            MemoryStream stream = response.Payload;
            string returnValue = System.Text.Encoding.UTF8.GetString(stream.ToArray());
            Console.WriteLine(returnValue);
            return returnValue;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.InvokeFunction]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.ListFunctions]

        /// <summary>
        /// Gets a list of Lambda functions.
        /// </summary>
        /// <param name="client">The initialized Lambda client object.</param>
        /// <returns>A list of FunctionConfiguration objects.</returns>
        public async Task<List<FunctionConfiguration>> ListFunctions(AmazonLambdaClient client)
        {
            var reponse = await client.ListFunctionsAsync();
            var functionList = reponse.Functions;
            return functionList;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.ListFunctions]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.GetFunction]

        /// <summary>
        /// Gets information about a Lambda function.
        /// </summary>
        /// <param name="client">The initialized Lambda client object.</param>
        /// <param name="functionName">The name of the Lambda function for
        /// which to retrieve information.</param>
        /// <returns>A System Threading Task.</returns>
        public async Task<FunctionConfiguration> GetFunction(AmazonLambdaClient client, string functionName)
        {
            var functionRequest = new GetFunctionRequest
            {
                FunctionName = functionName,
            };

            var response = await client.GetFunctionAsync(functionRequest);
            return response.Configuration;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.GetFunction]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.CreateLambdaFunction]

        /// <summary>
        /// Creates a new Lambda function.
        /// </summary>
        /// <param name="client">The initialized Lambda client object.</param>
        /// <param name="functionName">The name of the function.</param>
        /// <param name="s3Bucket">The S3 bucket where the zip file containing
        /// the code is located.</param>
        /// <param name="s3Key">The S3 key of the zip file.</param>
        /// <param name="role">A role with the appropriate Amazon Lambda
        /// permissions.</param>
        /// <param name="handler">The name of the handler function.</param>
        /// <returns>The Amazon Resource Name (ARN) of the newly created
        /// Amazon Lambda function.</returns>
        public async Task<string> CreateLambdaFunction(
            AmazonLambdaClient client,
            string functionName,
            string s3Bucket,
            string s3Key,
            string role,
            string handler)
        {
            var functionCode = new FunctionCode
            {
                S3Bucket = s3Bucket,
                S3Key = s3Key,
            };

            var createFunctionRequest = new CreateFunctionRequest
            {
                FunctionName = functionName,
                Description = "Created by the Lambda .NET API",
                Code = functionCode,
                Handler = handler,
                Runtime = Runtime.Dotnet6,
                Role = role,
            };

            var reponse = await client.CreateFunctionAsync(createFunctionRequest);
            return reponse.FunctionArn;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.CreateLambdaFunction]
    }
}
