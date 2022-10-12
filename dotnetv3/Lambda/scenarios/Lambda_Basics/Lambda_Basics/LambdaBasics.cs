// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Before running this SDK for .NET (v3) code example, set up your development environment, including your credentials.
// For more information, see the following documentation:
// https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html
// This code example performs the following operations:
// 1. Creates an IAM policy that will be used by AWS Lambda.
// 2. Attaches the policy to a new IAM role.
// 3. Creates an AWS Lambda function.
// 4. Gets a specific AWS Lambda function.
// 5. Lists all Lambda functions.
// 6. Invokes a Lambda function.
// 7. Updates a Lambda function's code.
// 8. Updates a Lambda function's configuration value.
// 9. Deletes the Lambda function.
// 10. Deletes the role.

// snippet-start:[lambda.dotnetv3.Lambda_Basics.main]
// Set the following variables:
//
//   functionName - The name of the Lambda function.
//   roleArn - The AWS Identity and Access Management (IAM) service role that
//       has Lambda permissions.
//   handler - The fully qualified method name (for example,
//       example.Handler::handleRequest).
//   bucketName - The Amazon Simple Storage Service (Amazon S3) bucket name
//       that contains the .zip or .jar used to update the Lambda function's code.
//   key - The Amazon S3 key name that represents the .zip or .jar (for
//       example, LambdaHello-1.0-SNAPSHOT.jar).
//   keyUpdate - The Amazon S3 key name that represents the updated .zip (for
//      example, "updated-function.zip").
using Amazon.Runtime;

var _configuration = new ConfigurationBuilder()
    .SetBasePath(Directory.GetCurrentDirectory())
    .AddJsonFile("settings.json") // Load test settings from JSON file.
    .AddJsonFile("settings.local.json",
    true) // Optionally load local settings.
.Build();

string functionName = _configuration["FunctionName"];
string roleName = _configuration["RoleName"];
string policyDocument = "{" +
    " \"Version\": \"2012-10-17\"," +
    " \"Statement\": [ " +
    "    {" +
    "        \"Effect\": \"Allow\"," +
    "        \"Principal\": {" +
    "            \"Service\": \"lambda.amazonaws.com\" " +
    "    }," +
    "        \"Action\": \"sts:AssumeRole\" " +
    "    }" +
    "]" +
"}";

string handler = _configuration["Handler"];
string bucketName = _configuration["BucketName"];
string key = _configuration["Key"];
string updateKey = _configuration["UpdateKey"];

string sepBar = new('-', 80);

var lambdaClient = new AmazonLambdaClient();
var lambdaMethods = new LambdaMethods();
var lambdaRoleMethods = new LambdaRoleMethods();

ShowOverview();

// Create the policy to use with the Lambda functions and then attach the
// policy to a new role.
var roleArn = await lambdaRoleMethods.CreateLambdaRole(roleName, policyDocument);

Console.WriteLine("Waiting for role to become active.");
System.Threading.Thread.Sleep(10000);

// Create the Lambda function using a zip file stored in an Amazon S3 bucket.
Console.WriteLine(sepBar);
Console.WriteLine($"Creating the AWS Lambda function: {functionName}.");
var lambdaARN = await lambdaMethods.CreateLambdaFunction(
    lambdaClient,
    functionName,
    bucketName,
    key,
    roleArn,
    handler);

Console.WriteLine(sepBar);
Console.WriteLine($"The AWS Lambda ARN is {lambdaARN}");

// Get the Lambda function.
Console.WriteLine($"Getting the {functionName} AWS Lambda function.");
FunctionConfiguration config;
do
{
    config = await lambdaMethods.GetFunction(lambdaClient, functionName);
    Console.Write(".");
}
while (config.State != State.Active);

Console.WriteLine($"\nThe function, {functionName} has been created.");
Console.WriteLine($"The runtime of this Lambda function is {config.Runtime}.");

PressEnter();

// List the Lambda functions.
Console.WriteLine(sepBar);
Console.WriteLine("Listing all Lambda functions.");
var functions = await lambdaMethods.ListFunctions(lambdaClient);
DisplayFunctionList(functions);
Console.WriteLine(sepBar);
Console.WriteLine("*** Sleep for 1 min to get Lambda function ready.");
System.Threading.Thread.Sleep(60000);

Console.WriteLine(sepBar);
Console.WriteLine("Invoke the Lambda increment function.");
string value = string.Empty;
do
{
    Console.Write("Enter a value to increment: ");
    value = Console.ReadLine();
}
while (value == string.Empty);

string functionParameters = "{" +
    "\"action\": \"increment\", " +
    "\"x\": \"" + value + "\"" +
"}";
var answer = await lambdaMethods.InvokeFunctionAsync(lambdaClient, functionName, functionParameters);
Console.WriteLine($"{value} +1 = {answer}.");

Console.WriteLine(sepBar);
Console.WriteLine("Now update the Lambda function code.");
await lambdaMethods.UpdateFunctionCode(lambdaClient, functionName, bucketName, updateKey);

Console.WriteLine("Sleep for 1 min to update to complete.");
System.Threading.Thread.Sleep(60000);

Console.WriteLine(sepBar);
Console.WriteLine("Now call the updated function...");

// Get an action and two numbers from the user.
value = string.Empty;
do
{
    Console.Write("Enter the first value: ");
    value = Console.ReadLine();
}
while (value == string.Empty);

string value2 = string.Empty;
do
{
    Console.Write("Enter a second value: ");
    value2 = Console.ReadLine();
}
while (value2 == string.Empty);

var opSelected = string.Empty;

Console.WriteLine("Select the operation to perform:");
Console.WriteLine("\t1. add");
Console.WriteLine("\t2. subtract");
Console.WriteLine("\t3. multiply");
Console.WriteLine("\t4. divide");
Console.WriteLine("Enter the number (1, 2, 3, or 4) of the operation you want to perform: ");
do
{
    Console.Write("Your choice? ");
    opSelected = Console.ReadLine();
}
while (opSelected == string.Empty);

var operation = (opSelected) switch
{
    "1" => "add",
    "2" => "subtract",
    "3" => "multiply",
    "4" => "divide",
    _ => "add",
};

functionParameters = "{" +
    "\"action\": \"" + operation + "\", " +
    "\"x\": \"" + value + "\"," +
    "\"y\": \"" + value2 + "\"" +
"}";

answer = await lambdaMethods.InvokeFunctionAsync(lambdaClient, functionName, functionParameters);
Console.WriteLine("The answer when we {action} the two numbers is: {answer}.");

PressEnter();

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

// Now delete the IAM role.
success = await lambdaRoleMethods.DeleteLambdaRole(roleName);
if (success)
{
    Console.WriteLine("The role has been successfully removed.");
}
else
{
    Console.WriteLine("Couldn't delete the role.");
}

Console.WriteLine("The Lambda Scenario is now complete.");

void DisplayFunctionList(List<FunctionConfiguration> functions)
{
    functions.ForEach(functionConfig =>
    {
        Console.WriteLine($"{functionConfig.FunctionName}\t{functionConfig.Description}");
    });
}

void ShowOverview()
{
    Console.WriteLine("Welcome to the AWS Lambda Basics Example");
    Console.WriteLine("Getting started with functions");
    Console.WriteLine(sepBar);
    Console.WriteLine("This scenario performs the following operations:");
    Console.WriteLine("\t 1. Creates an IAM policy that will be used by AWS Lambda.");
    Console.WriteLine("\t 2. Attaches the policy to a new IAM role.");
    Console.WriteLine("\t 3. Creates an AWS Lambda function.");
    Console.WriteLine("\t 4. Gets a specific AWS Lambda function.");
    Console.WriteLine("\t 5. Lists all Lambda functions.");
    Console.WriteLine("\t 6. Invokes the Lambda function.");
    Console.WriteLine("\t 7. Updates the Lambda function's code.");
    Console.WriteLine("\t 8. Updates the Lambda function's configuration value.");
    Console.WriteLine("\t 9. Deletes the Lambda function.");
    Console.WriteLine("\t10. Deletes the IAM role.");
    PressEnter();
}

void PressEnter()
{
    Console.Write("Press <Enter> to continue.");
    _ = Console.ReadLine();
    Console.WriteLine();
}

// snippet-end:[lambda.dotnetv3.Lambda_Basics.main]
