/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

Purpose:
redshift-modify-cluster.ts demonstrates how to modify an Amazon Redshift cluster. This example shows how to change the main user password.
For more information about other cluster settings you can modify, see https://docs.aws.amazon.com/redshift/latest/APIReference/API_ModifyCluster.html.

Inputs (replace in code):
- REGION: The AWS Region
- CLUSTER_NAME:  The name of the cluster

Running the code:
ts-node redshift-modify-cluster.ts
*/

// snippet-start:[redshift.javascript.redshift-modify-clustersV3]
// Import required AWS SDK clients and commands for Node.js
const {
  RedshiftClient,
  ModifyClusterCommand
} = require("@aws-sdk/client-redshift-node");

// Set the AWS Region
const REGION = "REGION";

// Set the parameters
const params = {
  ClusterIdentifier: "CLUSTER_NAME",
  MasterUserPassword: "NEW_MASTER_USER_PASSWORD",
};

// Create an Amazon Redshift client service object
const redshift = new RedshiftClient(REGION);

const run = async () => {
  try {
    const data = await redshift.send(new ModifyClusterCommand(params));
    console.log(data.Cluster.ClusterIdentifier + " was modified.");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[redshift.javascript.redshift-modify-clustersV3]

