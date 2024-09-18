// swift-tools-version: 5.10
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[lambda.swift.function.package]
import PackageDescription

let package = Package(
    name: "LambdaExample",
    platforms: [
        .macOS(.v12)
    ],
    // The product is an executable named "LambdaExample", which is built
    // using the target "LambdaExample".
    products: [
        .executable(name: "LambdaExample", targets: ["LambdaExample"])
    ],
    // Add the dependencies: these are the packages that need to be fetched
    // before building the project.
    dependencies: [
        .package(
            url: "https://github.com/swift-server/swift-aws-lambda-runtime.git",
            from: "1.0.0-alpha"),
        .package(url: "https://github.com/awslabs/aws-sdk-swift.git",
            from: "1.0.0"),
    ],
    targets: [
        // Add the executable target for the main program. These are the
        // specific modules this project uses within the packages listed under
        // "dependencies."
        .executableTarget(
            name: "LambdaExample",
            dependencies: [
                .product(name: "AWSLambdaRuntime", package: "swift-aws-lambda-runtime"),
                .product(name: "AWSS3", package: "aws-sdk-swift"),
            ]
        )
    ]
)
// snippet-end:[lambda.swift.function.package]
