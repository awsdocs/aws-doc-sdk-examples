// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ListFunctionsExample
{
    // snippet-start:[Lambda.dotnetv3.ListFunctionsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.Lambda;
    using Amazon.Lambda.Model;

    /// <summary>
    /// This example shows two ways to list the AWS Lambda functions you have
    /// created for your account. It will only list the functions within one
    /// AWS Region at a time, however, so you need to pass the AWS Region you
    /// are interested in to the Lambda client object constructor. This example
    /// was created with the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class ListFunctions
    {
        public static async Task Main()
        {
            // If the AWS Region you are interested in listing is the same as
            // the AWS Region defined for the default user, you don't have to
            // supply the RegionEndpoint constant to the constructor.
            IAmazonLambda client = new AmazonLambdaClient(RegionEndpoint.USEast2);

            // First use the ListFunctionsAsync method.
            var functions1 = await ListFunctionsAsync(client);

            DisplayFunctionList(functions1);

            // Get the list again useing a Lambda client paginator.
            var functions2 = await ListFunctionsPaginatorAsync(client);

            DisplayFunctionList(functions2);
        }

        /// <summary>
        /// Calls the asynchronous ListFunctionsAsync method of the Lambda
        /// client to retrieve the list of functions in the AWS Region with
        /// which the Lambda client was initialized.
        /// </summary>
        /// <param name="client">The initialized Lambda client which will be
        /// used to retrieve the list of Lambda functions.</param>
        /// <returns>A list of Lambda functions configuration information.</returns>
        public static async Task<List<FunctionConfiguration>> ListFunctionsAsync(IAmazonLambda client)
        {
            // Get the list of functions. The response will have a property
            // called Functions, a list of information about the Lambda
            // functions defined on your account in the specified region.
            var response = await client.ListFunctionsAsync();

            return response.Functions;
        }

        // snippet-start:[Lambda.dotnetv3.ListFunctionsExample.Paginator]

        /// <summary>
        /// Uses a Lambda paginator to retrieve the list of functions in the
        /// AWS Region with which the Lambda client was initialized.
        /// </summary>
        /// <param name="client">The initialized Lambda client which will be
        /// used to retrieve the list of Lambda functions.</param>
        /// <returns>A list of Lambda functions configuration information.</returns>
        public static async Task<List<FunctionConfiguration>> ListFunctionsPaginatorAsync(IAmazonLambda client)
        {
            Console.WriteLine("\nNow let's show the list using a paginator.\n");

            // Get the list of functions using a paginator.
            var paginator = client.Paginators.ListFunctions(new ListFunctionsRequest());

            // Defined return a list of function information to the caller
            // for display using the DisplayFunctionList method.
            var functions = new List<FunctionConfiguration>();

            await foreach (var resp in paginator.Responses)
            {
                resp.Functions
                    .ForEach(f => functions.Add(f));
            }

            return functions;
        }

        // snippet-end:[Lambda.dotnetv3.ListFunctionsExample.Paginator]

        /// <summary>
        /// Displays the details of each function in the list of functions
        /// passed to the method.
        /// </summary>
        /// <param name="functions">A list of FunctionConfiguration objects.</param>
        public static void DisplayFunctionList(List<FunctionConfiguration> functions)
        {
            // Display a list of the Lambda functions on the console.
            functions
                .ForEach(f => Console.WriteLine($"{f.FunctionName}\t{f.Handler}"));
        }
    }
    // snippet-end:[Lambda.dotnetv3.ListFunctionsExample]
}
