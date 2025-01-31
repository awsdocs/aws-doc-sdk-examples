// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { dirname, join } from "node:path";
import { spawn } from "node:child_process";

import { describe, it, beforeAll, afterAll } from "vitest";
import {
  CloudFormationClient,
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
    const cdkDeploy = spawn("cdk", ["deploy", "--require-approval", "never"], {
      cwd: `${__dirname}/../cdk`,
    });

    cdkDeploy.stderr.on("data", (d) => {
      console.error(d);
    });

    await new Promise((resolve, reject) => {
      cdkDeploy.on("exit", (code) => {
        if (code === 0) {
          resolve();
        } else {
          reject();
        }
      });
    });

    await retry(
      { intervalInMs: 2000, maxRetries: 100, backoff: 5000 },
      async () => {
        const { Stacks } = await cloudformationClient.send(
          new DescribeStacksCommand({ StackName: stackName }),
        );
        const stack = Stacks[0];
        if (stack.StackStatus !== "CREATE_COMPLETE") {
          throw new Error(
            `Stack creation incomplete. STATUS:  ${stack.StackStatus}`,
          );
        }
      },
    );
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
