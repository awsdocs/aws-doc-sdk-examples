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
        return { calledLambdas: [...(event.calledLambdas) || [], "${this.name}"] }
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
  { ...BASE_APP_FUNCTION, name: "ExtractText" },
  // Override properties by including them after expanding the function object.
  { ...BASE_APP_FUNCTION, memorySize: 256, name: "AnalyzeSentiment" },
  { ...BASE_APP_FUNCTION, name: "TranslateText" },
  { ...BASE_APP_FUNCTION, name: "SynthesizeAudio" },
  { ...BASE_APP_FUNCTION, name: "GetFeedback" },
];

const FUNCTIONS: Record<string, AppFunctionConfig[]> = {
  // These functions are used in a simple pipeline. Each function is called
  // with the previous function's outputs.
  examplelang: EXAMPLE_LANG_FUNCTIONS,
  // Add more languages here. For example
  // javascript: JAVASCRIPT_FUNCTIONS,
};

export function getFunctions(language: string = ""): AppFunctionConfig[] {
  return FUNCTIONS[language] ?? FUNCTIONS.examplelang;
}
