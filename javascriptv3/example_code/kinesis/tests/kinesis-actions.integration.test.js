// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { resolve } from "node:path";

import { describe, it, beforeAll, afterAll } from "vitest";
import {
  CreateStackCommand,
  DeleteStackCommand,
  waitUntilStackCreateComplete,
  waitUntilStackDeleteComplete,
  CloudFormationClient,
} from "@aws-sdk/client-cloudformation";

import { getCfnOutputs } from "@aws-doc-sdk-examples/lib/sdk/cfn-outputs.js";

import { main } from "../actions/put-records.js";
import { readFile } from "node:fs/promises";

describe("kinesis-actions", () => {
  const stackName = "kinesis-example-stack";
  const cfnClient = new CloudFormationClient({});
  /** @type { Record<string,string> } */
  let outputs;

  beforeAll(async () => {
    const stackTemplate = await readFile(
      resolve(import.meta.dirname, "../stack.yaml"),
    );
    await cfnClient.send(
      new CreateStackCommand({
        StackName: stackName,
        TemplateBody: stackTemplate.toString(),
        Capabilities: ["CAPABILITY_IAM"],
      }),
    );
    await waitUntilStackCreateComplete(
      { client: cfnClient },
      { StackName: stackName },
    );
    outputs = await getCfnOutputs(stackName);
  });

  afterAll(async () => {
    await cfnClient.send(new DeleteStackCommand({ StackName: stackName }));
    await waitUntilStackDeleteComplete(
      { client: cfnClient },
      { StackName: stackName },
    );
  });

  it("should successfully use the PutRecords action", async () => {
    // Output key comes from stack.yaml in the kinesis directory.
    await main({ streamArn: outputs.ExampleStreamArn });
  });
});
