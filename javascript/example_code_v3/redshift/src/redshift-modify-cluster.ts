/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/redshift-examples.html.

Purpose:
redshift-modify-cluster.ts demonstrates how to modify an AWS Redshift cluster. This example shows how to change the main user password.
For more information about other cluster settings you can modify, see https://docs.aws.amazon.com/redshift/latest/APIReference/API_ModifyCluster.html.

Inputs (replace in code):
- REGION: The AWS Region
- CLUSTER_NAME:  The name of the cluster

Running the code:
ts-node redshift-describe-clusters.ts
*/

// snippet-start:[redshift.javascript.redshift-describe-clustersV3]

// Import required AWS SDK clients and commands for Node.js
const {
    Redshift,
    ModifyClusterCommand
} = require("@aws-sdk/client-redshift-node");

// Set the AWS Region
const REGION = "REGION";

params = {
    ClusterIdentifier: "CLUSTER_NAME",
    MasterUserPassword: "NEW_MASTER_USER_PASSWORD"
};

// Create an Amazon Redshift client service object
const redshift = new Redshift(REGION);

const run = async () => {
    try {
        const data = await redshift.send(new ModifyClusterCommand(params));
        console.log(data.Cluster.ClusterIdentifier + ' was modified.');

    }
    catch(err){
        console.log("Error", err);
    }
};
run();
// snippet-end:[redshift.javascript.redshift-describe-clustersV3]
module.exports = {run};  //for unit tests only
