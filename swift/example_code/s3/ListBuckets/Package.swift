// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import PackageDescription

let package = Package(
    // snippet-start:[s3.swift.listbuckets.name-platforms]
    name: "listbuckets",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
    // snippet-end:[s3.swift.listbuckets.name-platforms]
    // snippet-start:[s3.swift.listbuckets.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.28.0"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            .branch("main")
        )
    ],
    // snippet-end:[s3.swift.listbuckets.dependencies]
    // snippet-start:[s3.swift.listbuckets.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
        // snippet-start:[s3.swift.listbuckets.targets-executable]
        .executableTarget(
            name: "listbuckets",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift"),
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/ListBuckets"
        ),
        // snippet-end:[s3.swift.listbuckets.targets-executable]
        // snippet-start:[s3.swift.listbuckets.targets-test]
        .testTarget(
            name: "listbuckets-tests",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift"),
                "listbuckets"
            ],
            path: "./Tests/ListBucketsTests"
        )
        // snippet-end:[s3.swift.listbuckets.targets-test]
    ]
    // snippet-end:[s3.swift.listbuckets.targets]
)
