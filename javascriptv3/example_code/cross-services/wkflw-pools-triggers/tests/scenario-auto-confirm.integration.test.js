// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { readFile } from "node:fs/promises";
import { dirname, join } from "node:path";

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  Capability,
  CloudFormationClient,
  CreateStackCommand,
  DeleteStackCommand,
  DescribeStacksCommand,
} from "@aws-sdk/client-cloudformation";
import { fileURLToPath } from "node:url";
import { retry } from "@aws-doc-sdk-examples/lib/utils/util-timers.js";
import { AutoConfirm } from "../scenario-auto-confirm.js";

const __dirname = dirname(fileURLToPath(import.meta.url));

describe("Scenario - AutoConfirm", () => {
  const cloudformationClient = new CloudFormationClient({
    region: "us-east-1",
  });
  const stackName = "PoolsAndTriggersStack";

  beforeAll(async () => {
    const path = join(__dirname, "../cdk/stack.yaml");
    const stack = await readFile(path, { encoding: "utf8" });

    await cloudformationClient.send(
      new CreateStackCommand({
        StackName: stackName,
        TemplateBody: stack,
        Capabilities: [Capability.CAPABILITY_NAMED_IAM],
      }),
    );
    await retry({ intervalInMs: 2000, maxRetries: 60 }, async () => {
      const { Stacks } = await cloudformationClient.send(
        new DescribeStacksCommand({ StackName: stackName }),
      );
      const stack = Stacks[0];
      if (stack.StackStatus !== "CREATE_COMPLETE") {
        throw new Error("Stack creation incomplete.");
      }
    });
  });

  afterAll(async () => {
    await cloudformationClient.send(
      new DeleteStackCommand({
        StackName: stackName,
      }),
    );

    await retry({ intervalInMs: 2000, maxRetries: 60 }, async () => {
      const { Stacks } = await cloudformationClient.send(
        new DescribeStacksCommand(),
      );

      const targetStack = Stacks.find((s) => s.StackName === stackName);

      if (targetStack) {
        throw new Error("Stack not deleted yet.");
      }
    });
  });

  it("should complete without error", async () => {
    const context = {
      errors: [],
      users: [
        {
          UserName: "test_user_1",
          UserEmail: "test_email_1@example.com",
        },
      ],
    };
    const autoConfirmScenario = AutoConfirm(context);
    await autoConfirmScenario.run({ confirmAll: true });
  });
});
