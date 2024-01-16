// swift-tools-version: 5.5
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// The swift-tools-version declares the minimum version of Swift required to
// build this package.

import PackageDescription

let package = Package(
    name: "ListBuckets-Simple",
    // snippet-start:[s3.swift.intro.package-dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.17.0"
        )
    ],
    // snippet-end:[s3.swift.intro.package-dependencies]
    // snippet-start:[s3.swift.intro.package-targets]
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .executableTarget(
            name: "ListBuckets-Simple",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift")
            ],
            path: "Sources")
    ]
    // snippet-end:[s3.swift.intro.package-targets]
)
