import { Duration } from "aws-cdk-lib";
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

const FUNCTIONS: Record<string, AppFunctionConfig[]> = {
  examplelang: EXAMPLE_LANG_FUNCTIONS,
  // Add more languages here. For example
  // javascript: JAVASCRIPT_FUNCTIONS,
  ruby: RUBY_FUNCTIONS,
};

export function getFunctions(language: string = ""): AppFunctionConfig[] {
  return FUNCTIONS[language] ?? FUNCTIONS.examplelang;
}
