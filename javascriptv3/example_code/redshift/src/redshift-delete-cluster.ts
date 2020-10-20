/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

Purpose:
redshift-delete-cluster.ts demonstrates how to delete an Amazon RedShift cluster.

Inputs (replace in code):
- REGION: The AWS Region
- CLUSTER_NAME:  The name of the cluster
- SkipFinalClusterSnapshot: Determines whether a final snapshot of the cluster is created before Amazon Redshift deletes the cluster.
- CLUSTER_SNAPSHOT_ID: Required if 'SkipFinalClusterSnapshot' is 'false', for example mycluster-xxxx-xx-xx-xx-xx-xx

For more information about these and additional parameters, see https://docs.aws.amazon.com/redshift/latest/APIReference/API_CreateCluster.html.

Running the code:
ts-node redshift-create-cluster.ts
*/

// snippet-start:[redshift.javascript.redshift-delete-clusterV3]
// Import required AWS SDK clients and commands for Node.js
const {
  RedshiftClient,
  DeleteClusterCommand,
} = require("@aws-sdk/client-redshift-node");

// Set the AWS Region
const REGION = "REGION";

const params = {
  ClusterIdentifier: "CLUSTER_NAME",
  SkipFinalClusterSnapshot: false,
  FinalClusterSnapshotIdentifier: "CLUSTER_SNAPSHOT_ID",
};

// Create an Amazon Redshift client service object
const redshift = new RedshiftClient(REGION);

const run = async () => {
  try {
    const data = await redshift.send(new DeleteClusterCommand(params));
    console.log("Cluster deleted");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[redshift.javascript.redshift-delete-clusterV3]

