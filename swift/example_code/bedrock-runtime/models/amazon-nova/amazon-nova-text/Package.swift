// swift-tools-version: 6.1
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// 
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AmazonNovaText",
    // Let Xcode know the minimum Apple platforms supported.
    platforms: [
        .macOS(.v13),
        .iOS(.v15)
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(url: "https://github.com/awslabs/aws-sdk-swift", from: "1.2.61")
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .executableTarget(
            name: "Converse",
            dependencies: [
                .product(name: "AWSBedrockRuntime", package: "aws-sdk-swift"),
            ],
            path: "Sources/Converse"
        ),
        .executableTarget(
            name: "ConverseStream",
            dependencies: [
                .product(name: "AWSBedrockRuntime", package: "aws-sdk-swift"),
            ],
            path: "Sources/ConverseStream"
        )
    ]
)
