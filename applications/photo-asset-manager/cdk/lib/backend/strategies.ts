import { BundlingOutput, Duration } from "aws-cdk-lib";
import { Code, Runtime } from "aws-cdk-lib/aws-lambda";
import { resolve } from "path";
import { PamLambdasStrategy } from "./lambdas";

export const EMPTY_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(10),
  memorySize: 128,
  codeAsset() {
    return Code.fromAsset("./missing");
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

export const DOTNET_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset() {
    // Relative to cdk.json.
    const dotnetSources = resolve("../../../dotnetv3/cross-service/PhotoAssetManager");

    return Code.fromAsset(dotnetSources, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          " dotnet tool install -g Amazon.Lambda.Tools"+
          " && dotnet build"+
          " && cd PamApi"+
          " && dotnet lambda package --output-package /asset-output/function.zip",
        ],
        image: Runtime.DOTNET_6.bundlingImage,
        user: "root",
        outputType: BundlingOutput.ARCHIVED,
      },
    });
  },
  runtime: Runtime.DOTNET_6,
  handlers: {
    ...EMPTY_LAMBDAS_STRATEGY.handlers,
    detectLabels: "PamApi::PamApi.DetectLabelsFunction::FunctionHandler",
    download: "PamApi::PamApi.DownloadFunction::FunctionHandler",
    labels: "PamApi::PamApi.LambdaEntryPoint::FunctionHandlerAsync",
    upload: "PamApi::PamApi.LambdaEntryPoint::FunctionHandlerAsync",
  },
};

export const DOTNET_LAMBDAS_ANNOTATIONS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset() {
    // Relative to cdk.json.
    const dotnetSources = resolve("../../../dotnetv3/cross-service/PhotoAssetManager");

    return Code.fromAsset(dotnetSources, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          " dotnet tool install -g Amazon.Lambda.Tools"+
          " && dotnet build"+
          " && cd PamApiAnnotations"+
          " && dotnet lambda package --output-package /asset-output/function.zip",
        ],
        image: Runtime.DOTNET_6.bundlingImage,
        user: "root",
        outputType: BundlingOutput.ARCHIVED,
      },
    });
  },
  runtime: Runtime.DOTNET_6,
  handlers: {
    ...EMPTY_LAMBDAS_STRATEGY.handlers,
    detectLabels: "PamApiAnnotations::PamApiAnnotations.DetectLabelsFunction::FunctionHandler",
    download: "PamApiAnnotations::PamApiAnnotations.DownloadFunction::FunctionHandler",
    labels: "PamApiAnnotations::PamApiAnnotations.Functions_GetLabels_Generated::GetLabels",
    upload: "PamApiAnnotations::PamApiAnnotations.Functions_Upload_Generated::Upload",
  },
};

export const STRATEGIES: Record<string, PamLambdasStrategy> = {
  java: JAVA_LAMBDAS_STRATEGY,
  python: PYTHON_LAMBDAS_STRATEGY,
  dotnet: DOTNET_LAMBDAS_STRATEGY,
  dotnetla: DOTNET_LAMBDAS_ANNOTATIONS_STRATEGY,
  empty: EMPTY_LAMBDAS_STRATEGY,
};

export function getStrategy(language: string = ""): PamLambdasStrategy {
  language = language.toLowerCase();
  return STRATEGIES[language] ?? EMPTY_LAMBDAS_STRATEGY;
}
