/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

Purpose:
redshift-create-cluster.ts demonstrates how to create an Amazon Redshift cluster.

Inputs (replace in code):
- REGION: The AWS Region
- CLUSTER_NAME:  The name of the cluster
- NODE_TYPE: The node type to be provisioned for the cluster, for example, dc2.large
- CLUSTER_TYPE: The type of the cluster. When cluster type is specified a 'single-node', the NumberOfNodes parameter is not required
- IAM_ROLE_ARN: Optional - the Amazon Resource Name (ARN) of an IAM role with permissions your cluster needs to access other AWS services on your behalf, such as Amazon S3.
- CLUSTER_SUBNET_GROUPNAME: Optional - the name of a cluster subnet group to associate with this cluster. Defaults to 'default' if not specified.
- DATABASE_NAME: Optional - defaults to 'dev' if not specified
- PORT_NUMBER: Optional - defaults to '5439' if not specified

For more information about these and additional parameters, see https://docs.aws.amazon.com/redshift/latest/APIReference/API_CreateCluster.html.

Running the code:
ts-node redshift-create-cluster.ts
*/

// snippet-start:[redshift.javascript.redshift-create-clusterV3]
// Import required AWS SDK clients and commands for Node.js
const {
  RedshiftClient,
  CreateClusterCommand,
} = require("@aws-sdk/client-redshift-node");

// Set the AWS Region
const REGION = "REGION";

const params = {
  ClusterIdentifier: "CLUSTER_NAME", // Required
  NodeType: "NODE_TYPE", //Required
  MasterUsername: "MASTER_USER_NAME", // Required - must be lowercase
  MasterUserPassword: "MASTER_USER_PASSWORD", // Required - must contain at least one uppercase leeter, and one number
  ClusterType: "CLUSTER_TYPE", // Required
  IAMRoleARN: "IAM_ROLE_ARN", // Optional - the ARN of an IAM role with permissions your cluster needs to access other AWS services on your behalf, such as Amazon S3.
  ClusterSubnetGroupName: "CLUSTER_SUBNET_GROUPNAME", //Optional - the name of a cluster subnet group to be associated with this cluster. Defaults to 'default' if not specified.
  DBName: "DATABASE_NAME", // Optional - defaults to 'dev' if not specified
  Port: "PORT_NUMBER", // Optional - defaults to '5439' if not specified
};

// Create an Amazon Redshift client service object
const redshift = new RedshiftClient(REGION);

const run = async () => {
  try {
    const data = await redshift.send(new CreateClusterCommand(params));
    console.log(
      "Cluster " + data.Cluster.ClusterIdentifier + " successfully created"
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[redshift.javascript.redshift-create-clusterV3]

