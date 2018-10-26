 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cloudwatch]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


﻿/*******************************************************************************
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
using Amazon;
using Amazon.CloudWatch;
using Amazon.CloudWatch.Model;
using Amazon.CloudWatchLogs;
using Amazon.CloudWatchLogs.Model;
using Amazon.CloudWatchEvents;
using Amazon.CloudWatchEvents.Model;

namespace AWSSDKDocSamples.CloudWatch
{
    class CloudWatchSamples
    {
        static void Main(string[] args)
        {
            // This project contains the following examples:
            //PutRuleExample();
            //PutTargetExample();
            //PutEventExample();
            //EnableAlarmAction("instanceId", "customerAccount");
            //DisableAlarmActions();
            //PutMetricAlarm();
            //CloudWatchMetricsTest();
            //DescribeAlarmForMetric();
            //PutSubscriptionFilters();
            //DescribeSubscriptionFilters();
            //DescribeAlarms();
            //DeleteAlarm(new List < string > { "alarmNames"});
        }
        public static void PutRuleExample()
        {
            AmazonCloudWatchEventsClient client = new AmazonCloudWatchEventsClient();

            var putRuleRequest = new PutRuleRequest
            {
                Name = "DEMO_EVENT",
                RoleArn = "IAM_ROLE_ARN",
                ScheduleExpression = "rate(5 minutes)",
                State = RuleState.ENABLED
            };

            var putRuleResponse = client.PutRule(putRuleRequest);
            Console.WriteLine("Successfully set the rule {0}", putRuleResponse.RuleArn);
        }
        public static void PutTargetExample()
        {
            AmazonCloudWatchEventsClient client = new AmazonCloudWatchEventsClient();

            var putTargetRequest = new PutTargetsRequest
            {
                Rule = "DEMO_EVENT",
                Targets =
                {
                    new Target { Arn = "LAMBDA_FUNCTION_ARN", Id = "myCloudWatchEventsTarget"}
                }
            };

            client.PutTargets(putTargetRequest);
        }
        public static void PutEventExample()
        {
            AmazonCloudWatchEventsClient client = new AmazonCloudWatchEventsClient();

            var putEventsRequest = new PutEventsRequest
            {
                Entries = new List<PutEventsRequestEntry>
                {
                    new PutEventsRequestEntry
                    {
                        Detail = @"{ ""key1"" : ""value1"", ""key2"" : ""value2"" }",
                        DetailType = "appRequestSubmitted",
                        Resources =
                        {
                            "RESOURCE_ARN"
                        },
                        Source = "com.compnay.myapp"
                    }
                }
            };

            client.PutEvents(putEventsRequest);
        }
        public static void EnableAlarmAction(string instanceId, string customerAccount)
        {
            using (var client = new AmazonCloudWatchClient(Amazon.RegionEndpoint.USWest2))
            {
                client.PutMetricAlarm(new PutMetricAlarmRequest
                {
                    AlarmName = "Web_Server_CPU_Utilization",
                    ComparisonOperator = ComparisonOperator.GreaterThanThreshold,
                    EvaluationPeriods = 1,
                    MetricName = "CPUUtilization",
                    Namespace = "AWS/EC2",
                    Period = 60,
                    Statistic = Statistic.Average,
                    Threshold = 70.0,
                    ActionsEnabled = true,
                    AlarmActions = new List<string> { "arn:aws:swf:us-west-2:" + customerAccount + ":action/actions/AWS_EC2.InstanceId.Reboot/1.0" },
                    AlarmDescription = "Alarm when server CPU exceeds 70%",
                    Dimensions = new List<Dimension>
                    {
                        new Dimension { Name = "InstanceId", Value = instanceId }
                    },
                    Unit = StandardUnit.Seconds
                });

                client.EnableAlarmActions(new EnableAlarmActionsRequest
                {
                    AlarmNames = new List<string> { "Web_Server_CPU_Utilization" }
                });

                MetricDatum metricDatum = new MetricDatum
                { MetricName = "CPUUtilization" };

                PutMetricDataRequest putMetricDatarequest = new PutMetricDataRequest
                {
                    MetricData = new List<MetricDatum> { metricDatum }
                };
                client.PutMetricData(putMetricDatarequest);

            }
        }
        public static void DisableAlarmActions()
        {
            using (var client = new AmazonCloudWatchClient(RegionEndpoint.USWest2))
            {
                client.DisableAlarmActions(new DisableAlarmActionsRequest
                {
                    AlarmNames = new List<string> { "Web_Server_CPU_Utilization" }
                });
            }
        }
        public static void PutMetricAlarm()
        {
            var client = new AmazonCloudWatchClient(RegionEndpoint.USWest2);
            client.PutMetricAlarm(new PutMetricAlarmRequest
            {
                AlarmName = "Web_Server_CPU_Utilization",
                ComparisonOperator = ComparisonOperator.GreaterThanThreshold,
                EvaluationPeriods = 1,
                MetricName = "CPUUtilization",
                Namespace = "AWS/EC2",
                Period = 60,
                Statistic = Statistic.Average,
                Threshold = 70.0,
                ActionsEnabled = true,
                AlarmActions = new List<string> { "arn:aws:swf:us-west-2:" + "customerAccount" + ":action/actions/AWS_EC2.InstanceId.Reboot/1.0" },
                AlarmDescription = "Alarm when server CPU exceeds 70%",
                Dimensions = new List<Dimension>
                    {
                        new Dimension { Name = "InstanceId", Value = "INSTANCE_ID" }
                    },
                Unit = StandardUnit.Seconds
            });

        }
        public static void CloudWatchMetricsTest()
        {
            var logGroupName = "LogGroupName";
            DimensionFilter dimensionFilter = new DimensionFilter()
            {
                Name = logGroupName
            };
            var dimensionFilterList = new List<DimensionFilter>();
            dimensionFilterList.Add(dimensionFilter);

            var dimension = new Dimension
            {
                Name = "UniquePages",
                Value = "URLs"
            };
            using (var cw = new AmazonCloudWatchClient(RegionEndpoint.USWest2))
            {
                var listMetricsResponse = cw.ListMetrics(new ListMetricsRequest
                {
                    Dimensions = dimensionFilterList,
                    MetricName = "IncomingLogEvents",
                    Namespace = "AWS/Logs"
                });

                Console.WriteLine(listMetricsResponse.Metrics);

                cw.PutMetricData(new PutMetricDataRequest
                {
                    MetricData = new List<MetricDatum>{new MetricDatum
                    {
                        MetricName = "PagesVisited",
                        Dimensions = new List<Dimension>{dimension},
                        Unit = "None",
                        Value = 1.0
                    }},
                    Namespace = "SITE/TRAFFIC"
                });

                var describeMetricsResponse = cw.DescribeAlarmsForMetric(new DescribeAlarmsForMetricRequest
                {
                    MetricName = "PagesVisited",
                    Dimensions = new List<Dimension>() { dimension },
                    Namespace = "SITE/TRAFFIC"
                });

                Console.WriteLine(describeMetricsResponse.MetricAlarms);
            }

        }
        public static void DescribeAlarmForMetric()
        {
            var dimension = new Dimension
            {
                Name = "UniquePages",
                Value = "URLs"
            };
            var cloudWatch = new AmazonCloudWatchClient(RegionEndpoint.USWest2);
            var describeMetricsResponse = cloudWatch.DescribeAlarmsForMetric(new DescribeAlarmsForMetricRequest
            {
                MetricName = "PagesVisited",
                Dimensions = new List<Dimension>() { dimension },
                Namespace = "SITE/TRAFFIC"
            });

            Console.WriteLine(describeMetricsResponse.MetricAlarms);
        }
        public static void PutSubscriptionFilters()
        {
            var client = new AmazonCloudWatchLogsClient();
            var request = new Amazon.CloudWatchLogs.Model.PutSubscriptionFilterRequest()
            {
                DestinationArn = "LAMBDA_FUNCTION_ARN",
                FilterName = "FILTER_NAME",
                FilterPattern = "ERROR",
                LogGroupName = "Log_Group"
            };
            try
            {
                var response = client.PutSubscriptionFilter(request);
            }
            catch (InvalidParameterException e)
            {
                Console.WriteLine(e.Message);
            }


        }
        public static void DescribeSubscriptionFilters()
        {
            var client = new AmazonCloudWatchLogsClient();
            var request = new Amazon.CloudWatchLogs.Model.DescribeSubscriptionFiltersRequest()
            {
                LogGroupName = "GROUP_NAME",
                Limit = 5
            };
            try
            {
                var response = client.DescribeSubscriptionFilters(request);
            }
            catch (Amazon.CloudWatchLogs.Model.ResourceNotFoundException e)
            {
                Console.WriteLine(e.Message);
            }

        }
        public static void DeleteSubscriptionFilter()
        {
            var client = new AmazonCloudWatchLogsClient();
            var request = new Amazon.CloudWatchLogs.Model.DeleteSubscriptionFilterRequest()
            {
                LogGroupName = "GROUP_NAME",
                FilterName = "FILTER"
            };
            try
            {
                var response = client.DeleteSubscriptionFilter(request);
            }
            catch (Amazon.CloudWatchLogs.Model.ResourceNotFoundException e)
            {
                Console.WriteLine(e.Message);
            }

        }
        public static void DescribeAlarms()
        {
            using (var cloudWatch = new AmazonCloudWatchClient(RegionEndpoint.USWest2))
            {
                var request = new DescribeAlarmsRequest();
                request.StateValue = "INSUFFICIENT_DATA";
                request.AlarmNames = new List<string> { "Alarm1", "Alarm2" };
                do
                {
                    var response = cloudWatch.DescribeAlarms(request);
                    foreach (var alarm in response.MetricAlarms)
                    {
                        Console.WriteLine(alarm.AlarmName);
                    }
                    request.NextToken = response.NextToken;
                } while (request.NextToken != null);
            }
        }
        public static void DeleteAlarm(List<string> alarmNames)
        {
            using (var cloudWatch = new AmazonCloudWatchClient(RegionEndpoint.USWest2))
            {
                var response = cloudWatch.DeleteAlarms(
                    new DeleteAlarmsRequest
                    {
                        AlarmNames = alarmNames
                    });
            }
        }

    }
    }
