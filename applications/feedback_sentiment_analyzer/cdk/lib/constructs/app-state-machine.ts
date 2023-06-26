import { Construct } from "constructs";
import { AppFunction } from "./app-lambdas";
import { DefinitionBody, StateMachine } from "aws-cdk-lib/aws-stepfunctions";

export class AppStateMachine extends Construct {
  readonly stateMachine: StateMachine;

  constructor(
    scope: Construct,
    id: string,
    readonly functions: Record<string, AppFunction>
  ) {
    super(scope, id);
    this.stateMachine = this.createStateMachine(functions);
  }

  private createStateMachine({
    ExtractText,
    AnalyzeSentiment,
    TranslateText,
    SynthesizeAudio,
    GetFeedback,
  }: Record<string, AppFunction>) {
    if (
      ![
        ExtractText,
        AnalyzeSentiment,
        TranslateText,
        SynthesizeAudio,
        GetFeedback,
      ].every(Boolean)
    ) {
      throw new Error(`
      Function mismatch. 
      You must provide all of the following functions:
      ExtractText: ${ExtractText},
      AnalyzeSentiment: ${AnalyzeSentiment},
      TranslateText: ${TranslateText},
      SynthesizeAudio: ${SynthesizeAudio},
      GetFeedback: ${GetFeedback},
      `);
    }

    return new StateMachine(this, "state-machine", {
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
                "Payload.$": "$",
                FunctionName: `${this.functions["ExtractText"].fn.functionArn}`,
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
              InputPath: "$.source_text",
              ResultPath: "$.sentiment",
              Parameters: {
                FunctionName: `${this.functions["AnalyzeSentiment"].fn.functionArn}`,
                "Payload.$": "$",
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
                  Variable: "$.sentiment['Payload']",
                  StringEquals: "NEGATIVE",
                  Next: "TranslateText",
                },
              ],
              Default: "CreateComment",
            },
            TranslateText: {
              Type: "Task",
              Resource: "arn:aws:states:::lambda:invoke",
              InputPath: "$.source_text",
              ResultPath: "$.translated_text",
              Parameters: {
                "Payload.$": "$",
                FunctionName: `${this.functions["TranslateText"].fn.functionArn}`,
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
              InputPath: "$.translated_text",
              ResultPath: "$.audio_key",
              Parameters: {
                "Payload.$": "$",
                FunctionName: `${this.functions["SynthesizeAudio"].fn.functionArn}`,
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
              Next: "CreateComment",
            },
            CreateComment: {
              Type: "Task",
              Resource: "arn:aws:states:::dynamodb:putItem",
              InputPath: "$",
              Parameters: {
                TableName: "comments",
                Item: {
                  comment_key: "$.detail.object.key",
                  source_text: "$.source_text.Payload",
                  sentiment: "$.sentiment.Payload",
                  translated_text: "$.translated_text.Payload.translated_text",
                  source_language: "$.translated_text.Payload.source_language",
                  audio_key: "$.audio_key.Payload",
                },
              },
              End: true,
            },
          },
        })
      ),
    });
  }
}
