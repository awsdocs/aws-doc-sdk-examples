// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace InvokeFunctionExample
{
    // snippet-start:[Lambda.dotnetv3.InvokeFunctionExample]
    using System.Threading.Tasks;
    using Amazon.Lambda;
    using Amazon.Lambda.Model;

    /// <summary>
    /// Shows how to invoke an existing Amazon Lambda Function from a C#
    /// application. The example was created using the AWS SDK for .NET and
    /// .NET Core 5.0.
    /// </summary>
    public class InvokeFunction
    {
        /// <summary>
        /// Initializes the Lambda client and then invokes the Lambda Function
        /// called "CreateDDBTable" with the parameter "\"DDBWorkTable\"" to
        /// create the table called DDBWorkTable.
        /// </summary>
        public static async Task Main()
        {
            IAmazonLambda client = new AmazonLambdaClient();
            string functionName = "CreateDDBTable";
            string invokeArgs = "\"DDBWorkTable\"";

            var response = await client.InvokeAsync(
                new InvokeRequest
                {
                    FunctionName = functionName,
                    Payload = invokeArgs,
                    InvocationType = "Event",
                });
        }
    }
    // snippet-end:[Lambda.dotnetv3.InvokeFunctionExample]
}
