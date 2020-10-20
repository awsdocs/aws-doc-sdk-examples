/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

Purpose:
redshift-describe-clusters.ts demonstrates how to return properties of provisioned Amazon Redshift clusters, including general
cluster properties, cluster database properties, maintenance and backup properties, and security and access properties.

Inputs (replace in code):
- REGION: The AWS Region
- CLUSTER_NAME:  The name of the cluster

For more information about these and additional parameters, see https://docs.aws.amazon.com/redshift/latest/APIReference/API_DescribeClusters.html.

Running the code:
ts-node redshift-describe-clusters.ts
*/

// snippet-start:[redshift.javascript.redshift-describe-clustersV3]
// Import required AWS SDK clients and commands for Node.js
const {
  RedshiftClient,
  DescribeClustersCommand
} = require("@aws-sdk/client-redshift-node");

// Set the AWS Region
const REGION = "REGION";

const params = {
  ClusterIdentifier: "CLUSTER_NAME",
};

// Create an Amazon Redshift client service object
const redshift = new RedshiftClient(REGION);

const run = async () => {
  try {
    const data = await redshift.send(new DescribeClustersCommand(params));
    console.log(data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[redshift.javascript.redshift-describe-clustersV3]

