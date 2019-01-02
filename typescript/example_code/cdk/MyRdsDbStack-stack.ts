//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-comment:[This is a full sample when you include MyRdsDbStack.ts, which goes in the bin dir.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Instantiates the stack in MyRdsDbStack.ts.]
//snippet-keyword:[CDK V0.21.0]
//snippet-keyword:[ec2.VpcNetwork function]
//snippet-keyword:[rds.DatabaseCluster function]
//snippet-keyword:[TypeScript]
//snippet-service:[cdk]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-12-27]
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
import ec2 = require('@aws-cdk/aws-ec2');
import rds = require('@aws-cdk/aws-rds');

export class MyRdsDbStack extends cdk.Stack {
  constructor(parent: cdk.App, name: string, props?: cdk.StackProps) {
    super(parent, name, props);

    const vpc = new ec2.VpcNetwork(this, 'VPC');

    new rds.DatabaseCluster(this, 'MyRdsDb', {
      defaultDatabaseName: 'MyAuroraDatabase',
      masterUser: {
        username: 'admin',
        password: '123456'
      },
      engine: rds.DatabaseClusterEngine.Aurora,
      instanceProps: {
        instanceType: new ec2.InstanceTypePair(ec2.InstanceClass.Burstable2, ec2.InstanceSize.Small),
        vpc: vpc,
        vpcPlacement: {
          subnetsToUse: ec2.SubnetType.Public
        }
      }
    });
  }
}
