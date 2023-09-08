/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { resolve } from "path";
import { BundlingOutput, Duration } from "aws-cdk-lib";
import { Code, Runtime } from "aws-cdk-lib/aws-lambda";

import { AppFunctionConfig } from "./constructs/app-lambdas";

const BASE_APP_FUNCTION: AppFunctionConfig = {
  name: "TestLambda",
  timeout: Duration.seconds(10),
  memorySize: 128,
  runtime: Runtime.NODEJS_18_X,
  handler: "index.handler",
  codeAsset() {
    return Code.fromInline(`
      exports.handler = async (event) => {
        console.log(event);
        return "Blah"
      }
  `);
  },
  // Alternatively, you can bundle code at deploy time. Here's an example with JavaScript.
  //
  // codeAsset() {
  //   const source = resolve("../../../path/to/source");
  //   return Code.fromAsset(source, {
  //     bundling: {
  //       command: [
  //         "/bin/sh",
  //         "-c",
  //         "npm i && \
  //          npm run build && \
  //          cp /asset-input/dist/index.mjs /asset-output/",
  //       ],
  //       outputType: BundlingOutput.NOT_ARCHIVED,
  //       user: "root",
  //       image: this.runtime.bundlingImage,
  //     },
  //   });
  // },
};

const EXAMPLE_LANG_FUNCTIONS: AppFunctionConfig[] = [
  // The 'name' property must match the examples below in new examples.
  { ...BASE_APP_FUNCTION, name: "ExtractText" },
  // Override properties by including them after expanding the function object.
  { ...BASE_APP_FUNCTION, memorySize: 256, name: "AnalyzeSentiment" },
  {
    ...BASE_APP_FUNCTION,
    codeAsset() {
      return Code.fromInline(`
        exports.handler = async (event) => {
          console.log("AnalyzeSentiment", event);
          return { translated_text: "Bonjour", source_language: "en" }
        }
    `);
    },
    name: "TranslateText",
  },
  { ...BASE_APP_FUNCTION, name: "SynthesizeAudio" },
];

const RUBY_ROOT =
  "../../../ruby/cross_service_examples/feedback_sentiment_analyzer/";
const RUBY_CODE = Code.fromAsset(RUBY_ROOT);
const BASE_RUBY_FUNCTION: AppFunctionConfig = {
  ...BASE_APP_FUNCTION,
  runtime: Runtime.RUBY_3_2,
  timeout: Duration.seconds(30),
  codeAsset() {
    return RUBY_CODE;
  },
};

const RUBY_FUNCTIONS: AppFunctionConfig[] = [
  // The 'name' property must match the examples below in new examples.
  {
    ...BASE_RUBY_FUNCTION,
    name: "ExtractText",
    handler: "textract_lambda_handler.lambda_handler",
  },
  {
    ...BASE_RUBY_FUNCTION,
    name: "AnalyzeSentiment",
    handler: "comprehend_lambda_handler.lambda_handler",
  },
  {
    ...BASE_RUBY_FUNCTION,
    name: "TranslateText",
    handler: "translate_lambda_handler.lambda_handler",
  },
  {
    ...BASE_RUBY_FUNCTION,
    name: "SynthesizeAudio",
    handler: "polly_lambda_handler.lambda_handler",
  },
];

const JAVASCRIPT_BUNDLING_CONFIG = {
  command: [
    "/bin/sh",
    "-c",
    "npm i && \
   npm run build && \
   cp /asset-input/dist/index.mjs /asset-output/",
  ],
  outputType: BundlingOutput.NOT_ARCHIVED,
  user: "root",
  image: Runtime.NODEJS_18_X.bundlingImage,
};

const JAVASCRIPT_FUNCTIONS = [
  {
    ...BASE_APP_FUNCTION,
    name: "ExtractText",
    codeAsset() {
      const source = resolve(
        "../../../javascriptv3/example_code/cross-services/feedback-sentiment-analyzer/ExtractText"
      );
      return Code.fromAsset(source, {
        bundling: JAVASCRIPT_BUNDLING_CONFIG,
      });
    },
  },
  {
    ...BASE_APP_FUNCTION,
    name: "AnalyzeSentiment",
    codeAsset() {
      const source = resolve(
        "../../../javascriptv3/example_code/cross-services/feedback-sentiment-analyzer/AnalyzeSentiment"
      );
      return Code.fromAsset(source, {
        bundling: JAVASCRIPT_BUNDLING_CONFIG,
      });
    },
  },
  {
    ...BASE_APP_FUNCTION,
    name: "TranslateText",
    codeAsset() {
      const source = resolve(
        "../../../javascriptv3/example_code/cross-services/feedback-sentiment-analyzer/TranslateText"
      );
      return Code.fromAsset(source, {
        bundling: JAVASCRIPT_BUNDLING_CONFIG,
      });
    },
  },
  {
    ...BASE_APP_FUNCTION,
    name: "SynthesizeAudio",
    codeAsset() {
      const source = resolve(
        "../../../javascriptv3/example_code/cross-services/feedback-sentiment-analyzer/SynthesizeAudio"
      );
      return Code.fromAsset(source, {
        bundling: JAVASCRIPT_BUNDLING_CONFIG,
      });
    },
  },
];


/*
const JAVA_BUNDLING_CONFIG = {
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset: () => {
    // Relative to cdk.json.
    const javaSources = resolve("../../../javav2/usecases/creating_fsa_app/");

    return Code.fromAsset(javaSources, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          "mvn install && cp /asset-input/target/creating_fsa_app-1.0-SNAPSHOT.jar /asset-output/",
        ],
        image: Runtime.JAVA_11.bundlingImage,
        user: "root",
        outputType: BundlingOutput.ARCHIVED,
        volumes: [
          {
            hostPath: `${process.env["HOME"]}/.m2/`,
            containerPath: "/root/.m2",
          },
        ],
      },
    });
  },
  runtime: Runtime.JAVA_11,
  handlers: {
    ExtractText: "com.example.fsa.handlers.S3Handler",
    AnalyzeSentiment: "com.example.fsa.handlers.SentimentHandler",
    TranslateText: "com.example.fsa.handlers.TranslateHandler",
    fnSynthesizeAudio: "com.example.fsa.handlers.PollyHandler",
  },
};
*/

const JAVA_BUNDLING_CONFIG = {
  command: [
    "/bin/sh",
    "-c",
    "mvn install && cp /asset-input/target/creating_fsa_app-1.0-SNAPSHOT.jar /asset-output/",
  ],
  outputType: BundlingOutput.ARCHIVED,
  user: "root",
  image: Runtime.JAVA_11.bundlingImage,
  volumes: [
    {
      hostPath: `${process.env["HOME"]}/.m2/`,
      containerPath: "/root/.m2",
    },
  ],
 
};

const JAVA_FUNCTIONS: AppFunctionConfig[] = [
  {
    ...BASE_APP_FUNCTION,
    name: "ExtractText",
    codeAsset: () => {
      const source = resolve(
        "../../../javav2/usecases/creating_fsa_app"
      );
      return Code.fromAsset(source, {
        bundling: JAVA_BUNDLING_CONFIG,
      });
    },
    handler: "com.example.fsa.handlers.S3Handler"
  },
  {
    ...BASE_APP_FUNCTION,
    name: "AnalyzeSentiment",
    codeAsset: () => {
      const source = resolve(
        "../../../javav2/usecases/creating_fsa_app"
      );
      return Code.fromAsset(source, {
        bundling: JAVA_BUNDLING_CONFIG,
      });
    },
    handler: "com.example.fsa.handlers.SentimentHandler"
  },
  {
    ...BASE_APP_FUNCTION,
    name: "TranslateText",
    codeAsset: () => {
      const source = resolve(
        "../../../javav2/usecases/creating_fsa_app"
      );
      return Code.fromAsset(source, {
        bundling: JAVA_BUNDLING_CONFIG,
      });
    },
    handler: "com.example.fsa.handlers.TranslateHandler"
  },
  {
    ...BASE_APP_FUNCTION,
    name: "SynthesizeAudio",
    codeAsset: () => {
      const source = resolve(
        "../../../javav2/usecases/creating_fsa_app"
      );
      return Code.fromAsset(source, {
        bundling: JAVA_BUNDLING_CONFIG,
      });
    },
    handler: "com.example.fsa.handlers.PollyHandler"
  },
];

const FUNCTIONS: Record<string, AppFunctionConfig[]> = {
  examplelang: EXAMPLE_LANG_FUNCTIONS,
  // Add more languages here. For example
  // javascript: JAVASCRIPT_FUNCTIONS,
  ruby: RUBY_FUNCTIONS,
  java: JAVA_FUNCTIONS,
  javascript: JAVASCRIPT_FUNCTIONS,
};

export function getFunctions(language: string = ""): AppFunctionConfig[] {
  return FUNCTIONS[language] ?? FUNCTIONS.examplelang;
}
