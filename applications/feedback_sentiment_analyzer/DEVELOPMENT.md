# FSA development

This document describes the process for adding an additional set of AWS Lambda functions for use with the Feedback Sentiment Analyzer (FSA). The requirements for each individual Lambda function are in [SPECIFICATION.md](./SPECIFICATION.md).

## Add new Lambda function configurations

The `FUNCTIONS` map in [functions.ts](./cdk/lib/functions.ts) maps an arbitrary deployment name to a list of Lambda functions required by FSA. This is the main entry point for defining your Lambda functions that will run with FSA. These functions are deployed when you run `cdk deploy` as described in the [README](./README.md). The following steps explain how to add new functions.

1. Add a new entry to the `FUNCTIONS` constant. For example, adding JavaScript functions might look like this:

```typescript
const FUNCTIONS: Record<string, AppFunctionConfig[]> = {
  examplelang: EXAMPLE_LANG_FUNCTIONS,
  javascript: JAVASCRIPT_FUNCTIONS,
};
```

2. `JAVASCRIPT_FUNCTIONS` is undefined. Define a constant named `JAVASCRIPT_FUNCTIONS` and set it equal to an empty `AppFunctionConfig` array.

```typescript
const JAVASCRIPT_FUNCTIONS: AppFunctionConfig[] = [];
```

3. Define the objects inside the `JAVASCRIPT_FUNCTIONS` array. Each object is an [AppFunctionConfig](./cdk/lib/constructs/app-lambdas.ts#4) which extends [FunctionProps](https://docs.aws.amazon.com/cdk/api/v2/docs/aws-cdk-lib.aws_lambda.FunctionProps.html) from the AWS Cloud Development Kit (AWS CDK) library. FSA requires four named Lambda functions:

- ExtractText
- AnalyzeSentiment
- TranslateText
- SynthesizeAudio

A definition for the `ExtractText` function might look like the following:

```typescript
  {
    ...BASE_APP_FUNCTION,
    codeAsset() {
      return Code.fromInline(`
        exports.handler = async (event) => {
          // implementation goes here
        }
    `);
    },
    name: "ExtractText",
  },
```

The `codeAsset` function must return a [Code](https://docs.aws.amazon.com/cdk/api/v2/docs/aws-cdk-lib.aws_lambda.Code.html) object. You can include inline code or [create a bundled asset](#bundling).

## Implement Lambda functions

FSA requires a specific set of Lambda functions The accompanying CDK script orchestrates these functions as [AWS Step Functions](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html). For a detailed specification of these functions visit the following links in [SPECIFICATION.md](./SPECIFICATION.md)

- [ExtractText](./SPECIFICATION.md#extracttext)
- [AnalyzeSentiment](./SPECIFICATION.md#analyzesentiment)
- [TranslateText](./SPECIFICATION.md#translatetext)
- [SynthesizeAudio](./SPECIFICATION.md#synthesizeaudio)

## Bundling (language dependent)

The AWS CDK can compile resources during the deployment using Docker. To configure this, use the [`bundling`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-s3-assets.AssetOptions.html#bundling) option of `Code.fromAsset`. Languages with a well-known Runtime usually have a `Runtime.image` property that has an appropriate Docker base image. Otherwise, any public Docker image is suitable.

Docker will execute the `command` for the bundling step.
The assets, specified as the first argument to `Code.fromAsset`, are mounted in the container at `/asset-input/` (which is also the working directory).
The command should perform any compilation steps necessary, and move any necessary artifacts to `/asset-output/`.
The contents of `/asset-output/` are archived in the Lambda's Amazon Simple Storage Service (Amazon S3) bucket. These contents are the executable that the Runtime uses.
