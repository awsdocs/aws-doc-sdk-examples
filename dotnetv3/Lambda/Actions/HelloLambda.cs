// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[Lambda.dotnetv3.LambdaActions.HelloLambda]
namespace ServiceActions;

using Amazon.Lambda;

public class HelloLambda
{

    static async Task Main(string[] args)
    {
        var lambdaClient = new AmazonLambdaClient();

        var response = await lambdaClient.ListFunctionsAsync();
        response.Functions.ForEach(function =>
        {
            Console.WriteLine($"{function.FunctionName}\t{function.Description}");
        });
    }
}

// snippet-end:[Lambda.dotnetv3.LambdaActions.HelloLambda]