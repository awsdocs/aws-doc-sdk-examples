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

// snippet-sourcedescription:[GetObjectTest.cs demonstrates how to download an object from an S3 bucket.]
// snippet-service:[s3]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[GET Object]
// snippet-keyword:[GetObjectRequest]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-04-30]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.dotNET.GetObjectTest]

using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.IO;
using System.Threading.Tasks;

namespace Amazon.DocSamples.S3
{
    class GetObjectTest
    {
        private const string bucketName = "*** bucket name ***";
        private const string keyName = "*** object key ***";
        // Specify your bucket region (an example region is shown).
        private static readonly RegionEndpoint bucketRegion = RegionEndpoint.USWest2;
        private static IAmazonS3 client;

        public static void Main()
        {
            client = new AmazonS3Client(bucketRegion);
            ReadObjectDataAsync().Wait();
        }

        static async Task ReadObjectDataAsync()
        {
            string responseBody = "";
            try
            {
                GetObjectRequest request = new GetObjectRequest
                {
                    BucketName = bucketName,
                    Key = keyName
                };
                using (GetObjectResponse response = await client.GetObjectAsync(request))
                using (Stream responseStream = response.ResponseStream)
                using (StreamReader reader = new StreamReader(responseStream))
                {
                    string title = response.Metadata["x-amz-meta-title"]; // Assume you have "title" as medata added to the object.
                    string contentType = response.Headers["Content-Type"];
                    Console.WriteLine("Object metadata, Title: {0}", title);
                    Console.WriteLine("Content type: {0}", contentType);

                    responseBody = reader.ReadToEnd(); // Now you process the response body.
                }
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine("Error encountered ***. Message:'{0}' when writing an object", e.Message);
            }
            catch (Exception e)
            {
                Console.WriteLine("Unknown encountered on server. Message:'{0}' when writing an object", e.Message);
            }
        }
    }
}
// snippet-end:[s3.dotNET.GetObjectTest]
