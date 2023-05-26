// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import PackageDescription

let package = Package(
    name: "listbuckets",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.17.0"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            .branch("main")
        ),
        .package(
            name: "SwiftUtilities",
            path: "../../../modules/SwiftUtilities"
        ),
    ],
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
        .executableTarget(
            name: "listbuckets",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift"),
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources"
        ),
        .testTarget(
            name: "listbuckets-tests",
            dependencies: [
                "listbuckets",
                "SwiftUtilities"
            ],
            path: "./Tests"
        )
    ]
)
