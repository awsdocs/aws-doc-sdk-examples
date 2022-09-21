// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  DetectDocumentTextCommand,
  AnalyzeDocumentCommand,
  FeatureType,
  StartDocumentTextDetectionCommand,
  StartDocumentAnalysisCommand,
  GetDocumentTextDetectionCommand,
  JobStatus,
  GetDocumentAnalysisCommand,
} from "@aws-sdk/client-textract";
import { GetObjectCommand } from "@aws-sdk/client-s3";
import {
  ReceiveMessageCommand,
  DeleteMessageCommand,
} from "@aws-sdk/client-sqs";

/**
 * Encapsulates the data model used by the application and wraps all calls to AWS
 * in a common place. The React application subscribes to the model so that it is
 * informed whenever the underlying data changes.
 */
export default class TextractModel {
  /**
   * @param s3: An Amazon Simple Storage Service (Amazon S3) client.
   * @param sqs: An Amazon Simple Queue Service (Amazon SQS) client.
   * @param textract: An Amazon Textract client.
   * @param SNSTopicArn: The Amazon Resource Name (ARN) of an Amazon Simple
   *                     Notification Service (Amazon SNS) topic. Amazon Textract
   *                     published a notification to this topic whenever a job
   *                     completes.
   * @param RoleArn: The ARN of an AWS Identity and Access Management (IAM) role that
   *                 can be assumed by Amazon Textract and grants permission to publish
   *                 to the Amazon SNS topic.
   * @param QueueUrl: The URL of an Amazon SQS queue that is subscribed to the
   *                  Amazon SNS topic. This queue is polled for status messages after
   *                  a job is started.
   * @param ConfigError: An error message that indicates the demo application is not
   *                     configured correctly.
   */
  constructor({
    s3,
    sqs,
    textract,
    SNSTopicArn,
    RoleArn,
    QueueUrl,
    ConfigError,
  }) {
    this.s3 = s3;
    this.sqs = sqs;
    this.textract = textract;
    this.snsTopicArn = SNSTopicArn;
    this.roleArn = RoleArn;
    this.queueUrl = QueueUrl;
    this.extraction = null;
    this.imageData = { bucketName: "", objectKey: "" };
    this.onChanges = [];
    this.modelError = ConfigError;
  }

  /**
   * Subscribes the caller to be informed of data changes.
   *
   * @param onChange: A function that is called when data changes.
   */
  subscribe(onChange) {
    this.onChanges.push(onChange);
  }

  /**
   * Called when data changes to inform each subscriber of the change.
   */
  inform() {
    this.onChanges.forEach((sub) => sub());
  }

  /**
   * Reads all data from a ReadableStream and reduces it to a string. This function
   * is used to retrieve data from an Amazon S3 object.
   *
   * @param stream: A ReadableStream.
   * @returns {Promise<string>}: A Promise that contains the returned data.
   */
  async _readStream(stream) {
    let data = "";
    const reader = stream.getReader();
    try {
      while (true) {
        const { done, value } = await reader.read();
        if (done) {
          console.log("Done with stream.");
          return data;
        }

        data += value.reduce(function (a, b) {
          return a + String.fromCharCode(b);
        }, "");
      }
    } finally {
      reader.releaseLock();
    }
  }

  /**
   * Loads an image from Amazon S3 and returns it as a Base64 string.
   *
   * @param bucketName: The name of the bucket that contains the image.
   * @param objectKey: The name of the image.
   * @returns {Promise<{bucketName: string, objectKey: string, base64Data: string}>}:
   *      A Promise that contains the image location and data as a Base64 string.
   */
  async loadImage(bucketName, objectKey) {
    this.modelError = null;
    this.extraction = null;
    console.log(`Loading from ${bucketName}:${objectKey}`);
    try {
      const resp = await this.s3.send(
        new GetObjectCommand({ Bucket: bucketName, Key: objectKey })
      );
      const str_data = await this._readStream(resp.Body);
      this.imageData = {
        bucketName: bucketName,
        objectKey: objectKey,
        base64Data: btoa(str_data).replace(/.{76}(?=.)/g, "$&\n"),
      };
    } catch (error) {
      console.log(error.message);
      this.modelError = error.message;
      if (error.Code === "AccessDenied") {
        this.modelError +=
          ". This may mean the image you entered is not present in " +
          "the specified bucket.";
      }
    } finally {
      this.inform();
    }
    return this.imageData;
  }

  /**
   * Calls synchronous Amazon Textract functions to extract data from an image.
   * Synchronous Amazon Textract functions can be awaited and do not require starting
   * a job or polling an Amazon SQS queue for status.
   *
   * Data returned from Amazon Textract is stored in `this.extraction`.
   *
   * @param extractType: The type of data to extract from the image.
   * @returns {Promise<void>}
   */
  async _extractDocumentSynchronous(extractType) {
    const input = {
      Document: {
        S3Object: {
          Bucket: this.imageData.bucketName,
          Name: this.imageData.objectKey,
        },
      },
    };
    let command;
    if (extractType === "text") {
      command = new DetectDocumentTextCommand(input);
    } else {
      input["FeatureTypes"] =
        extractType === "form" ? [FeatureType.FORMS] : [FeatureType.TABLES];
      command = new AnalyzeDocumentCommand(input);
    }

    const textractResponse = await this.textract.send(command);
    this.extraction = {
      Name: this.imageData.objectKey,
      ExtractType: extractType,
      Children: this._make_page_hierarchy(textractResponse["Blocks"]),
    };
    console.log(textractResponse);
    this.inform();
  }

  /**
   * Calls asynchronous Amazon Textract functions to extract data from an image.
   * Asynchronous Amazon Textract functions return immediately with a job ID. Amazon
   * Textract notifies the specified Amazon SNS topic of job status, and Amazon SNS
   * sends a message to the subscribed Amazon SQS queue. This function polls the queue
   * for status messages until the job completes, and then gets the data for the job
   * from Amazon Textract.
   *
   * Data returned from Amazon Textract is stored in `this.extraction`.
   *
   * @param extractType: The type of data to extract from the image.
   * @returns {Promise<void>}
   */
  async _extractDocumentAsynchronous(extractType) {
    const input = {
      DocumentLocation: {
        S3Object: {
          Bucket: this.imageData.bucketName,
          Name: this.imageData.objectKey,
        },
      },
      NotificationChannel: {
        SNSTopicArn: this.snsTopicArn,
        RoleArn: this.roleArn,
      },
    };
    let command;
    if (extractType === "text") {
      command = new StartDocumentTextDetectionCommand(input);
    } else {
      input["FeatureTypes"] =
        extractType === "form" ? [FeatureType.FORMS] : [FeatureType.TABLES];
      command = new StartDocumentAnalysisCommand(input);
    }

    const { JobId: jobId } = await this.textract.send(command);
    console.log(`JobId: ${jobId}`);

    let waitTime = 0;
    const getJob = async () =>  {
      const { Messages } = await this.sqs.send(
        new ReceiveMessageCommand({
          QueueUrl: this.queueUrl,
          MaxNumberOfMessages: 1,
        })
      );
      if (Messages) {
        console.log(`Message[0]: ${Messages[0].Body}`);
        await this.sqs.send(
          new DeleteMessageCommand({
            QueueUrl: this.queueUrl,
            ReceiptHandle: Messages[0].ReceiptHandle,
          })
        );
        if (
          JSON.parse(JSON.parse(Messages[0].Body).Message).Status ===
          JobStatus.SUCCEEDED
        ) {
          let getCommand;
          if (extractType === "text") {
            getCommand = new GetDocumentTextDetectionCommand({ JobId: jobId });
          } else {
            getCommand = new GetDocumentAnalysisCommand({ JobId: jobId });
          }
          const { Blocks } = await this.textract.send(getCommand);
          this.extraction = {
            Name: this.imageData.objectKey,
            ExtractType: extractType,
            Children: this._make_page_hierarchy(Blocks),
          };
          this.inform();
        }
      } else {
        const tick = 5000;
        waitTime += tick;
        console.log(`Waited ${waitTime / 1000} seconds. No messages yet.`);
        setTimeout(getJob, tick);
      }
    }
    await getJob(jobId);
  }

  /**
   * Calls Amazon Textract to extract data from an image.
   *
   * @param syncType: Indicates the type of API to call to perform the extraction.
   * @param extractType: Indicates the type of data to extract.
   * @returns {Promise<void>}
   */
  async extractDocument(syncType, extractType) {
    this.modelError = null;
    this.extraction = null;

    try {
      if (syncType === "sync") {
        await this._extractDocumentSynchronous(extractType);
      } else {
        await this._extractDocumentAsynchronous(extractType);
      }
    } catch (error) {
      console.log(error.message);
      this.modelError = error.message;
    } finally {
      this.inform();
    }
  }

  /**
   * Adds hierarchical children to a structure based on the list of child IDs
   * in the Amazon Textract data.
   *
   * @param block: The parent block.
   * @param block_dict: A dictionary of blocks returned by Amazon Textract, for
   *                    fast lookup by ID.
   */
  _add_children(block, block_dict) {
    const rels_list = block.Relationships || [];
    rels_list.forEach((rels) => {
      if (rels.Type === "CHILD") {
        block["Children"] = [];
        rels.Ids.forEach((relId) => {
          const kid = block_dict[relId];
          block["Children"].push(kid);
          this._add_children(kid, block_dict);
        });
      }
    });
  }

  /**
   * Builds a hierarchy of blocks out of data returned by Amazon Textract. This
   * hierarchy is used by the React application to display a tree of checkboxes.
   *
   * @param blocks: The flat list of blocks returned by Amazon Textract.
   * @returns {[]}: A hierarchical tree of blocks.
   */
  _make_page_hierarchy(blocks) {
    const block_dict = {};
    blocks.forEach((block) => (block_dict[block.Id] = block));

    const pages = [];
    blocks.forEach((block) => {
      if (block.BlockType === "PAGE") {
        pages.push(block);
        this._add_children(block, block_dict);
      }
    });
    return pages;
  }
}
