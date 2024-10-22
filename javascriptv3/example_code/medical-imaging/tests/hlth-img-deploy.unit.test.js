// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { expect, it, vi, describe, beforeEach } from "vitest";

const writeFileMock = vi.fn();
const readFileMock = vi.fn();
const fsMod = {
  writeFile: writeFileMock,
  readFile: readFileMock,
};
vi.doMock("node:fs/promises", () => ({
  default: fsMod,
  ...fsMod,
}));

const stateFilePath = "state.json";
const inputHandler = vi.fn();
vi.doMock("@aws-doc-sdk-examples/lib/scenario/index.js", async () => {
  const actual = await vi.importActual(
    "@aws-doc-sdk-examples/lib/scenario/index.js",
  );
  return {
    ...actual,
    ScenarioInput: vi.fn().mockImplementation(() => ({
      skipWhen(fn) {
        this.skip = fn;
        return this;
      },
      handle: inputHandler,
    })),
  };
});

const { Scenario } = await import(
  "@aws-doc-sdk-examples/lib/scenario/index.js"
);

const { saveState } = await import(
  "@aws-doc-sdk-examples/lib/scenario/steps-common.js"
);

const cloudFormationSend = vi.fn();
const createStackCommand = vi.fn((obj) => obj);
const cloudformationConstructor = vi
  .fn()
  .mockReturnValue({ send: cloudFormationSend });
vi.doMock("@aws-sdk/client-cloudformation", async () => {
  const actual = await vi.importActual("@aws-sdk/client-cloudformation");
  return {
    ...actual,
    CreateStackCommand: createStackCommand,
    CloudFormationClient: cloudformationConstructor,
  };
});

const stsGetCallerIdentitySend = vi.fn();
const stsConstructor = vi
  .fn()
  .mockReturnValue({ send: stsGetCallerIdentitySend });
vi.doMock("@aws-sdk/client-sts", async () => {
  const actual = await vi.importActual("@aws-sdk/client-sts");
  return {
    ...actual,
    STSClient: stsConstructor,
  };
});

const retryMock = vi
  .fn()
  .mockImplementation(
    async (/** @type {{ maxRetries: number }} */ config, fn) => {
      const maxRetries = config.maxRetries ?? 10;
      let retries = 0;
      let error;

      while (retries < maxRetries) {
        try {
          return await fn();
        } catch (err) {
          error = err;
          retries++;
          await new Promise((resolve) => setTimeout(resolve, 10)); // Wait for 10ms before retrying
        }
      }

      throw error;
    },
  );

vi.doMock("@aws-doc-sdk-examples/lib/utils/util-timers.js", () => ({
  retry: retryMock,
}));

const {
  deployStack,
  getStackName,
  getDatastoreName,
  getAccountId,
  createStack,
  waitForStackCreation,
  outputState,
} = await import("../scenarios/health-image-sets/deploy-steps.js");

describe("deploy-steps", () => {
  const deploySteps = new Scenario("deploy-steps", [
    deployStack,
    getStackName,
    getDatastoreName,
    getAccountId,
    createStack,
    waitForStackCreation,
    outputState,
    saveState,
  ]);
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should skip deployment when the user chooses not to deploy", async () => {
    inputHandler.mockImplementationOnce((/** @type {object} */ state) => {
      state.deployStack = false;
    });

    await deploySteps.run({ confirmAll: true, verbose: false });

    expect(cloudFormationSend).toHaveBeenCalledTimes(0);
  });

  it("should deploy a stack with appropriate values when the user chooses to deploy", async () => {
    const accountId = "111122223333";
    const stackName = "test-stack";
    const datastoreName = "test-datastore";

    inputHandler
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.deployStack = true;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.getStackName = stackName;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.getDatastoreName = datastoreName;
      });
    stsGetCallerIdentitySend.mockResolvedValueOnce({ Account: accountId });
    cloudFormationSend
      .mockResolvedValueOnce({
        StackId: `arn:aws:cloudformation:us-east-1:${accountId}:stack/med-store/ad906b30-f36a-11ee-b7f5-0affc25fc6d9`,
      })
      .mockResolvedValueOnce({
        Stacks: [{ StackName: stackName, StackStatus: "CREATE_COMPLETE" }],
      });

    readFileMock.mockResolvedValueOnce("");

    await deploySteps.run({ confirmAll: true, verbose: false });

    expect(cloudFormationSend).toHaveBeenCalledWith({
      StackName: stackName,
      TemplateBody: expect.any(String),
      Capabilities: ["CAPABILITY_IAM"],
      Parameters: [
        { ParameterKey: "datastoreName", ParameterValue: datastoreName },
        { ParameterKey: "userAccountID", ParameterValue: accountId },
      ],
    });
  });

  it("should retry DescribeStacksCommand while stack is in CREATE_IN_PROGRESS state", async () => {
    const accountId = "111122223333";
    const stackName = "test-stack";
    const datastoreName = "test-datastore";

    inputHandler
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.deployStack = true;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.getStackName = stackName;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.getDatastoreName = datastoreName;
      });
    stsGetCallerIdentitySend.mockResolvedValueOnce({ Account: accountId });
    cloudFormationSend
      .mockResolvedValueOnce({
        StackId: `arn:aws:cloudformation:us-east-1:${accountId}:stack/med-store/ad906b30-f36a-11ee-b7f5-0affc25fc6d9`,
      })
      .mockResolvedValueOnce({
        Stacks: [{ StackStatus: "CREATE_IN_PROGRESS", StackName: stackName }],
      })
      .mockResolvedValueOnce({
        Stacks: [{ StackStatus: "CREATE_COMPLETE", StackName: stackName }],
      });

    await deploySteps.run({ confirmAll: true, verbose: false });

    expect(cloudFormationSend).toHaveBeenCalledTimes(3);
    expect(retryMock).toHaveBeenCalledTimes(1);
  });

  it("should save the correct output state file", async () => {
    const accountId = "111122223333";
    const stackName = "test-stack";
    const datastoreName = "test-datastore";
    const datastoreId = "datastore-123";
    const bucketName = "bucket-name";
    const roleArn = "arn:aws:iam::111122223333:role/test-role";

    inputHandler
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.deployStack = true;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.getStackName = stackName;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.getDatastoreName = datastoreName;
      });
    stsGetCallerIdentitySend.mockResolvedValueOnce({ Account: accountId });
    cloudFormationSend
      .mockResolvedValueOnce({
        StackId: `arn:aws:cloudformation:us-east-1:${accountId}:stack/med-store/ad906b30-f36a-11ee-b7f5-0affc25fc6d9`,
      })
      .mockResolvedValueOnce({
        Stacks: [
          {
            StackStatus: "CREATE_COMPLETE",
            StackName: stackName,
            Outputs: [
              { OutputKey: "DatastoreID", OutputValue: datastoreId },
              { OutputKey: "BucketName", OutputValue: bucketName },
              { OutputKey: "RoleArn", OutputValue: roleArn },
            ],
          },
        ],
      });

    await deploySteps.run({ confirmAll: true, verbose: false });

    expect(writeFileMock).toHaveBeenCalledWith(
      stateFilePath,
      JSON.stringify({
        name: deploySteps.name,
        earlyExit: false,
        deployStack: true,
        getStackName: stackName,
        getDatastoreName: datastoreName,
        accountId,
        stackId: `arn:aws:cloudformation:us-east-1:${accountId}:stack/med-store/ad906b30-f36a-11ee-b7f5-0affc25fc6d9`,
        stackOutputs: {
          DatastoreID: datastoreId,
          BucketName: bucketName,
          RoleArn: roleArn,
        },
      }),
    );
  });
});
