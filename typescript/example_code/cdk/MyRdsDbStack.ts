 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[TypeScript]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
import ec2 = require('@aws-cdk/aws-ec2');
import rds = require('@aws-cdk/aws-rds');

class MyRdsDbStack extends cdk.Stack {
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

const app = new cdk.App(process.argv);

new MyRdsDbStack(app, 'MyRdsDbStack');

process.stdout.write(app.run());
