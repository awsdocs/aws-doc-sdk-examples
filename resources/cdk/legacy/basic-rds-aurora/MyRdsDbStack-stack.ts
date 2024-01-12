// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
import * as core from "@aws-cdk/core";
import * as ec2 from "@aws-cdk/aws-ec2";
import * as rds from "@aws-cdk/aws-rds";

export class MyRdsDbStack extends core.Stack {
  constructor(scope: core.App, id: string, props?: core.StackProps) {
    super(scope, id, props);

    const vpc = new ec2.Vpc(this, "VPC");

    new rds.DatabaseCluster(this, "MyRdsDb", {
      defaultDatabaseName: "MyAuroraDatabase",
      // credentials: rds.Credentials.fromGeneratedSecret('clusteradmin'), // Optional - will default to 'admin' username and generated password
      engine: rds.DatabaseClusterEngine.AURORA,
      instanceProps: {
        instanceType: new ec2.InstanceType(ec2.InstanceClass.BURSTABLE2),
        vpc: vpc,
        vpcSubnets: {
          subnetType: ec2.SubnetType.PUBLIC,
        },
      },
    });
  }
}
// snippet-end:[cdk.typescript.MyRdsDbStack-stack]
