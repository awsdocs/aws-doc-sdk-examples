/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/

using System;
using System.Collections.Generic;
using System.Collections;
using System.Linq;
using System.Text;
using System.IO;
 
using Amazon.CloudWatchLogs;
using Amazon.CloudWatchLogs.Model;
namespace AwsCloudWatchSample1
{
    class AmazonCloudWatchLogsSample
    {
        static void Main(string[] args)
        {

            var logClient = new AmazonCloudWatchLogsClient();
            // Add a new log group for testing
            const string newLogGroupName = "NewLogGroup";
            DescribeLogGroupsResponse dlgr = logClient.DescribeLogGroups();
            var groups = new List<LogGroup> { };
            groups = dlgr.LogGroups;
            LogGroup lg = new LogGroup();
            lg.LogGroupName = newLogGroupName;
            // Look for our new log group name to determine if we need to do setup
            LogGroup result = groups.Find(
                delegate (LogGroup bk)
                {
                    return bk.LogGroupName == newLogGroupName;
                }
                );
            if (result != null)
            {
                Console.WriteLine(result.LogGroupName + " found");
            }
            else
            {
                //Haven't seen this log group, set it up
                CreateLogGroupRequest clgr = new CreateLogGroupRequest(newLogGroupName);
                logClient.CreateLogGroup(clgr);
                // Create a file to sace next SequenceToken in
                File.CreateText("..\\..\\" + lg.LogGroupName + ".txt");
                CreateLogStreamRequest csr = new CreateLogStreamRequest(lg.LogGroupName, newLogGroupName);
                logClient.CreateLogStream(csr);
            }

            string tokenFile = "";

            try
            {

                Console.WriteLine(lg.LogGroupName);
                //Pick up the next sequence token from the last run
                tokenFile = lg.LogGroupName;
                StreamReader sr = File.OpenText("..\\..\\" + tokenFile + ".txt");
                string sequenceToken = sr.ReadLine();
                sr.Close();
                lg.RetentionInDays = 30;
                string groupName = lg.LogGroupName; ;
                TestMetricFilterRequest tmfr = new TestMetricFilterRequest();
                List<InputLogEvent> logEvents = new List<InputLogEvent>(3);
                InputLogEvent ile = new InputLogEvent();
                ile.Message = "Test Event 1";
                //DateTime dt = new DateTime(1394793518000);
                DateTime dt = new DateTime(2017, 01, 11) ;
                ile.Timestamp = dt;
                logEvents.Add(ile);
                ile.Message = "Test Event 2";
                logEvents.Add(ile);
                ile.Message = "This message also contains an Error";
                logEvents.Add(ile);
                DescribeLogStreamsRequest dlsr = new DescribeLogStreamsRequest(groupName);

                PutLogEventsRequest pler = new PutLogEventsRequest(groupName, tokenFile, logEvents);
                pler.SequenceToken = sequenceToken; //use last sequence token
                PutLogEventsResponse plerp = new PutLogEventsResponse();
                plerp = logClient.PutLogEvents(pler);
                Console.WriteLine("Next sequence token = " + plerp.NextSequenceToken);
                FileStream fs = File.OpenWrite("..\\..\\" + tokenFile + ".txt");
                fs.Position = 0;
                UTF8Encoding utf8 = new UTF8Encoding();
                Byte[] encodedBytes = utf8.GetBytes(plerp.NextSequenceToken);
                fs.Write(encodedBytes, 0, utf8.GetByteCount(plerp.NextSequenceToken));
                fs.Close();
                List<string> lem = new List<string>(1);
                lem.Add("Error");
                tmfr.LogEventMessages = lem;
                tmfr.FilterPattern = "Error";
                TestMetricFilterResponse tmfrp = new TestMetricFilterResponse();
                tmfrp = logClient.TestMetricFilter(tmfr);
                var results = new List<MetricFilterMatchRecord> { };
                results = tmfrp.Matches;
                Console.WriteLine("Found " + results.Count.ToString() + " match records");
                IEnumerator ie = results.GetEnumerator();
                while (ie.MoveNext())
                { 
                    MetricFilterMatchRecord mfmr = (MetricFilterMatchRecord)ie.Current;
                    Console.WriteLine("Event Message = " + mfmr.EventMessage);
                }
                Console.WriteLine("Metric filter test done");

            }
            catch (Exception e)
            {

                Console.WriteLine(e.Message);

            }

        }
    }
}
