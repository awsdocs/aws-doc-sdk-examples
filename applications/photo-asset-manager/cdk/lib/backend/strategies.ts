// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { BundlingOutput, Duration } from "aws-cdk-lib";
import { Architecture, Code, Runtime } from "aws-cdk-lib/aws-lambda";
import { resolve } from "path";
import { PamLambdasStrategy } from "./lambdas";
import { execSync } from "child_process";

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
  architecture: Architecture.X86_64,
};

export const JAVA_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  ...EMPTY_LAMBDAS_STRATEGY,
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
          "mvn install && cp /asset-input/target/PhotoAssetRestSDK-1.0-SNAPSHOT.jar /asset-output/",
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
  ...EMPTY_LAMBDAS_STRATEGY,
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

export const JAVASCRIPT_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  ...EMPTY_LAMBDAS_STRATEGY,
  memorySize: 256,
  timeout: Duration.minutes(5),
  codeAsset() {
    const js = resolve(
      "../../../javascriptv3/example_code/cross-services/photo-asset-manager"
    );
    return Code.fromAsset(js, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          "npm i && \
          npm run build && \
          cp /asset-input/dist/index.mjs /asset-output/",
        ],
        outputType: BundlingOutput.NOT_ARCHIVED,
        user: "root",
        image: this.runtime.bundlingImage,
      },
    });
  },
  handlers: {
    detectLabels: "index.handlers.detectLabels",
    download: "index.handlers.download",
    labels: "index.handlers.labels",
    upload: "index.handlers.upload",
  },
};

export const DOTNET_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  ...EMPTY_LAMBDAS_STRATEGY,
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset() {
    // Relative to cdk.json.
    const dotnetSources = resolve(
      "../../../dotnetv3/cross-service/PhotoAssetManager"
    );

    return Code.fromAsset(dotnetSources, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          " dotnet tool install -g Amazon.Lambda.Tools" +
            " && dotnet build" +
            " && cd PamApi" +
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
  ...EMPTY_LAMBDAS_STRATEGY,
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset() {
    // Relative to cdk.json.
    const dotnetSources = resolve(
      "../../../dotnetv3/cross-service/PhotoAssetManager"
    );

    return Code.fromAsset(dotnetSources, {
      bundling: {
        command: [
          "/bin/sh",
          "-c",
          " dotnet tool install -g Amazon.Lambda.Tools" +
            " && dotnet build" +
            " && cd PamApiAnnotations" +
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
    detectLabels:
      "PamApiAnnotations::PamApiAnnotations.DetectLabelsFunction::FunctionHandler",
    download:
      "PamApiAnnotations::PamApiAnnotations.DownloadFunction::FunctionHandler",
    labels:
      "PamApiAnnotations::PamApiAnnotations.Functions_GetLabels_Generated::GetLabels",
    upload:
      "PamApiAnnotations::PamApiAnnotations.Functions_Upload_Generated::Upload",
  },
};

export const RUST_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  ...EMPTY_LAMBDAS_STRATEGY,
  codeAsset() {
    const rustSources = resolve(
      "../../../rustv1/cross_service/photo_asset_management"
    );

    console.log(
      "RUST: Cross compiling zip from local sources using `cargo lambda`"
    );
    execSync("cargo lambda build --release --arm64 --output-format Zip", {
      cwd: rustSources,
    });
    const rustZip = resolve(
      "../../../rustv1/target/lambda/pam/bootstrap.zip"
    );
    return Code.fromAsset(rustZip);

    // At this time, the `cargo-lambda` downloads the entire crates registry on
    // every build (and sometimes fails). Until it's stable, it's not appropriate
    // to use for the bundler.
    // return Code.fromAsset(rustSources, {
    //   bundling: {
    //     command: [
    //       "/bin/sh",
    //       "-c",
    //       "cargo lambda build --release --arm64 --output-format Zip && " +
    //         "cp /asset-input/target/lambda/pam/bootstrap.zip /asset-output/",
    //     ],
    //     image: DockerImage.fromRegistry("ghcr.io/cargo-lambda/cargo-lambda"),
    //     user: "root",
    //     outputType: BundlingOutput.ARCHIVED,
    //   },
    // });
  },
  runtime: Runtime.PROVIDED_AL2,
  architecture: Architecture.ARM_64,
  handlers: {
    detectLabels: "detect_labels",
    download: "download",
    labels: "labels",
    upload: "upload",
  },
};

export const CPP_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  timeout: Duration.seconds(90),
  memorySize: 1024,
  codeAsset() {
    // Relative to cdk.json.
    const dockerBuildPath = resolve("../../../cpp/example_code/cross-service/photo_asset_manager/");

    return Code.fromDockerBuild(dockerBuildPath, {
      buildArgs: {
        buildArgsKey: '.',
      },
  });
  },

  runtime: Runtime.PROVIDED_AL2,
  architecture: Architecture.ARM_64, // The same architecture as the machine building the code.
  handlers: {
    ...EMPTY_LAMBDAS_STRATEGY.handlers,
    detectLabels: "detectLabels",
    download: "download",
    labels: "getLabels",
    upload: "upload",
  },
};

export const PHP_LAMBDAS_STRATEGY: PamLambdasStrategy = {
  ...EMPTY_LAMBDAS_STRATEGY,
  memorySize: 256,
  timeout: Duration.minutes(5),
  codeAsset() {
    let phpPAMZip = resolve(
        "../../../php/applications/photo_asset_manager/bootstrap.zip"
    );
    return Code.fromAsset(phpPAMZip);
  },
  runtime: Runtime.PROVIDED_AL2,
  architecture: Architecture.X86_64,
  handlers: {
    detectLabels: "detectLabels",
    download: "download",
    labels: "labels",
    upload: "upload",
  },
};

export const STRATEGIES: Record<string, PamLambdasStrategy> = {
  java: JAVA_LAMBDAS_STRATEGY,
  javascript: JAVASCRIPT_LAMBDAS_STRATEGY,
  python: PYTHON_LAMBDAS_STRATEGY,
  dotnet: DOTNET_LAMBDAS_STRATEGY,
  dotnetla: DOTNET_LAMBDAS_ANNOTATIONS_STRATEGY,
  rust: RUST_LAMBDAS_STRATEGY,
  cpp: CPP_LAMBDAS_STRATEGY,
  php: PHP_LAMBDAS_STRATEGY,
  empty: EMPTY_LAMBDAS_STRATEGY,
};

export function getStrategy(language: string = ""): PamLambdasStrategy {
  language = language.toLowerCase();
  return STRATEGIES[language] ?? EMPTY_LAMBDAS_STRATEGY;
}
