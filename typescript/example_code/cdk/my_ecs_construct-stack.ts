// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include my_ecs_construct.ts, which should be in the bin directory.]
// snippet-comment:[This this should go in the lib directory.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Creates a Fargate service with versioning enabled.]
// snippet-keyword:[CDK V1.0.0]
// snippet-keyword:[AWS CDK]
// snippet-keyword:[aws-ec2.Vpc function]
// snippet-keyword:[TypeScript]
// snippet-sourcesyntax:[javascript]
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
// snippet-start:[cdk.typescript.my_ecs_construct-stack]
import core = require("@aws-cdk/core");
// snippet-start:[cdk.typescript.my_ecs_construct-stack.imports]
import ec2 = require("@aws-cdk/aws-ec2");
import ecs = require("@aws-cdk/aws-ecs");
import ecs_patterns = require("@aws-cdk/aws-ecs-patterns");
// snippet-end:[cdk.typescript.my_ecs_construct-stack.imports]

// snippet-start:[cdk.typescript.my_ecs_construct-stack.class]
export class MyEcsConstructStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    // snippet-start:[cdk.typescript.my_ecs_construct.create_fargate_service]
    const vpc = new ec2.Vpc(this, "MyVpc", {
      maxAzs: 3 // Default is all AZs in region
    });

    const cluster = new ecs.Cluster(this, "MyCluster", {
      vpc: vpc
    });

    // Create a load-balanced Fargate service and make it public
    new ecs_patterns.ApplicationLoadBalancedFargateService(this, "MyFargateService", {
      cluster: cluster, // Required
      cpu: 512, // Default is 256
      desiredCount: 6, // Default is 1
      image: ecs.ContainerImage.fromRegistry("amazon/amazon-ecs-sample"), // Required
      memoryLimitMiB: 2048, // Default is 512
      publicLoadBalancer: true // Default is false
    });
    // snippet-end:[cdk.typescript.my_ecs_construct.create_fargate_service]
  }
}
// snippet-end:[cdk.typescript.my_ecs_construct-stack.class]
// snippet-end:[cdk.typescript.my_ecs_construct-stack]
