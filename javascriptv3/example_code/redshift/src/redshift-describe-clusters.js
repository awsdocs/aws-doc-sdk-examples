/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

Purpose:
redshift-describe-clusters.js demonstrates how to return properties of provisioned Amazon Redshift clusters, including general
cluster properties, cluster database properties, maintenance and backup properties, and security and access properties.

Inputs (replace in code):
- CLUSTER_NAME:  The name of the cluster

For more information about these and additional parameters, see https://docs.aws.amazon.com/redshift/latest/APIReference/API_DescribeClusters.html.

Running the code:
node redshift-describe-clusters.js
*/

// snippet-start:[redshift.javascript.redshift-describe-clustersV3]
// Import required AWS SDK clients and commands for Node.js
import { DescribeClustersCommand }  from "@aws-sdk/client-redshift";
import { redshiftClient } from "./libs/redshiftClient.js";

const params = {
  ClusterIdentifier: "CLUSTER_NAME",
};

const run = async () => {
  try {
    const data = await redshiftClient.send(new DescribeClustersCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[redshift.javascript.redshift-describe-clustersV3]
// module.exports = { run, params };
