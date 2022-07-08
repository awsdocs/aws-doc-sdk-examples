// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DeleteKeyPairExample
{
    // snippet-start:[EC2.dotnetv3.DeleteKeyPairExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to delete an existing Amazon Elastic Compute Cloud
    /// (Amazon EC2) key pair. The example was uses the AWS SDK for .NET
    /// version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteKeyPair
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and then calls the
        /// DeleteKeyPairAsync method to delete the key pair.
        /// </summary>
        public static async Task Main()
        {
            string keyName = "sdk-example-key-pair";

            // If the key pair was not created in the same AWS Region as
            // the default user on your system, you need to supply
            // the AWS Region as a parameter to the client constructor.
            var client = new AmazonEC2Client();

            var request = new DeleteKeyPairRequest
            {
                KeyName = keyName,
            };

            var response = await client.DeleteKeyPairAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Successfully deleted the key pair: {keyName}.");
            }
            else
            {
                Console.WriteLine("Could not delete the key pair.");
            }
        }
    }

    // snippet-end:[EC2.dotnetv3.DeleteKeyPairExample]
}
