// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";

import {
  AttachRolePolicyCommand,
  CreatePolicyCommand,
  CreateRoleCommand,
  DeletePolicyCommand,
  DeleteRoleCommand,
  DetachRolePolicyCommand,
  GetRoleCommand,
  ListPoliciesCommand,
} from "@aws-sdk/client-iam";

import {
  CreateEventSourceMappingCommand,
  CreateFunctionCommand,
  DeleteEventSourceMappingCommand,
  DeleteFunctionCommand,
  DeleteLayerVersionCommand,
  GetFunctionCommand,
  PublishLayerVersionCommand,
} from "@aws-sdk/client-lambda";
import {
  CreateBucketCommand,
  DeleteBucketCommand,
  DeleteObjectCommand,
  GetObjectCommand,
  ListObjectsV2Command,
  PutObjectCommand,
} from "@aws-sdk/client-s3";
import {
  CreatePipelineCommand,
  DeletePipelineCommand,
  DescribePipelineCommand,
  DescribePipelineExecutionCommand,
  PipelineExecutionStatus,
  StartPipelineExecutionCommand,
} from "@aws-sdk/client-sagemaker";
import {
  CreateQueueCommand,
  DeleteQueueCommand,
  GetQueueAttributesCommand,
} from "@aws-sdk/client-sqs";

import {
  createLambdaExecutionRole,
  attachPolicy,
  createLambdaExecutionPolicy,
  createLambdaLayer,
  createLambdaFunction,
  uploadCSVDataToS3,
  createSagemakerRole,
  createSagemakerExecutionPolicy,
  createSagemakerPipeline,
  createSQSQueue,
  configureLambdaSQSEventSource,
  createS3Bucket,
  startPipelineExecution,
  waitForPipelineComplete,
  getObject,
} from "./lib.js";

describe("lib.js", () => {
  describe("createLambdaExecutionRole", () => {
    it("should create a new IAM role for the Lambda function", async () => {
      const mockIAMClient = {
        send: vi.fn((command) => {
          if (command instanceof CreateRoleCommand) {
            return Promise.resolve({
              Role: { Arn: "arn:aws:iam::123456789012:role/test-role" },
            });
          }
          if (command instanceof GetRoleCommand) {
            return Promise.resolve({
              Role: { Arn: "arn:aws:iam::123456789012:role/test-role" },
            });
          }
        }),
      };

      const { arn } = await createLambdaExecutionRole({
        name: "test-role",
        iamClient: mockIAMClient,
      });

      expect(arn).toBe("arn:aws:iam::123456789012:role/test-role");
    });
  });

  describe("attachPolicy", () => {
    it("should attach an IAM policy to an IAM role", async () => {
      const mockIAMClient = {
        send: vi.fn((command) => {
          if (command instanceof AttachRolePolicyCommand) {
            return Promise.resolve({});
          }
          if (command instanceof DetachRolePolicyCommand) {
            return Promise.resolve({});
          }
        }),
      };

      const { cleanUp } = await attachPolicy({
        roleName: "test-role",
        policyArn: "arn:aws:iam::123456789012:policy/test-policy",
        iamClient: mockIAMClient,
      });

      expect(mockIAMClient.send).toHaveBeenCalledWith(
        expect.any(AttachRolePolicyCommand),
      );

      await cleanUp();
      expect(mockIAMClient.send).toHaveBeenCalledWith(
        expect.any(DetachRolePolicyCommand),
      );
    });
  });

  describe("createLambdaExecutionPolicy", () => {
    it("should create a new IAM policy for the Lambda function", async () => {
      const mockIAMClient = {
        send: vi.fn((command) => {
          if (command instanceof CreatePolicyCommand) {
            return Promise.resolve({
              Policy: {
                Arn: "arn:aws:iam::123456789012:policy/test-policy",
              },
            });
          }
          if (command instanceof ListPoliciesCommand) {
            return Promise.resolve({
              Policies: [
                {
                  PolicyName: "test-policy",
                  Arn: "arn:aws:iam::123456789012:policy/test-policy",
                },
              ],
            });
          }
          if (command instanceof DeletePolicyCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { arn, policyConfig, cleanUp } = await createLambdaExecutionPolicy({
        name: "test-policy",
        iamClient: mockIAMClient,
        pipelineExecutionRoleArn: "arn:aws:iam::123456789012:role/test-role",
      });

      expect(arn).toBe("arn:aws:iam::123456789012:policy/test-policy");
      expect(policyConfig).toEqual({
        Version: "2012-10-17",
        Statement: expect.any(Array),
      });

      await cleanUp();
      expect(mockIAMClient.send).toHaveBeenCalledWith(
        expect.any(DeletePolicyCommand),
      );
    });
  });

  describe("createLambdaLayer", () => {
    it("should create a new Lambda layer with the required packages", async () => {
      const mockLambdaClient = {
        send: vi.fn((command) => {
          if (command instanceof PublishLayerVersionCommand) {
            return Promise.resolve({
              LayerVersionArn:
                "arn:aws:lambda:us-west-2:123456789012:layer:sagemaker-wkflw-lambda-layer:1",
              Version: 1,
            });
          }
          if (command instanceof DeleteLayerVersionCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { versionArn, version, cleanUp } = await createLambdaLayer({
        name: "sagemaker-wkflw-lambda-layer",
        lambdaClient: mockLambdaClient,
      });

      expect(versionArn).toBe(
        "arn:aws:lambda:us-west-2:123456789012:layer:sagemaker-wkflw-lambda-layer:1",
      );
      expect(version).toBe(1);

      await cleanUp();
      expect(mockLambdaClient.send).toHaveBeenCalledWith(
        expect.any(DeleteLayerVersionCommand),
      );
    });
  });

  describe("createLambdaFunction", () => {
    it("should create a new Lambda function with the specified configuration", async () => {
      const mockLambdaClient = {
        send: vi.fn((command) => {
          if (command instanceof CreateFunctionCommand) {
            return Promise.resolve({
              FunctionArn:
                "arn:aws:lambda:us-west-2:123456789012:function:sagemaker-wkflw-lambda-function",
            });
          }
          if (command instanceof GetFunctionCommand) {
            return Promise.resolve({
              Configuration: {
                FunctionArn:
                  "arn:aws:lambda:us-west-2:123456789012:function:sagemaker-wkflw-lambda-function",
              },
            });
          }
          if (command instanceof DeleteFunctionCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { arn, cleanUp } = await createLambdaFunction({
        name: "sagemaker-wkflw-lambda-function",
        roleArn:
          "arn:aws:iam::123456789012:role/sagemaker-wkflw-lambda-execution-role",
        lambdaClient: mockLambdaClient,
        layerVersionArn:
          "arn:aws:lambda:us-west-2:123456789012:layer:sagemaker-wkflw-lambda-layer:1",
      });

      expect(arn).toBe(
        "arn:aws:lambda:us-west-2:123456789012:function:sagemaker-wkflw-lambda-function",
      );

      await cleanUp();
      expect(mockLambdaClient.send).toHaveBeenCalledWith(
        expect.any(DeleteFunctionCommand),
      );
    });
  });

  describe("uploadCSVDataToS3", () => {
    it("should upload the sample CSV data to the specified S3 bucket", async () => {
      const mockS3Client = {
        send: vi.fn((command) => {
          if (command instanceof PutObjectCommand) {
            return Promise.resolve();
          }
        }),
      };

      await uploadCSVDataToS3({
        bucketName: "test-bucket",
        s3Client: mockS3Client,
      });

      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(PutObjectCommand),
      );
    });
  });

  describe("createSagemakerRole", () => {
    it("should create a new IAM role for the SageMaker pipeline", async () => {
      const mockIAMClient = {
        send: vi.fn((command) => {
          if (command instanceof CreateRoleCommand) {
            return Promise.resolve({
              Role: { Arn: "arn:aws:iam::123456789012:role/test-role" },
            });
          }
          if (command instanceof GetRoleCommand) {
            return Promise.resolve({
              Role: { Arn: "arn:aws:iam::123456789012:role/test-role" },
            });
          }
          if (command instanceof DeleteRoleCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { arn, cleanUp } = await createSagemakerRole({
        name: "test-role",
        iamClient: mockIAMClient,
        wait: () => Promise.resolve(),
      });

      expect(arn).toBe("arn:aws:iam::123456789012:role/test-role");

      await cleanUp();
      expect(mockIAMClient.send).toHaveBeenCalledWith(
        expect.any(DeleteRoleCommand),
      );
    });
  });

  describe("createSagemakerExecutionPolicy", () => {
    it("should create a new IAM policy for the SageMaker pipeline execution", async () => {
      const mockIAMClient = {
        send: vi.fn((command) => {
          if (command instanceof CreatePolicyCommand) {
            return Promise.resolve({
              Policy: {
                Arn: "arn:aws:iam::123456789012:policy/test-sagemaker-execution-policy",
              },
            });
          }
          if (command instanceof ListPoliciesCommand) {
            return Promise.resolve({
              Policies: [
                {
                  PolicyName: "test-sagemaker-execution-policy",
                  Arn: "arn:aws:iam::123456789012:policy/test-sagemaker-execution-policy",
                },
              ],
            });
          }
          if (command instanceof DeletePolicyCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { arn, policyConfig, cleanUp } =
        await createSagemakerExecutionPolicy({
          name: "test-sagemaker-execution-policy",
          sqsQueueArn: "arn:aws:sqs:us-west-2:123456789012:test-queue",
          lambdaArn:
            "arn:aws:lambda:us-west-2:123456789012:function:test-lambda-function",
          iamClient: mockIAMClient,
          s3BucketName: "test-bucket",
        });

      expect(arn).toBe(
        "arn:aws:iam::123456789012:policy/test-sagemaker-execution-policy",
      );
      expect(policyConfig).toEqual({
        Version: "2012-10-17",
        Statement: [
          {
            Effect: "Allow",
            Action: ["lambda:InvokeFunction"],
            Resource:
              "arn:aws:lambda:us-west-2:123456789012:function:test-lambda-function",
          },
          {
            Effect: "Allow",
            Action: ["s3:*"],
            Resource: [
              "arn:aws:s3:::test-bucket",
              "arn:aws:s3:::test-bucket/*",
            ],
          },
          {
            Effect: "Allow",
            Action: ["sqs:SendMessage"],
            Resource: "arn:aws:sqs:us-west-2:123456789012:test-queue",
          },
        ],
      });

      await cleanUp();
      expect(mockIAMClient.send).toHaveBeenCalledWith(
        expect.any(DeletePolicyCommand),
      );
    });
  });

  describe("createSagemakerPipeline", () => {
    it("should create a new SageMaker pipeline with the specified configuration", async () => {
      const mockSageMakerClient = {
        send: vi.fn((command) => {
          if (command instanceof CreatePipelineCommand) {
            return Promise.resolve({
              PipelineArn:
                "arn:aws:sagemaker:us-west-2:123456789012:pipeline/sagemaker-wkflw-pipeline",
            });
          }
          if (command instanceof DescribePipelineCommand) {
            return Promise.resolve({
              PipelineArn:
                "arn:aws:sagemaker:us-west-2:123456789012:pipeline/sagemaker-wkflw-pipeline",
            });
          }
          if (command instanceof DeletePipelineCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { arn, cleanUp } = await createSagemakerPipeline({
        roleArn:
          "arn:aws:iam::123456789012:role/sagemaker-wkflw-pipeline-execution-role",
        name: "sagemaker-wkflw-pipeline",
        functionArn:
          "arn:aws:lambda:us-west-2:123456789012:function:sagemaker-wkflw-lambda-function",
        sagemakerClient: mockSageMakerClient,
      });

      expect(arn).toBe(
        "arn:aws:sagemaker:us-west-2:123456789012:pipeline/sagemaker-wkflw-pipeline",
      );

      await cleanUp();
      expect(mockSageMakerClient.send).toHaveBeenCalledWith(
        expect.any(DeletePipelineCommand),
      );
    });
  });

  describe("createSQSQueue", () => {
    it("should create a new SQS queue with the specified configuration", async () => {
      const mockSQSClient = {
        send: vi.fn((command) => {
          if (command instanceof CreateQueueCommand) {
            return Promise.resolve({
              QueueUrl:
                "https://sqs.us-west-2.amazonaws.com/123456789012/test-queue",
            });
          }
          if (command instanceof GetQueueAttributesCommand) {
            return Promise.resolve({
              Attributes: {
                QueueArn: "arn:aws:sqs:us-west-2:123456789012:test-queue",
              },
            });
          }
          if (command instanceof DeleteQueueCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { queueUrl, queueArn, cleanUp } = await createSQSQueue({
        name: "test-queue",
        sqsClient: mockSQSClient,
      });

      expect(queueUrl).toBe(
        "https://sqs.us-west-2.amazonaws.com/123456789012/test-queue",
      );
      expect(queueArn).toBe("arn:aws:sqs:us-west-2:123456789012:test-queue");

      await cleanUp();
      expect(mockSQSClient.send).toHaveBeenCalledWith(
        expect.any(DeleteQueueCommand),
      );
    });
  });

  describe("configureLambdaSQSEventSource", () => {
    it("should configure the Lambda function to receive events from the SQS queue", async () => {
      const functionArn =
        "arn:aws:lambda:us-west-2:123456789012:function:test-lambda-function";
      const mockLambdaClient = {
        send: vi.fn((command) => {
          if (command instanceof CreateEventSourceMappingCommand) {
            return Promise.resolve({ UUID: "test-uuid" });
          }
          if (command instanceof GetFunctionCommand) {
            return Promise.resolve({
              Configuration: {
                FunctionArn: functionArn,
              },
            });
          }
          if (command instanceof DeleteEventSourceMappingCommand) {
            return Promise.resolve();
          }
        }),
      };

      const queueArn = "arn:aws:sqs:us-west-2:123456789012:test-queue";
      const { cleanUp } = await configureLambdaSQSEventSource({
        lambdaName: "test-lambda-function",
        queueArn,
        lambdaClient: mockLambdaClient,
        paginateListEventSourceMappings: async function* () {
          yield {
            EventSourceArn: queueArn,
            FunctionArn: functionArn,
            UUID: "",
          };
        },
      });

      expect(mockLambdaClient.send).toHaveBeenCalledWith(
        expect.any(CreateEventSourceMappingCommand),
      );

      await cleanUp();
      expect(mockLambdaClient.send).toHaveBeenCalledWith(
        expect.any(DeleteEventSourceMappingCommand),
      );
    });
  });

  describe("createS3Bucket", () => {
    it("should create a new S3 bucket with the specified name", async () => {
      const mockS3Client = {
        send: vi.fn((command) => {
          if (command instanceof CreateBucketCommand) {
            return Promise.resolve();
          }
          if (command instanceof DeleteObjectCommand) {
            return Promise.resolve();
          }
          if (command instanceof DeleteBucketCommand) {
            return Promise.resolve();
          }
        }),
      };

      const { cleanUp } = await createS3Bucket({
        name: "test-bucket",
        s3Client: mockS3Client,
        paginateListObjectsV2: async function* () {
          yield {
            Contents: [
              { Key: "test-object-1.txt" },
              { Key: "test-object-2.txt" },
            ],
          };
        },
      });

      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(CreateBucketCommand),
      );

      await cleanUp();
      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(DeleteObjectCommand),
      );
      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(DeleteBucketCommand),
      );
    });
  });

  describe("startPipelineExecution", () => {
    it("should start the execution of the SageMaker pipeline with the specified parameters", async () => {
      const mockSageMakerClient = {
        send: vi.fn((command) => {
          if (command instanceof StartPipelineExecutionCommand) {
            return Promise.resolve({
              PipelineExecutionArn:
                "arn:aws:sagemaker:us-west-2:123456789012:pipeline-execution/sagemaker-wkflw-pipeline/example-execution",
            });
          }
        }),
      };

      const { arn } = await startPipelineExecution({
        sagemakerClient: mockSageMakerClient,
        name: "sagemaker-wkflw-pipeline",
        bucketName: "test-bucket",
        roleArn: "arn:aws:iam::123456789012:role/test-role",
        queueUrl: "https://sqs.us-west-2.amazonaws.com/123456789012/test-queue",
      });

      expect(arn).toBe(
        "arn:aws:sagemaker:us-west-2:123456789012:pipeline-execution/sagemaker-wkflw-pipeline/example-execution",
      );
      expect(mockSageMakerClient.send).toHaveBeenCalledWith(
        expect.any(StartPipelineExecutionCommand),
      );
    });
  });

  describe("waitForPipelineComplete", () => {
    it("should wait for the SageMaker pipeline execution to complete", async () => {
      const mockSageMakerClient = {
        send: vi
          .fn()
          .mockImplementationOnce(() =>
            Promise.resolve({
              PipelineExecutionStatus: PipelineExecutionStatus.EXECUTING,
              FailureReason: null,
            }),
          )
          .mockImplementationOnce(() =>
            Promise.resolve({
              PipelineExecutionStatus: PipelineExecutionStatus.STOPPING,
              FailureReason: null,
            }),
          )
          .mockImplementationOnce(() =>
            Promise.resolve({
              PipelineExecutionStatus: PipelineExecutionStatus.SUCCEEDED,
              FailureReason: null,
            }),
          ),
      };

      const wait = vi.fn(() => Promise.resolve());

      await waitForPipelineComplete({
        arn: "arn:aws:sagemaker:us-west-2:123456789012:pipeline-execution/sagemaker-wkflw-pipeline/example-execution",
        sagemakerClient: mockSageMakerClient,
        wait,
      });

      expect(mockSageMakerClient.send).toHaveBeenCalledTimes(3);
      expect(mockSageMakerClient.send).toHaveBeenCalledWith(
        expect.any(DescribePipelineExecutionCommand),
      );
      expect(wait).toHaveBeenCalledTimes(2);
    });
  });

  describe("getObject", () => {
    it("should retrieve the string value of an S3 object", async () => {
      const mockS3Client = {
        send: vi.fn((command) => {
          if (command instanceof ListObjectsV2Command) {
            return {
              Contents: [
                {
                  Key: "output/output.csv",
                },
              ],
            };
          }
          if (command instanceof GetObjectCommand) {
            return {
              Body: {
                transformToString: () => "Sample output data",
              },
            };
          }
        }),
      };

      const output = await getObject({
        bucket: "test-bucket",
        s3Client: mockS3Client,
      });

      expect(output).toBe("Sample output data");
      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(ListObjectsV2Command),
      );
      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(GetObjectCommand),
      );
    });

    it("should throw an error if no objects are found in the bucket", async () => {
      const mockS3Client = {
        send: vi.fn((command) => {
          if (command instanceof ListObjectsV2Command) {
            return {
              Contents: [],
            };
          }
        }),
      };

      await expect(
        getObject({
          bucket: "test-bucket",
          s3Client: mockS3Client,
        }),
      ).rejects.toThrow("No objects found in bucket.");
      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(ListObjectsV2Command),
      );
    });

    it("should throw an error if no CSV file is found in the bucket", async () => {
      const mockS3Client = {
        send: vi.fn((command) => {
          if (command instanceof ListObjectsV2Command) {
            return {
              Contents: [
                {
                  Key: "output/other-file.txt",
                },
              ],
            };
          }
        }),
      };

      await expect(
        getObject({
          bucket: "test-bucket",
          s3Client: mockS3Client,
        }),
      ).rejects.toThrow(
        'No CSV file found in bucket with the prefix "output/".',
      );
      expect(mockS3Client.send).toHaveBeenCalledWith(
        expect.any(ListObjectsV2Command),
      );
    });
  });
});
