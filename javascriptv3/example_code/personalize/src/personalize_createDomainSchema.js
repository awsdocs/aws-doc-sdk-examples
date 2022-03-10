/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDomainSchema.js demonstrates how to create a schema for a dataset in domain dataset
group with Amazon Personalize.
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateSchema.html.

Inputs (replace in code):
- NAME
- DOMAIN
- SCHEMA_PATH

Running the code:
node createDomanSchema.js
*/

// snippet-start:[personalize.JavaScript.createDomainSchemaV3]
// Get service clients module and commands using ES6 syntax.
import { CreateSchemaCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

import fs from 'fs';

let schemaFilePath = "SCHEMA_PATH";
let mySchema = "";

try {
  mySchema = fs.readFileSync(schemaFilePath).toString();
} catch (err) {
  mySchema = 'TEST' // for unit tests.
}

// set the domain schema params
export const createDomainSchemaParam = {
  name: 'NAME', /* required */
  schema: mySchema, /* required */
  domain: 'DOMAIN'   /* required for a domain dsg, specify ECOMMERCE or VIDEO_ON_DEMAND */
};

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateSchemaCommand(createDomainSchemaParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createDomainSchemaV3]
// For unit tests only.
// module.exports ={run, createDomainSchemaParam};