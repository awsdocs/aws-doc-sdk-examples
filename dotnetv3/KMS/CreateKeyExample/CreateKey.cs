// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

    /// <summary>
    /// Shows how to create a new AWS Key Management Service (AWS KMS)
    /// key. It uses the AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
namespace CreateKeyExample
{
    // snippet-start:[KMS.dotnetv3.CreateKeyExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.KeyManagementService;
    using Amazon.KeyManagementService.Model;

    public class CreateKey
    {
         public static async Task Main()
        {
            // Note that if you need to create a Key in an AWS Region
            // other than the region defined for the default user, you need to
            // pass the region to the client constructor.
            var client = new AmazonKeyManagementServiceClient();

            // The call to CreateKeyAsync will create a symmetrical AWS KMS
            // key. For more information about symmetrical and asymmetrical
            // keys, see:
            //
            // https://docs.aws.amazon.com/kms/latest/developerguide/symm-asymm-choose.html
            var response = await client.CreateKeyAsync(new CreateKeyRequest());

            // The KeyMetadata object contains information about the new AWS KMS key.
            KeyMetadata keyMetadata = response.KeyMetadata;

            if (keyMetadata is not null)
            {
                Console.WriteLine($"KMS Key: {keyMetadata.KeyId} was successfully created.");
            }
            else
            {
                Console.WriteLine("Could not create KMS Key.");
            }
        }

    }

    // snippet-end:[KMS.dotnetv3.CreateKeyExample]
}
