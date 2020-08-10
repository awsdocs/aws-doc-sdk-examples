// snippet-sourcedescription:[KmsS3Encryption.cs demonstrates how to asynchronously encrypt an item in an Amazon S3 bucket.]
// snippet-service:[s3]
// snippet-service:[kms]
// snippet-keyword:[.NET]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[AWS Key Management Service]
// snippet-keyword:[AWS KMS]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateKeyAsync]
// snippet-keyword:[PutObjectAsync]
// snippet-keyword:[GetObjectAsync]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-08-10]
// snippet-sourceauthor:[AWS-NETSDK]
/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
*/
//
// ABOUT THIS .NET EXAMPLE: This code example is part of the AWS SDK for .NET Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-net/latest/developer-guide/kms-keys-s3-encryption.html
//
// snippet-start:[kms.dotnet.createkeyasync.complete]
using System;
using System.IO;
using System.Threading.Tasks;
using System.Collections.Generic;

using Amazon;
using Amazon.KeyManagementService;
using Amazon.KeyManagementService.Model;
using Amazon.Extensions.S3.Encryption;
using Amazon.Extensions.S3.Encryption.Primitives;
using Amazon.S3.Model;

namespace KmsS3Encryption
{
  class S3Sample
  {
    public static async Task Main(string[] args)
    {
      if (args.Length != 3)
      {
        Console.WriteLine("\nUsage: KmsS3Encryption REGION BUCKET ITEM");
        Console.WriteLine("  REGION: The AWS Region (for example, \"us-west-1\").");
        Console.WriteLine("  BUCKET: The name of an existing S3 bucket.");
        Console.WriteLine("  ITEM: The name you want to use for the item.");
        return;
      }
      string regionName = args[0];
      string bucketName = args[1];
      string itemName = args[2];

      // Create a customer master key (CMK) and store the result
      var createKeyResponse = await MyCreateKeyAsync(regionName);
      var kmsEncryptionContext = new Dictionary<string, string>();
      var kmsEncryptionMaterials = new EncryptionMaterialsV2(
        createKeyResponse.KeyMetadata.KeyId, KmsType.KmsContext, kmsEncryptionContext);

      // Create the object in the bucket, then display the content of the object
      var putObjectResponse =
        await CreateAndRetrieveObjectAsync(kmsEncryptionMaterials, bucketName, itemName);
      Stream stream = putObjectResponse.ResponseStream;
      StreamReader reader = new StreamReader(stream);
      Console.WriteLine(reader.ReadToEnd());
      Console.WriteLine("Press any key to continue...");
      Console.ReadKey();
    }


    //
    // Method to create a customer master key
    static async Task<CreateKeyResponse> MyCreateKeyAsync(string regionName)
    {
      var kmsClient = new AmazonKeyManagementServiceClient(
        RegionEndpoint.GetBySystemName(regionName));
      return await kmsClient.CreateKeyAsync(new CreateKeyRequest());
    }


    //
    // Method to create and encrypt an object in an S3 bucket
    static async Task<GetObjectResponse> CreateAndRetrieveObjectAsync(
      EncryptionMaterialsV2 materials, string bucketName, string keyName)
    {
      // CryptoStorageMode.ObjectMetadata is required for KMS EncryptionMaterials
      var config = new AmazonS3CryptoConfigurationV2(SecurityProfile.V2AndLegacy)
      {
        StorageMode = CryptoStorageMode.ObjectMetadata
      };
      var s3Client = new AmazonS3EncryptionClientV2(config, materials);

      // Create, encrypt, and put the object
      await s3Client.PutObjectAsync(new PutObjectRequest
      {
        BucketName = bucketName,
        Key = keyName,
        ContentBody = "Object content for KmsS3Encryption example."
      });

      // Get, decrypt, and return the object
      return await s3Client.GetObjectAsync(new GetObjectRequest
      {
        BucketName = bucketName,
        Key = keyName
      });
    }
  }
}
// snippet-end:[kms.dotnet.createkeyasync.complete]