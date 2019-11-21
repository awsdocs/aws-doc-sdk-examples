// snippet-sourcedescription:[KmsS3Encryption.cs demonstrates how to asynchronously encrypt an Amazon S3 bucket.]
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
// snippet-sourcedate:[2019-11-15]
// snippet-sourceauthor:[Doug-AWS]
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
/* 
   To compile and run this program:

   1. Using the Visual Studio command prompt compile it with 
      (all on one line; your path to the DLL might vary):

      csc KmsS3Encryption.cs
      /reference:"C:\Program Files (x86)\AWS SDK for .NET\bin\Net45\AWSSDK.Core.dll"
      /reference:"C:\Program Files (x86)\AWS SDK for .NET\bin\Net45\AWSSDK.KeyManagementService.dll"
      /reference:"C:\Program Files (x86)\AWS SDK for .NET\bin\Net45\AWSSDK.S3.dll"

   2. Run it:

      KmsS3Encryption.exe REGION BUCKET ITEM
*/
// snippet-start:[kms.dotnet.createkeyasync.complete]
using System;
using System.IO;
using System.Threading.Tasks;

using Amazon;
using Amazon.KeyManagementService;
using Amazon.KeyManagementService.Model;
using Amazon.S3.Encryption;
using Amazon.S3.Model;

namespace KmsS3Encryption
{
    class S3Sample
    {
        static async Task<CreateKeyResponse> MyCreateKeyAsync(string regionName)
        {
            RegionEndpoint region = RegionEndpoint.GetBySystemName(regionName);

            AmazonKeyManagementServiceClient kmsClient = new AmazonKeyManagementServiceClient(region);

            CreateKeyResponse response = await kmsClient.CreateKeyAsync(new CreateKeyRequest());

            return response;
        }

        static async Task<GetObjectResponse> MyPutObjectAsync(EncryptionMaterials materials, string bucketName, string keyName)
        {
            // CryptoStorageMode.ObjectMetadata is required for KMS EncryptionMaterials
            var config = new AmazonS3CryptoConfiguration()
            {
                StorageMode = CryptoStorageMode.ObjectMetadata
            };

            AmazonS3EncryptionClient s3Client = new AmazonS3EncryptionClient(config, materials);

            // encrypt and put object
            var putRequest = new PutObjectRequest
            {
                BucketName = bucketName,
                Key = keyName,
                ContentBody = "object content"
            };

            await s3Client.PutObjectAsync(putRequest);

            // get object and decrypt
            var getRequest = new GetObjectRequest
            {
                BucketName = bucketName,
                Key = keyName
            };

            GetObjectResponse response = await s3Client.GetObjectAsync(getRequest);

            return response;
        }
        public static void Main(string[] args)
        {
            if (args.Length < 3)
            {
                Console.WriteLine("You must supply a Region, bucket name, and item name:");
                Console.WriteLine("Usage: KmsS3Encryption REGION BUCKET ITEM");
                return;
            }

            string regionName = args[0];
            string bucketName = args[1];
            string itemName = args[2];

            Task<CreateKeyResponse> response = MyCreateKeyAsync(regionName);

            KeyMetadata keyMetadata = response.Result.KeyMetadata;
            string kmsKeyId = keyMetadata.KeyId;

            // An object that contains information about the CMK created by this operation.
            EncryptionMaterials kmsEncryptionMaterials = new EncryptionMaterials(kmsKeyId);

            Task<GetObjectResponse> goResponse = MyPutObjectAsync(kmsEncryptionMaterials, bucketName, itemName);

            Stream stream = goResponse.Result.ResponseStream;
            StreamReader reader = new StreamReader(stream);

            Console.WriteLine(reader.ReadToEnd());

            Console.WriteLine("Press any key to continue...");
            Console.ReadKey();
        }
    }
}
// snippet-end:[kms.dotnet.createkeyasync.complete]