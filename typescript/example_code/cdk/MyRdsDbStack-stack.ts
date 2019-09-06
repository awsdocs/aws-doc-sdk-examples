// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-comment:[This is a full sample when you include MyRdsDbStack.ts, which goes in the bin dir.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Instantiates the stack in MyRdsDbStack.ts.]
// snippet-keyword:[CDK V1.0.0]
// snippet-keyword:[AWS CDK]
// snippet-keyword:[aws-ec2.Vpc function]
// snippet-keyword:[aws-rds.DatabaseCluster function]
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
// snippet-start:[cdk.typescript.MyRdsDbStack-stack]
import core = require("@aws-cdk/core");
import ec2 = require("@aws-cdk/aws-ec2");
import rds = require("@aws-cdk/aws-rds");

export class MyRdsDbStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    const vpc = new ec2.Vpc(this, "VPC");

    new rds.DatabaseCluster(this, "MyRdsDb", {
      defaultDatabaseName: "MyAuroraDatabase",
      masterUser: {
        username: "admin"
      },
      engine: rds.DatabaseClusterEngine.AURORA,
      instanceProps: {
        instanceType: new ec2.InstanceType(
          ec2.InstanceClass.BURSTABLE2
        ),
        vpc: vpc,
        vpcSubnets: {
          subnetType: ec2.SubnetType.PUBLIC
        }
      }
    });
  }
}
// snippet-end:[cdk.typescript.MyRdsDbStack-stack]
