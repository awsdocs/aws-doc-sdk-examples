// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This goes in the lib dir.]
// snippet-comment:[This is a full sample when you include HelloCdk.ts, which goes in the bin dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[HelloCdk-stack.ts creates a stack with an SQS queue, SNS topic, subscribes the queue to the topic, and sets a CloudWatch metric and alarm on the SQS queue.]
// snippet-keyword:[CDK V0.24.1]
// snippet-keyword:[sqs.Queue function]
// snippet-keyword:[sns.Topic function]
// snippet-keyword:[Topic.subscribeQueue function]
// snippet-keyword:[CloudWatch.Metric function]
// snippet-keyword:[CloudWatch.Alarm function]
// snippet-keyword:[TypeScript]
// snippet-service:[cdk]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-2-13]
// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of the
// License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
// snippet-start:[cdk.typescript.HelloCdk-stack]
import cdk = require('@aws-cdk/cdk');
import cloudwatch = require('@aws-cdk/aws-cloudwatch');
import sns = require('@aws-cdk/aws-sns');
import sqs = require('@aws-cdk/aws-sqs');

export class HelloCdkStack extends cdk.Stack {
    constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const queue = new sqs.Queue(this, 'HelloCdkQueue', {
            visibilityTimeoutSec: 300
        });

        const topic = new sns.Topic(this, 'HelloCdkTopic');

        topic.subscribeQueue(queue);

// snippet-start:[cdk.typescript.HelloCdk-stack_add_metric_and_alarm]
        // Add a metric to keep track of the number of messages available for retrieval from the queue
        // See https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-available-cloudwatch-metrics.html
        // for a list of available metrics for SQS
        const metric = new cloudwatch.Metric({
            namespace: 'AWS/SQS',
            metricName: 'ApproximateNumberOfMessagesVisible',
            dimensions: { QueueName: queue.queueName }  // WAS: getAtt('QueueName')
          });
          
          // Raise an alarm if we have more than 100 messages available for retrieval 
          // in two of the last three seconds
          new cloudwatch.Alarm(this, 'Alarm', {
            metric,
            threshold: 100,
            evaluationPeriods: 3,
            datapointsToAlarm: 2,
          });
// snippet-end:[cdk.typescript.HelloCdk-stack_add_metric_and_alarm]          
    }
}
// snippet-end:[cdk.typescript.HelloCdk-stack]
