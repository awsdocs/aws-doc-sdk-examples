/*** Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-sourcedescription:[EnableNotificationsTest.cs  adds a notification configuration to a bucket.]
// snippet-service:[s3]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[PutBucketNotificationRequest]
// snippet-keyword:[TopicConfiguration]
// snippet-keyword:[QueueConfiguration]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-04-30]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.dotNET.EnableNotificationsTest]

using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Amazon.DocSamples.S3
{
    class EnableNotificationsTest
    {
        private const string bucketName = "*** bucket name ***";
        private const string snsTopic = "*** SNS topic ARN ***";
        private const string sqsQueue = "*** SQS topic ARN ***";
        // Specify your bucket region (an example region is shown).
        private static readonly RegionEndpoint bucketRegion = RegionEndpoint.USWest2;
        private static IAmazonS3 client;

        public static void Main()
        {
            client = new AmazonS3Client(bucketRegion);
            EnableNotificationAsync().Wait();
        }

        static async Task EnableNotificationAsync()
        {
            try
            {
               PutBucketNotificationRequest request = new PutBucketNotificationRequest
                {
                    BucketName = bucketName
                };

                TopicConfiguration c = new TopicConfiguration
                {
                    Events = new List<EventType> { EventType.ObjectCreatedCopy },
                    Topic = snsTopic
                };
                request.TopicConfigurations = new List<TopicConfiguration>();
                request.TopicConfigurations.Add(c);
                request.QueueConfigurations = new List<QueueConfiguration>();
                request.QueueConfigurations.Add(new QueueConfiguration()
                {
                    Events = new List<EventType> { EventType.ObjectCreatedPut },
                    Queue = sqsQueue
                });
                
                PutBucketNotificationResponse response = await client.PutBucketNotificationAsync(request);
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine("Error encountered on server. Message:'{0}' ", e.Message);
            }
            catch (Exception e)
            {
                Console.WriteLine("Unknown error encountered on server. Message:'{0}' ", e.Message);
            }
        }
    }
}
// snippet-end:[s3.dotNET.EnableNotificationsTest]