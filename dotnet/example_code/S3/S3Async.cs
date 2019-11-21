// snippet-sourcedescription:[S3_async_net35_complete.cs demonstrates how to synchronously and asynchronously add an object to an Amazon S3 bucket.]
// snippet-service:[s3]
// snippet-keyword:[.NET]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[PutObject]
// snippet-keyword:[BeginPutObject]
// snippet-keyword:[EndPutObject]
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
// snippet-start:[s3.dotnet.putobject.async.complete]
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Text;
using System.Threading;

using Amazon;
using Amazon.Runtime;
using Amazon.S3;
using Amazon.S3.Model;

namespace async_aws_net
{
    // snippet-start:[s3.dotnet.putobject.async.clientstate]
    class ClientState
    {
        AmazonS3Client client;
        DateTime startTime;

        public AmazonS3Client Client
        {
            get { return client; }
            set { client = value; }
        }

        public DateTime Start
        {
            get { return startTime; }
            set { startTime = value; }
        }
    }
    // snippet-end:[s3.dotnet.putobject.async.clientstate]

    class Program
    {
        public static void Main(string[] args)
        {
            if (args.Length < 2)
            {
                Console.WriteLine("You must supply a bucket name");
                return;
            }

            TestPutObjectAsync(args[0]);
        }

        // snippet-start:[s3.dotnet.putobject.async.simplecallback]
        public static void SimpleCallback(IAsyncResult asyncResult)
        {
            Console.WriteLine("Finished PutObject operation with simple callback");
            Console.Write("\n\n");
        }
        // snippet-end:[s3.dotnet.putobject.async.simplecallback]

        // snippet-start:[s3.dotnet.putobject.async.callbackwithclient]
        public static void CallbackWithClient(IAsyncResult asyncResult)
        {
            try
            {
                AmazonS3Client s3Client = (AmazonS3Client)asyncResult.AsyncState;
                PutObjectResponse response = s3Client.EndPutObject(asyncResult);
                Console.WriteLine("Finished PutObject operation with client callback");
                Console.WriteLine("Service Response:");
                Console.WriteLine("-----------------");
                Console.WriteLine(response);
                Console.Write("\n\n");
            }
            catch (AmazonS3Exception s3Exception)
            {
                Console.WriteLine("Caught exception calling EndPutObject:");
                Console.WriteLine(s3Exception);
            }
        }
        // snippet-end:[s3.dotnet.putobject.async.callbackwithclient]

        // snippet-start:[s3.dotnet.putobject.async.callbackwithstate]
        public static void CallbackWithState(IAsyncResult asyncResult)
        {
            try
            {
                ClientState state = asyncResult.AsyncState as ClientState;
                AmazonS3Client s3Client = (AmazonS3Client)state.Client;
                PutObjectResponse response = state.Client.EndPutObject(asyncResult);
                Console.WriteLine(
                   "Finished PutObject operation with state callback that started at {0}",
                   (DateTime.Now - state.Start).ToString() + state.Start);
                Console.WriteLine("Service Response:");
                Console.WriteLine("-----------------");
                Console.WriteLine(response);
                Console.Write("\n\n");
            }
            catch (AmazonS3Exception s3Exception)
            {
                Console.WriteLine("Caught exception calling EndPutObject:");
                Console.WriteLine(s3Exception);
            }
        }
        // snippet-end:[s3.dotnet.putobject.async.callbackwithstate]

        // snippet-start:[s3.dotnet.putobject.async.testputobjectasync.start]
        public static void TestPutObjectAsync(string bucket)
        {
            // Create a client
            AmazonS3Client client = new AmazonS3Client();

            PutObjectResponse response;
            IAsyncResult asyncResult;

            //
            // Create a PutObject request
            //
            // You will need to change the BucketName below in order to run this
            // sample code.
            //
            PutObjectRequest request = new PutObjectRequest
            {
                BucketName = bucket,
                Key = "Item",
                ContentBody = "This is sample content..."
            };
            // snippet-end:[s3.dotnet.putobject.async.testputobjectasync.start]

            response = client.PutObject(request);
            Console.WriteLine("Finished PutObject operation for {0}.", request.Key);
            Console.WriteLine("Service Response:");
            Console.WriteLine("-----------------");
            Console.WriteLine("{0}", response);
            Console.Write("\n\n");

            request.Key = "Item1";

            // snippet-start:[s3.dotnet.putobject.async.testputobjectasync.beginputobject]
            asyncResult = client.BeginPutObject(request, null, null);
            while (!asyncResult.IsCompleted)
            {
                //
                // Do some work here
                //
            }
            try
            {
                response = client.EndPutObject(asyncResult);
            }
            catch (AmazonS3Exception s3Exception)
            {
                Console.WriteLine("Caught exception calling EndPutObject:");
                Console.WriteLine(s3Exception);
            }
            // snippet-end:[s3.dotnet.putobject.async.testputobjectasync.beginputobject]

            Console.WriteLine("Finished Async PutObject operation for {0}.", request.Key);
            Console.WriteLine("Service Response:");
            Console.WriteLine("-----------------");
            Console.WriteLine(response);
            Console.Write("\n\n");

            request.Key = "Item2";

            // snippet-start:[s3.dotnet.putobject.async.testputobjectasync.simplecallback]
            asyncResult = client.BeginPutObject(request, SimpleCallback, null);
            // snippet-end:[s3.dotnet.putobject.async.testputobjectasync.simplecallback]

            request.Key = "Item3";

            // snippet-start:[s3.dotnet.putobject.async.testputobjectasync.callbackwithclient]
            asyncResult = client.BeginPutObject(request, CallbackWithClient, client);
            // snippet-end:[s3.dotnet.putobject.async.testputobjectasync.callbackwithclient]

            request.Key = "Item4";

            // snippet-start:[s3.dotnet.putobject.async.testputobjectasync.callbackwithstate]
            asyncResult = client.BeginPutObject(request, CallbackWithState,
               new ClientState { Client = client, Start = DateTime.Now });
            // snippet-end:[s3.dotnet.putobject.async.testputobjectasync.callbackwithstate]

            Thread.Sleep(TimeSpan.FromSeconds(5));
        }
    }
}
// snippet-end:[s3.dotnet.putobject.async.complete]