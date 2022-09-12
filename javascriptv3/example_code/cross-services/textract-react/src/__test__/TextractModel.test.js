// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the TextractModel component.

import { S3Client } from "@aws-sdk/client-s3";
import {
  TextractClient,
  DetectDocumentTextCommand,
  AnalyzeDocumentCommand,
  FeatureType,
  StartDocumentTextDetectionCommand,
  StartDocumentAnalysisCommand,
  GetDocumentTextDetectionCommand,
  JobStatus,
  GetDocumentAnalysisCommand,
} from "@aws-sdk/client-textract";
import {
  SQSClient,
  ReceiveMessageCommand,
  DeleteMessageCommand,
} from "@aws-sdk/client-sqs";
import TextractModel from "../TextractModel";
import { TestExtractDocument, TestExtractResponse } from "./TestData";

beforeEach(() => {
  jest.clearAllMocks();
});

test("subscriber is informed", () => {
  const model = new TextractModel({});
  const sub = jest.fn();
  model.subscribe(sub);
  model.inform();
  expect(sub).toHaveBeenCalled();
});

describe("loadImage", () => {
  test("reads from stream, returns base 64 data", async () => {
    const bucket = "test-bucket";
    const key = "test-key";
    const imgValue = "test-image";
    const s3 = new S3Client({});
    s3.send = jest.fn((command) => {
      expect(command.input).toEqual({ Bucket: bucket, Key: key });
      return {
        Body: {
          getReader: () => ({
            read: jest
              .fn()
              .mockReturnValueOnce({
                done: false,
                value: { reduce: () => imgValue },
              })
              .mockReturnValueOnce({ done: true, value: {} }),
            releaseLock: () => true,
          }),
        },
      };
    });

    const tm = new TextractModel({ s3: s3 });
    tm.inform = jest.fn();

    const data = await tm.loadImage(bucket, key, s3);
    expect(s3.send).toHaveBeenCalled();
    expect(data).toEqual({
      bucketName: bucket,
      objectKey: key,
      base64Data: btoa(imgValue),
    });
    expect(tm.inform).toHaveBeenCalled();
    expect(tm.modelError).toBeNull();
    expect(tm.extraction).toBeNull();
  });

  test("fills modelError when error raised", () => {
    const testError = "test exception";
    const s3 = new S3Client({});
    s3.send = jest.fn(() => {
      throw new Error(testError);
    });

    const tm = new TextractModel({ s3: s3 });
    tm.loadImage("test-bucket", "test-key");
    expect(tm.modelError).toBe(testError);
  });
});

describe("extractDocument", () => {
  test("synchronous text type uses detect command, returns expected document", async () => {
    const syncType = "sync";
    const extractType = "text";
    const imageData = {
      bucketName: "test-bucket",
      objectKey: TestExtractDocument.Name,
    };
    const textract = new TextractClient({});
    textract.send = jest.fn((command) => {
      expect(command).toBeInstanceOf(DetectDocumentTextCommand);
      expect(command.input).toEqual({
        Document: {
          S3Object: { Bucket: imageData.bucketName, Name: imageData.objectKey },
        },
      });
      return TestExtractResponse;
    });
    const tm = new TextractModel({ textract: textract });
    tm.imageData = imageData;
    tm.inform = jest.fn();
    await tm.extractDocument(syncType, extractType);
    expect(textract.send).toHaveBeenCalled();
    expect(tm.extraction).toEqual(TestExtractDocument);
    expect(tm.inform).toHaveBeenCalled();
  });

  test("synchronous table type calls analyze command", async () => {
    const syncType = "sync";
    const extractType = "table";
    const textract = new TextractClient({});
    textract.send = jest.fn((command) => {
      expect(command).toBeInstanceOf(AnalyzeDocumentCommand);
      expect(command.input.FeatureTypes).toEqual([FeatureType.TABLES]);
      return { Blocks: [] };
    });
    const tm = new TextractModel({ textract: textract });
    await tm.extractDocument(syncType, extractType);
    expect(textract.send).toHaveBeenCalled();
  });

  test("modelError contains error message when error thrown", async () => {
    const testError = "test error";
    const textract = new TextractClient({});
    textract.send = jest.fn(() => {
      throw new Error(testError);
    });
    const tm = new TextractModel({ textract: textract });
    await tm.extractDocument("sync", "text");
    expect(textract.send).toHaveBeenCalled();
    expect(tm.modelError).toBe(testError);
  });

  test(
    "asynchronous text type calls detect command, waits for sqs, returns " +
      "expected document",
    async () => {
      const syncType = "async";
      const extractType = "text";
      const imageData = {
        bucketName: "test-bucket",
        objectKey: TestExtractDocument.Name,
      };
      const snsTopicArn = "sns-topic-arn";
      const roleArn = "role-arn";
      const queueUrl = "queue-url";
      const jobId = "job-id";
      const textract = new TextractClient({});
      textract.send = jest
        .fn()
        .mockImplementationOnce((command) => {
          expect(command).toBeInstanceOf(StartDocumentTextDetectionCommand);
          expect(command.input).toEqual({
            DocumentLocation: {
              S3Object: {
                Bucket: imageData.bucketName,
                Name: imageData.objectKey,
              },
            },
            NotificationChannel: {
              SNSTopicArn: snsTopicArn,
              RoleArn: roleArn,
            },
          });
          return { JobId: jobId };
        })
        .mockImplementationOnce((command) => {
          expect(command).toBeInstanceOf(GetDocumentTextDetectionCommand);
          expect(command.input).toEqual({ JobId: jobId });
          return TestExtractResponse;
        });
      const sqs = new SQSClient({});
      sqs.send = jest
        .fn()
        .mockImplementationOnce((command) => {
          expect(command).toBeInstanceOf(ReceiveMessageCommand);
          expect(command.input).toEqual({
            QueueUrl: queueUrl,
            MaxNumberOfMessages: 1,
          });
          return {
            Messages: [
              {
                ReceiptHandle: "receipt-handle",
                Body: JSON.stringify({
                  Message: JSON.stringify({ Status: JobStatus.SUCCEEDED }),
                }),
              },
            ],
          };
        })
        .mockImplementationOnce((command) => {
          expect(command).toBeInstanceOf(DeleteMessageCommand);
        });
      const tm = new TextractModel({
        textract: textract,
        sqs: sqs,
        snsTopicArn: snsTopicArn,
        roleArn: roleArn,
        queueUrl: queueUrl,
      });
      tm.imageData = imageData;
      tm.inform = jest.fn();
      await tm.extractDocument(syncType, extractType);
      expect(textract.send).toHaveBeenCalled();
      expect(sqs.send).toHaveBeenCalledTimes(2);
      expect(tm.extraction).toEqual(TestExtractDocument);
      expect(tm.inform).toHaveBeenCalled();
    }
  );

  test("asynchronous form type calls analyze command", async () => {
    const syncType = "async";
    const extractType = "form";
    const jobId = "job-id";
    const textract = new TextractClient({});
    textract.send = jest
      .fn()
      .mockImplementationOnce((command) => {
        expect(command).toBeInstanceOf(StartDocumentAnalysisCommand);
        expect(command.input.FeatureTypes).toEqual([FeatureType.FORMS]);
        return { JobId: jobId };
      })
      .mockImplementationOnce((command) => {
        expect(command).toBeInstanceOf(GetDocumentAnalysisCommand);
        expect(command.input).toEqual({ JobId: jobId });
        return { Blocks: [] };
      });
    const sqs = new SQSClient({});
    sqs.send = jest
      .fn()
      .mockImplementationOnce((command) => {
        expect(command).toBeInstanceOf(ReceiveMessageCommand);
        return {
          Messages: [
            {
              ReceiptHandle: "receipt-handle",
              Body: JSON.stringify({
                Message: JSON.stringify({ Status: JobStatus.SUCCEEDED }),
              }),
            },
          ],
        };
      })
      .mockImplementationOnce((command) => {
        expect(command).toBeInstanceOf(DeleteMessageCommand);
      });
    const tm = new TextractModel({ textract: textract, sqs: sqs });
    await tm.extractDocument(syncType, extractType);
    expect(textract.send).toHaveBeenCalled();
    expect(sqs.send).toHaveBeenCalledTimes(2);
  });

  test("setTimeout called when no messages yet", async () => {
    const syncType = "async";
    const extractType = "text";
    const jobId = "job-id";
    jest.useFakeTimers();
    const textract = new TextractClient({});
    textract.send = jest
      .fn()
      .mockImplementationOnce(() => {
        return { JobId: jobId };
      })
      .mockImplementationOnce(() => {
        return { Blocks: [] };
      });
    const sqs = new SQSClient({});
    sqs.send = jest
      .fn()
      .mockImplementationOnce(() => {
        return {};
      })
      .mockImplementationOnce(() => {
        return {
          Messages: [
            {
              ReceiptHandle: "receipt-handle",
              Body: JSON.stringify({
                Message: JSON.stringify({ Status: JobStatus.SUCCEEDED }),
              }),
            },
          ],
        };
      })
      .mockImplementationOnce((command) => {
        expect(command).toBeInstanceOf(DeleteMessageCommand);
      });
    const tm = new TextractModel({ textract: textract, sqs: sqs });
    await tm.extractDocument(syncType, extractType);
    jest.runAllTimers();
    await Promise.resolve();
    expect(textract.send).toHaveBeenCalled();
    expect(sqs.send).toHaveBeenCalledTimes(3);
    expect(setTimeout).toHaveBeenCalledTimes(1);
  });
});
