// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import fs from "node:fs/promises";
import path from "node:path";

import {
  CloudFormationClient,
  CreateStackCommand,
  DescribeStacksCommand,
} from "@aws-sdk/client-cloudformation";
import { STSClient, GetCallerIdentityCommand } from "@aws-sdk/client-sts";

import {
  Scenario,
  ScenarioAction,
  ScenarioInput,
  ScenarioOutput,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";
import { retry } from "@aws-doc-sdk-examples/lib/utils/util-timers.js";

const cfnClient = new CloudFormationClient({});
const stsClient = new STSClient({});

const __dirname = path.dirname(new URL(import.meta.url).pathname);
const cfnTemplatePath = path.join(
  __dirname,
  "../../../../../workflows/healthimaging_image_sets/resources/cfn_template.yaml"
);

const deployStack = new ScenarioInput(
  "deployStack",
  "Do you want to deploy the CloudFormation stack?",
  { type: "confirm" }
);

const getStackName = new ScenarioInput(
  "getStackName",
  "Enter a name for the CloudFormation stack:",
  { type: "input" }
);

const getDatastoreName = new ScenarioInput(
  "getDatastoreName",
  "Enter a name for the HealthImaging datastore:",
  { type: "input" }
);

const skipDeployment = new ScenarioOutput(
  "skipDeployment",
  "Skipping stack deployment."
);

const getAccountId = new ScenarioAction("getAccountId", async (state) => {
  const command = new GetCallerIdentityCommand({});
  const response = await stsClient.send(command);
  state.accountId = response.Account;
});

const createStack = new ScenarioAction("createStack", async (state) => {
  const stackName = state.getStackName;
  const datastoreName = state.getDatastoreName;
  const accountId = state.accountId;

  const command = new CreateStackCommand({
    StackName: stackName,
    TemplateBody: await fs.readFile(cfnTemplatePath, "utf8"),
    Capabilities: ["CAPABILITY_IAM"],
    Parameters: [
      {
        ParameterKey: "datastoreName",
        ParameterValue: datastoreName,
      },
      {
        ParameterKey: "userAccountID",
        ParameterValue: accountId,
      },
    ],
  });

  const response = await cfnClient.send(command);
  state.stackId = response.StackId;
});

const waitForStackCreation = new ScenarioAction(
  "waitForStackCreation",
  async (state) => {
    const command = new DescribeStacksCommand({
      StackName: state.stackId,
    });

    await retry({ intervalInMs: 10000, maxRetries: 60 }, async () => {
      const response = await cfnClient.send(command);
      const stack = response.Stacks?.find(
        (s) => s.StackName == state.getStackName
      );
      if (!stack || stack.StackStatus === "CREATE_IN_PROGRESS") {
        throw new Error("Stack creation is still in progress");
      }
      if (stack.StackStatus === "CREATE_COMPLETE") {
        state.stackOutputs = stack.Outputs?.reduce((acc, output) => {
          acc[output.OutputKey] = output.OutputValue;
          return acc;
        }, {});
      } else {
        throw new Error(
          `Stack creation failed with status: ${stack.StackStatus}`
        );
      }
    });
  }
);

const outputState = new ScenarioOutput("outputState", (state) => {
  /**
   * @type {{ stackOutputs: { DatastoreID: string, BucketName: string, RoleArn: string }}}
   */
  const { stackOutputs } = state;
  return `Stack creation completed. Output values:
Datastore ID: ${stackOutputs?.DatastoreID}
Bucket Name: ${stackOutputs?.BucketName}
Role ARN: ${stackOutputs?.RoleArn}
    `;
});

const saveState = new ScenarioAction("saveState", async (state) => {
  await fs.writeFile("step-1-state.json", JSON.stringify(state));
});

export const step1 = new Scenario(
  "Step 1: Deploy CloudFormation Stack",
  [
    deployStack,
    new ScenarioAction("skipDeployment", async (state, options) => {
      if (!state.deployStack) {
        await skipDeployment.handle(state, options);
        return;
      }

      await getStackName.handle(state, options);
      await getDatastoreName.handle(state, options);
      await getAccountId.handle(state, options);
      await createStack.handle(state, options);
      await waitForStackCreation.handle(state, options);
      await outputState.handle(state, options);
      await saveState.handle(state, options);
    }),
  ],
  {}
);
