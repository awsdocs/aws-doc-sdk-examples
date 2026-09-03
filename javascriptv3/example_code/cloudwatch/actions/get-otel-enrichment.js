// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.GetOTelEnrichment]
import { GetOTelEnrichmentCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Get the current OTel enrichment status for the account. Status is either
// "Running" or "Stopped".
const run = async () => {
  const command = new GetOTelEnrichmentCommand({});

  try {
    const response = await client.send(command);
    console.log(`OTel enrichment status is ${response.Status}.`);
    return response;
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.GetOTelEnrichment]
