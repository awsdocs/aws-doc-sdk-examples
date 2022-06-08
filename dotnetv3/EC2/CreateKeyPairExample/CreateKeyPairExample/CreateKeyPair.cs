// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreateKeyPairExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.EC2;
    using Amazon.EC2.Model;

    /// <summary>
    /// Shows how to create a new Amazon Elastic Compute Cloud (Amazon EC2)
    /// key pair. The example was uses the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    // snippet-start:[EC2.dotnetv3.CreateKeyPairExample]
    public class CreateKeyPair
    {
        /// <summary>
        /// Initializes the Amazon EC2 client object and then calls the
        /// CreateKeyPairAsync method to create the new key pair.
        /// </summary>
        public static async Task Main()
        {
            string keyName = "sdk-example-key-pair";

            // If the default user on your system is not the same as
            // the region where you want to create the key pair, you
            // need to supply the AWS Region as a parameter to the
            // client constructor.
            var client = new AmazonEC2Client();

            var request = new CreateKeyPairRequest
            {
                KeyName = keyName,
            };

            var response = await client.CreateKeyPairAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                var kp = response.KeyPair;
                Console.WriteLine($"{kp.KeyName} with the ID: {kp.KeyPairId}.");
            }
            else
            {
                Console.WriteLine("Could not create key pair.");
            }
        }
    }

    // snippet-end:[EC2.dotnetv3.CreateKeyPairExample]
}
