// swift-tools-version:5.9
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    // snippet-start:[cognitoidentity.swift.package-attributes]
    name: "FindOrCreateIdentityPool",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
    products: [
      .library(name: "CognitoIdentityHandler", targets: ["CognitoIdentityHandler"]),
      .executable(name: "FindOrCreateIdentityPool", targets: ["FindOrCreateIdentityPool"])
    ],
    // snippet-end:[cognitoidentity.swift.package-attributes]
    // snippet-start:[cognitoidentity.swift.package-dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "1.0.0"
        )
    ],
    // snippet-end:[cognitoidentity.swift.package-dependencies]
    // snippet-start:[cognitoidentity.swift.package-targets]
    targets: [
        // A library target containing the demo's classes
        // snippet-start:[cognitoidentity.swift.package-target-handler]
        .target(
            name: "CognitoIdentityHandler",
            dependencies: [
                .product(name: "AWSCognitoIdentity", package: "aws-sdk-swift"),
            ],
            path: "./Sources/CognitoIdentityHandler"
        ),
        // snippet-end:[cognitoidentity.swift.package-target-handler]
        // The target of the tests
        // snippet-start:[cognitoidentity.swift.package-target-tests]
        .testTarget(
            name: "CognitoIdentityHandlerTests",
            dependencies: [
                "FindOrCreateIdentityPool",
                .product(name: "AWSCognitoIdentity", package: "aws-sdk-swift"),
            ],
            path: "./Tests/CognitoIdentityHandlerTests"
        ),
        // snippet-end:[cognitoidentity.swift.package-target-tests]
        // The target of the main executable program
        // snippet-start:[cognitoidentity.swift.package-target-executable]
        .executableTarget(
            name: "FindOrCreateIdentityPool",
            dependencies: [
                "CognitoIdentityHandler"
            ],
            path: "./Sources/FindOrCreateIdentityPool"
        ),
        // snippet-end:[cognitoidentity.swift.package-target-executable]
    ]
    // snippet-end:[cognitoidentity.swift.package-targets]
)
