// swift-tools-version:5.9
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// The swift-tools-version declares the minimum version of Swift required to
// build this package.

import PackageDescription

let package = Package(
    name: "Waiters",
    dependencies: [
        // This project requires the AWS SDK for Swift.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "1.0.0"
        ),
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a
        // module or a test suite. Targets can depend on other targets in this
        // package and products from dependencies.
        .executableTarget(
            name: "Waiters",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift"),
            ],
            path: "Sources"
        ),
        .testTarget(
            name: "Waiters-Tests",
            dependencies: [
                "Waiters",
                .product(name: "AWSS3", package: "aws-sdk-swift")
            ],
            path: "Tests"
        )
    ]
)
