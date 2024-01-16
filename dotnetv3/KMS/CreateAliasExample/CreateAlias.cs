// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace CreateAliasExample
{
    // snippet-start:[kms.dotnetv3.CreateAliasExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.KeyManagementService;
    using Amazon.KeyManagementService.Model;

    /// <summary>
    /// Creates an alias for an AWS Key Management Service (AWS KMS) key.
    /// </summary>
    public class CreateAlias
    {
        public static async Task Main()
        {
            var client = new AmazonKeyManagementServiceClient();

            // The alias name must start with alias/ and can be
            // up to 256 alphanumeric characters long.
            var aliasName = "alias/ExampleAlias";

            // The value supplied as the TargetKeyId can be either
            // the key ID or key Amazon Resource Name (ARN) of the
            // AWS KMS key.
            var keyId = "1234abcd-12ab-34cd-56ef-1234567890ab";

            var request = new CreateAliasRequest
            {
                AliasName = aliasName,
                TargetKeyId = keyId,
            };

            var response = await client.CreateAliasAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Alias, {aliasName}, successfully created.");
            }
            else
            {
                Console.WriteLine($"Could not create alias.");
            }
        }
    }

    // snippet-end:[kms.dotnetv3.CreateAliasExample]
}