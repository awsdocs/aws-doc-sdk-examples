// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to build this package.
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import PackageDescription

let package = Package(
    name: "CognitoIdentityDemo",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
    products: [
      .library(name: "CognitoIdentityHandler", targets: ["CognitoIdentityHandler"]),
      .executable(name: "CognitoIdentityDemo", targets: ["CognitoIdentityDemo"])
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            name: "AWSSwiftSDK",
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.1.1"
        )
    ],
    targets: [
        // A library target containing the demo's classes
        .target(
            name: "CognitoIdentityHandler",
            dependencies: [
                .product(name: "AWSCognitoIdentity", package: "AWSSwiftSDK"),
            ],
            path: "./Sources/CognitoIdentityHandler"
        ),
        // The target of the tests
        .testTarget(
            name: "CognitoIdentityHandlerTests",
            dependencies: [
                "CognitoIdentityDemo",
                .product(name: "AWSCognitoIdentity", package: "AWSSwiftSDK"),
            ],
            path: "./Tests/CognitoIdentityHandlerTests"
        ),
        // The target of the main executable program
        .executableTarget(
            name: "CognitoIdentityDemo",
            dependencies: [
                "CognitoIdentityHandler"
            ],
            path: "./Sources/CognitoIdentityDemo"
        ),
    ]
)
