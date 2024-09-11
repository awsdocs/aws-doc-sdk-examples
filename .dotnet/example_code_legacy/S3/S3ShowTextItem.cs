// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
  To compile, use the VS command prompt and run (all on one line):

    csc S3ShowTextItem.cs
      /reference:"C:\Program Files (x86)\AWS SDK for .NET\bin\Net45\AWSSDK.Core.dll"
      /reference:"C:\Program Files (x86)\AWS SDK for .NET\bin\Net45\AWSSDK.S3.dll"

  To run:
    S3ShowTextItem.exe REGION BUCKET ITEM OUTPUT-FILE
*/
// snippet-start:[s3.dotnet.getobjectasync.complete]
using System;
using System.IO;
using System.Threading;
using System.Threading.Tasks;

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;

namespace S3ShowTextItem
{
    class S3Sample
    {
        static async Task<GetObjectResponse> MyGetObjectAsync(string region, string bucket, string item)
        {
            RegionEndpoint reg = RegionEndpoint.GetBySystemName(region);
            AmazonS3Client s3Client = new AmazonS3Client(reg);

            Console.WriteLine("Retrieving (GET) an object");

            GetObjectResponse response = await s3Client.GetObjectAsync(bucket, item, new CancellationToken());

            return response;
        }

        public static void Main(string[] args)
        {
            if (args.Length < 4)
            {
                Console.WriteLine("You must supply a region, bucket name, text file name, and output file name");
                return;
            }

            try
            {
                Task<GetObjectResponse> response = MyGetObjectAsync(args[0], args[1], args[2]);

                Stream responseStream = response.Result.ResponseStream;
                StreamReader reader = new StreamReader(responseStream);

                string responseBody = reader.ReadToEnd();

                using(FileStream s = new FileStream(args[3], FileMode.Create))
                using(StreamWriter writer = new StreamWriter(s))
                {
                    writer.WriteLine(responseBody);
                }
            }
            catch (AmazonS3Exception s3Exception)
            {
                Console.WriteLine(s3Exception.Message, s3Exception.InnerException);
            }

            Console.WriteLine("Press enter to continue");
            Console.ReadLine();
        }
    }
}
// snippet-end:[s3.dotnet.getobjectasync.complete]