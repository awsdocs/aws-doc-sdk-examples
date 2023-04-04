import { BundlingOutput, Duration } from "aws-cdk-lib";
import { Code, Runtime } from "aws-cdk-lib/aws-lambda";
import { resolve } from "path";
import { PamLambdasStrategy } from "./lambdas";

export const EMPTY_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(10),
  memorySize: 128,
  codeAsset() {
    return Code.fromAsset("");
  },
  runtime: Runtime.NODEJS_18_X,
  handlers: {
    detectLabels: "",
    download: "",
    labels: "",
    upload: "",
  },
};

export const JAVA_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset() {
    // Relative to cdk.json.
    const javaSources = resolve("../../../javav2/usecases/pam_source_files/");

    return Code.fromAsset(javaSources, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          "mvn install && \
                      cp /asset-input/target/PhotoAssetRestSDK-1.0-SNAPSHOT.jar /asset-output/",
        ],
        image: this.runtime.bundlingImage,
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
    ...EMPTY_LAMBDAS_STRATEGY.handlers,
    detectLabels: "com.example.photo.handlers.S3Handler",
    download: "com.example.photo.handlers.RestoreHandler",
    labels: "com.example.photo.handlers.GetHandler",
    upload: "com.example.photo.handlers.UploadHandler",
  },
};

export const PYTHON_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(60),
  memorySize: 512,
  codeAsset() {
    // Relative to cdk.json.
    const pythonSources = resolve("./rekognition_photo_analyzer");
    return Code.fromAsset(pythonSources);
  },
  runtime: Runtime.PYTHON_3_9,
  handlers: {
    ...EMPTY_LAMBDAS_STRATEGY.handlers,
  },
};

export const STRATEGIES: Record<string, PamLambdasStrategy> = {
  java: JAVA_LAMBDAS_STRATEGY,
  python: PYTHON_LAMBDAS_STRATEGY,
  empty: EMPTY_LAMBDAS_STRATEGY,
};

export function getStrategy(language: string = ""): PamLambdasStrategy {
  language = language.toLowerCase();
  return STRATEGIES[language] ?? EMPTY_LAMBDAS_STRATEGY;
}
