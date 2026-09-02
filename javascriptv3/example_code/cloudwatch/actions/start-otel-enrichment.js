// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.StartOTelEnrichment]
import { StartOTelEnrichmentCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Turn on OTel enrichment for the account. Once enrichment is running, CloudWatch
// vended metrics that carry a resource identifier dimension - for example the EC2
// CPUUtilization metric with its InstanceId dimension - are decorated with resource
// ARN and resource tag labels, and become queryable with PromQL.
//
// Resource tags on telemetry must already be enabled for the account before you call
// this operation.
const run = async () => {
  const command = new StartOTelEnrichmentCommand({});

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.StartOTelEnrichment]
