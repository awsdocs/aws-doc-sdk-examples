/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDomainatasetGroup.js demonstrates how to create a domain dataset group with Amazon Personalize.
A domain dataset group is a dataset group containing preconfigured resources for different business domains and use cases.
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateDatasetGroup.html.

Inputs (replace in code):
- NAME
- DOMAIN

Running the code:
node createDomainDatasetGroup.js
*/

// snippet-start:[personalize.JavaScript.createDomainDatasetGroupV3]
// Get service clients module and commands using ES6 syntax.
import {  CreateDatasetGroupCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set domain dataset group params
export const domainDatasetGroupParams = {
  name: 'NAME',  /* required */
  domain: 'DOMAIN'   /* required for a domain dsg, specify ECOMMERCE or VIDEO_ON_DEMAND */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateDatasetGroupCommand(domainDatasetGroupParams));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createDomainDatasetGroupV3]
// For unit tests only.
// module.exports ={run, createDomainDatasetGroupParam};