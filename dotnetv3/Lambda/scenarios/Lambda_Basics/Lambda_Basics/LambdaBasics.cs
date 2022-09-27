// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Before running this SDK for .NET (v3) code example, set up your development environment, including your credentials.
// For more information, see the following documentation:
// https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html
// This code example performs the following operations:
// 1. Creates an AWS Lambda function.
// 2. Gets a specific AWS Lambda function.
// 3. Lists all Lambda functions.
// 4. Invokes a Lambda function.
// 5. Updates a Lambda function's code.
// 6. Updates a Lambda function's configuration value.
// 7. Deletes a Lambda function.

// snippet-start:[lambda.dotnetv3.Lambda_Basics.main]
// Set the following variables:
//
//   functionName - The name of the Lambda function.
//   role - The AWS Identity and Access Management (IAM) service role that has
//       Lambda permissions.
//   handler - The fully qualified method name (for example,
//       example.Handler::handleRequest).
//   bucketName - The Amazon Simple Storage Service (Amazon S3) bucket name
//       that contains the .zip or .jar used to update the Lambda function's code.
//   key - The Amazon S3 key name that represents the .zip or .jar (for
//       example, LambdaHello-1.0-SNAPSHOT.jar).
//   keyUpdate - The Amazon S3 key name that represents the updated .zip or
//       .jar (for example, LambdaHello-1.0-SNAPSHOT.jar).
string functionName = "CreateS3Bucket";
string role = "lambda-support";
string handler = "FunctionHandler";
string bucketName = "<Enter Value>";
string key = "<Enter Value>";
string keyUpdate = "<Enter Value>";

string sepBar = new ('-', 80);

var lambdaClient = new AmazonLambdaClient();
var lambdaMethods = new LambdaMethods();

ShowOverview();

Console.WriteLine($"Creating the AWS Lambda function: {functionName}.");
var lambdaARN = await lambdaMethods.CreateLambdaFunction(lambdaClient, functionName, bucketName, key, role, handler);
Console.WriteLine(sepBar);
Console.WriteLine($"The AWS Lambda ARN is {lambdaARN}");

// Get the Lambda function.
Console.WriteLine($"Getting the {functionName} AWS Lambda function.");
await lambdaMethods.GetFunction(lambdaClient, functionName);
Console.WriteLine(sepBar);

// List the Lambda functions.
Console.WriteLine("Listing all functions.");
await lambdaMethods.ListFunctions(lambdaClient);

Console.WriteLine(sepBar);
Console.WriteLine("*** Sleep for 1 min to get Lambda function ready.");
System.Threading.Thread.Sleep(60000);

Console.WriteLine("*** Invoke the Lambda function.");
await lambdaMethods.InvokeFunctionAsync(lambdaClient, functionName);

Console.WriteLine(sepBar);
Console.WriteLine("*** Update the Lambda function code.");
await lambdaMethods.UpdateFunctionCode(lambdaClient, functionName, bucketName, keyUpdate);

Console.WriteLine("*** Sleep for 1 min to get Lambda function ready.");
System.Threading.Thread.Sleep(60000);

Console.WriteLine("*** Invoke the Lambda function again with the updated code.");
await lambdaMethods.InvokeFunctionAsync(lambdaClient, functionName);

Console.WriteLine(sepBar);
Console.WriteLine("Delete the AWS Lambda function.");
var success = await lambdaMethods.DeleteLambdaFunction(lambdaClient, functionName);
if (success)
{
    Console.WriteLine($"The {functionName} function was deleted.");
}
else
{
    Console.WriteLine($"Could not remove the function {functionName}");
}

void ShowOverview()
{
    Console.WriteLine("Welcome to the AWS Lambda Basics Example");
    Console.WriteLine("Getting started with functions");
    Console.WriteLine(sepBar);
    Console.WriteLine("This scenario performs the following operations:");
    Console.WriteLine("\t1. Creates an AWS Lambda function.");
    Console.WriteLine("\t2. Gets a specific AWS Lambda function.");
    Console.WriteLine("\t3. Lists all Lambda functions.");
    Console.WriteLine("\t4. Invokes a Lambda function.");
    Console.WriteLine("\t5. Updates a Lambda function's code.");
    Console.WriteLine("\t6. Updates a Lambda function's configuration value.");
    Console.WriteLine("\t7. Deletes a Lambda function.");
    PressEnter();
}

void PressEnter()
{
    Console.Write("Press <Enter> to continue.");
    _ = Console.ReadLine();
    Console.WriteLine();
}

// snippet-end:[lambda.dotnetv3.Lambda_Basics.main]
