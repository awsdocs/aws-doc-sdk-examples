import { BundlingOutput } from "aws-cdk-lib";
import { Code, Runtime } from "aws-cdk-lib/aws-lambda";
import { resolve } from "path";
import { PamLambdasStrategy } from "./lambdas";

export const EMPTY_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  codeAsset() {
    return Code.fromAsset("");
  },
  runtime: Runtime.NODEJS_18_X,
  handlers: {
    // archive: "",
    copy: "",
    detectLabels: "",
    download: "",
    labels: "",
    upload: "",
    // zipArchive: "",
  },
};
export const JAVA_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  codeAsset() {
    // Relative to cdk.json
    const javaSources = resolve(
      "../../../../javav2/usecases/pam_source_files/"
    );

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
          // # This shares the maven repo between host & container,
          // # which both speeds up and
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
    // archive: "",
    copy: "com.example.photo.handlers.S3Copy",
    detectLabels: "com.example.photo.handlers.S3Trigger",
    download: "com.example.photo.handlers.Restore",
    labels: "com.example.photo.handlers.GetHandler",
    upload: "com.example.photo.handlers.UploadHandler",
    // zipArchive: "com.example.photo.handlers.ZipArchiveHandler",
  },
};

export const PYTHON_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  codeAsset() {
    // Relative to cdk.json
    const pythonSources = resolve("./rekognition_photo_analyzer/lambda");
    return Code.fromAsset(pythonSources);
  },
  runtime: Runtime.PYTHON_3_9,
  handlers: {
    // archive: "",
    copy: "",
    detectLabels: "",
    download: "",
    labels: "",
    upload: "",
    // zipArchive: "",
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
