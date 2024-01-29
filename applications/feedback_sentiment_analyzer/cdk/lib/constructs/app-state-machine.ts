// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { Construct } from "constructs";
import { AppFunction } from "./app-lambdas";
import { DefinitionBody, StateMachine } from "aws-cdk-lib/aws-stepfunctions";
import { AppDatabase } from "./app-database";
import { Effect, Policy, PolicyStatement } from "aws-cdk-lib/aws-iam";

export class AppStateMachine extends Construct {
  readonly stateMachine: StateMachine;

  constructor(
    scope: Construct,
    id: string,
    readonly functions: Record<string, AppFunction>,
    database: AppDatabase
  ) {
    super(scope, id);
    const { ExtractText, AnalyzeSentiment, TranslateText, SynthesizeAudio } =
      functions;
    if (
      ![ExtractText, AnalyzeSentiment, TranslateText, SynthesizeAudio].every(
        Boolean
      )
    ) {
      throw new Error(`
      Function mismatch. 
      You must provide all of the following functions:
      ExtractText: ${ExtractText},
      AnalyzeSentiment: ${AnalyzeSentiment},
      TranslateText: ${TranslateText},
      SynthesizeAudio: ${SynthesizeAudio},
      `);
    }

    this.stateMachine = new StateMachine(this, "state-machine", {
      definitionBody: DefinitionBody.fromString(
        JSON.stringify({
          Comment:
            "Extract, Analyze, Translate, Synthesize, and Post a Comment",
          StartAt: "ExtractText",
          States: {
            ExtractText: {
              Type: "Task",
              Resource: "arn:aws:states:::lambda:invoke",
              ResultPath: "$.source_text",
              Parameters: {
                FunctionName: this.functions["ExtractText"].fn.functionArn,
                Payload: {
                  "region.$": "$.region",
                  "bucket.$": "$.detail.bucket.name",
                  "object.$": "$.detail.object.key",
                },
              },
              Retry: [
                {
                  ErrorEquals: [
                    "Lambda.ServiceException",
                    "Lambda.AWSLambdaException",
                    "Lambda.SdkClientException",
                    "Lambda.TooManyRequestsException",
                  ],
                  IntervalSeconds: 2,
                  MaxAttempts: 6,
                  BackoffRate: 2,
                },
              ],
              Next: "AnalyzeSentiment",
            },
            AnalyzeSentiment: {
              Type: "Task",
              Resource: "arn:aws:states:::lambda:invoke",
              InputPath: "$",
              ResultPath: "$.sentiment",
              Parameters: {
                FunctionName: this.functions["AnalyzeSentiment"].fn.functionArn,
                Payload: {
                  "region.$": "$.region",
                  "source_text.$": "$.source_text.Payload",
                },
              },
              Retry: [
                {
                  ErrorEquals: [
                    "Lambda.ServiceException",
                    "Lambda.AWSLambdaException",
                    "Lambda.SdkClientException",
                    "Lambda.TooManyRequestsException",
                  ],
                  IntervalSeconds: 2,
                  MaxAttempts: 6,
                  BackoffRate: 2,
                },
              ],
              Next: "ContinueIfPositive",
            },
            ContinueIfPositive: {
              Type: "Choice",
              Choices: [
                {
                  Variable: "$.sentiment.Payload.sentiment",
                  StringEquals: "POSITIVE",
                  Next: "TranslateText",
                },
              ],
              Default: "PutNegativeComment",
            },
            TranslateText: {
              Type: "Task",
              Resource: "arn:aws:states:::lambda:invoke",
              InputPath: "$",
              ResultPath: "$.translated_text",
              Parameters: {
                FunctionName: this.functions["TranslateText"].fn.functionArn,
                Payload: {
                  "region.$": "$.region",
                  "extracted_text.$": "$.source_text.Payload",
                  "source_language_code.$": "$.sentiment.Payload.language_code",
                },
              },
              Retry: [
                {
                  ErrorEquals: [
                    "Lambda.ServiceException",
                    "Lambda.AWSLambdaException",
                    "Lambda.SdkClientException",
                    "Lambda.TooManyRequestsException",
                  ],
                  IntervalSeconds: 2,
                  MaxAttempts: 6,
                  BackoffRate: 2,
                },
              ],
              Next: "SynthesizeAudio",
            },
            SynthesizeAudio: {
              Type: "Task",
              Resource: "arn:aws:states:::lambda:invoke",
              InputPath: "$",
              ResultPath: "$.audio_key",
              Parameters: {
                FunctionName: this.functions["SynthesizeAudio"].fn.functionArn,
                Payload: {
                  "region.$": "$.region",
                  "translated_text.$":
                    "$.translated_text.Payload.translated_text",
                  "bucket.$": "$.detail.bucket.name",
                  "object.$": "$.detail.object.key",
                },
              },
              Retry: [
                {
                  ErrorEquals: [
                    "Lambda.ServiceException",
                    "Lambda.AWSLambdaException",
                    "Lambda.SdkClientException",
                    "Lambda.TooManyRequestsException",
                  ],
                  IntervalSeconds: 2,
                  MaxAttempts: 7,
                  BackoffRate: 2,
                },
              ],
              Next: "PutPositiveComment",
            },
            PutNegativeComment: {
              Type: "Task",
              Resource: "arn:aws:states:::dynamodb:putItem",
              InputPath: "$",
              Parameters: {
                TableName: database.table.tableName,
                Item: {
                  [AppDatabase.KEY]: { "S.$": "$.detail.object.key" },
                  [AppDatabase.INDEX]: {
                    "S.$": "$.sentiment.Payload.sentiment",
                  },
                  source_text: { "S.$": "$.source_text.Payload" },
                },
              },
              End: true,
            },
            PutPositiveComment: {
              Type: "Task",
              Resource: "arn:aws:states:::dynamodb:putItem",
              InputPath: "$",
              Parameters: {
                TableName: database.table.tableName,
                Item: {
                  [AppDatabase.KEY]: { "S.$": "$.detail.object.key" },
                  [AppDatabase.INDEX]: {
                    "S.$": "$.sentiment.Payload.sentiment",
                  },
                  source_language: {
                    "S.$": "$.sentiment.Payload.language_code",
                  },
                  source_text: { "S.$": "$.source_text.Payload" },
                  translated_text: {
                    "S.$": "$.translated_text.Payload.translated_text",
                  },
                  audio_key: { "S.$": "$.audio_key.Payload" },
                },
              },
              End: true,
            },
          },
        })
      ),
    });

    this.stateMachine.role.attachInlinePolicy(
      new Policy(this, "dynamodb-policy", {
        statements: [
          new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ["dynamodb:PutItem"],
            resources: [database.table.tableArn],
          }),
        ],
      })
    );
  }
}
