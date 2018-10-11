//snippet-sourceauthor: [Doug-AWS]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

class HelloCdkStack extends cdk.Stack {
    constructor(parent: cdk.App, name: string, props?: cdk.StackProps) {
        super(parent, name, props);

        const queue = new sqs.Queue(this, 'HelloCdkQueue', {
            visibilityTimeoutSec: 300
        });

        const topic = new sns.Topic(this, 'HelloCdkTopic');

        topic.subscribeQueue(queue);
    }
}

const app = new cdk.App(process.argv);

new HelloCdkStack(app, 'HelloCdkStack');

process.stdout.write(app.run());
