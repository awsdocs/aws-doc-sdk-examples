//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-comment:[This is a full sample when you include HelloCdk.ts, which goes in the bin dir.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Creates a stack with an SQS queue, SNS topic, and subscribes the queue to the topic.]
//snippet-keyword:[CDK V0.21.0]
//snippet-keyword:[sqs.Queue function]
//snippet-keyword:[sns.Topic function]
//snippet-keyword:[Topic.subscribeQueue function]
//snippet-keyword:[TypeScript]
//snippet-service:[cdk]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-1-8]
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
import cdk = require('@aws-cdk/cdk');
import sns = require('@aws-cdk/aws-sns');
import sqs = require('@aws-cdk/aws-sqs');

export class HelloCdkStack extends cdk.Stack {
    constructor(parent: cdk.App, name: string, props?: cdk.StackProps) {
        super(parent, name, props);

        const queue = new sqs.Queue(this, 'HelloCdkQueue', {
            visibilityTimeoutSec: 300
        });

        const topic = new sns.Topic(this, 'HelloCdkTopic');

        topic.subscribeQueue(queue);
    }
}
