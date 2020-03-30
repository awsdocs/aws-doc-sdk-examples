// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This goes in the lib dir.]
// snippet-comment:[This is a full sample when you include HelloCdk.ts, which goes in the bin dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[HelloCdk-stack.ts creates a stack with an SQS queue, SNS topic, subscribes the queue to the topic, and sets a CloudWatch metric and alarm on the SQS queue.]
// snippet-keyword:[CDK V1.0.0]
// snippet-keyword:[sqs.Queue function]
// snippet-keyword:[sns.Topic function]
// snippet-keyword:[Topic.subscribeQueue function]
// snippet-keyword:[CloudWatch.Metric function]
// snippet-keyword:[CloudWatch.Alarm function]
// snippet-keyword:[TypeScript]
// snippet-service:[cdk]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-7-11]
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
import * as core from "@aws-cdk/core";
import * as cloudwatch from "@aws-cdk/aws-cloudwatch";
import * as sns from "@aws-cdk/aws-sns";
import * as sqs from "@aws-cdk/aws-sqs";
import * as subscriptions from "@aws-cdk/aws-sns-subscriptions";

export class HelloCdkStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    const queue = new sqs.Queue(this, "HelloCdkQueue", {
      visibilityTimeout: core.Duration.seconds(300)
    });

    const topic = new sns.Topic(this, "HelloCdkTopic");

    const sub = new subscriptions.SqsSubscription(queue, {});
    sub.bind(topic);

    // Raise an alarm if we have more than 100 messages available for retrieval
    // in two of the last three seconds

    // Do not change the spacing in the following example
    // otherwise you'll screw up the online docs
    // snippet-start:[cdk.typescript.HelloCdk-stack_alarm]
    const qMetric = queue.metric("ApproximateNumberOfMessagesVisible");

    new cloudwatch.Alarm(this, "Alarm", {
      metric: qMetric,
      threshold: 100,
      evaluationPeriods: 3,
      datapointsToAlarm: 2
    });
    // snippet-end:[cdk.typescript.HelloCdk-stack_alarm]
  }
}
// snippet-end:[cdk.typescript.HelloCdk-stack]
