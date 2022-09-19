// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace Lambda_Basics
{
    public class LambdaMethods
    {
        // snippet-start:[lambda.dotnetv3.Lambda_Basics.DeleteLambdaFunction]
        public static async Task<bool> DeleteLambdaFunction(AmazonLambdaClient awsLambda, string functionName)
        {
            var request = new DeleteFunctionRequest
            {
                FunctionName = functionName,
            };

            var response = await awsLambda.DeleteFunctionAsync(request);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"The {functionName} function was deleted.");
                return true;
            }
            else
            {
                Console.WriteLine($"Could not remove the function {functionName}");
                return false;
            }
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.DeleteLambdaFunction]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.UpdateFunctionCode]
        public static async Task UpdateFunctionCode(AmazonLambdaClient awsLambda, string functionName, string bucketName, string key)
        {
            var functionCodeRequest = new UpdateFunctionCodeRequest
            {
                FunctionName = functionName,
                Publish = true,
                S3Bucket = bucketName,
                S3Key = key,
            };

            var response = await awsLambda.UpdateFunctionCodeAsync(functionCodeRequest);
            Console.WriteLine($"The Function was last modified at {response.LastModified}.");
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.UpdateFunctionCode]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.InvokeFunction]
        public static async Task InvokeFunction(AmazonLambdaClient awsLambda, string functionName)
        {
            var payload = "{\"inputValue\":\"2000\"}";
            var request = new InvokeRequest
            {
                FunctionName = functionName,
                Payload = payload,
            };

            var response = await awsLambda.InvokeAsync(request);
            MemoryStream stream = response.Payload;
            string result = System.Text.Encoding.UTF8.GetString(stream.ToArray());
            Console.WriteLine(result);
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.InvokeFunction]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.ListFunctions]
        public static async Task<List<FunctionConfiguration>> ListFunctions(AmazonLambdaClient awsLambda)
        {
            var functionResult = await awsLambda.ListFunctionsAsync();
            var list = functionResult.Functions;
            foreach (FunctionConfiguration config in list)
            {
                Console.WriteLine($"The function name is {config.FunctionName}");
            }

            return list;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.ListFunctions]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.GetFunction]
        public static async Task GetFunction(AmazonLambdaClient awsLambda, string functionName)
        {
            var functionRequest = new GetFunctionRequest
            {
                FunctionName = functionName,
            };

            var response = await awsLambda.GetFunctionAsync(functionRequest);
            Console.WriteLine("The runtime of this Lambda function is " + response.Configuration.Runtime);
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.GetFunction]

        // snippet-start:[lambda.dotnetv3.Lambda_Basics.CreateLambdaFunction]
        public static async Task<string> CreateLambdaFunction(
            AmazonLambdaClient awsLambda,
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

            var functionRequest = new CreateFunctionRequest
            {
                FunctionName = functionName,
                Description = "Created by the Lambda .NET API",
                Code = functionCode,
                Handler = handler,
                Runtime = Runtime.Java8,
                Role = role,
            };

            var reponse = await awsLambda.CreateFunctionAsync(functionRequest);
            return reponse.FunctionArn;
        }

        // snippet-end:[lambda.dotnetv3.Lambda_Basics.CreateLambdaFunction]
    }
}
