// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import PackageDescription

let package = Package(
    name: "mocking",
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.28.0"
        )
    ],
    // snippet-start:[mocking.swift.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
        .executableTarget(
            name: "mocking",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift"),
            ],
            path: "./Sources"
        ),
        // snippet-start:[mocking.swift.package.testTarget]
        .testTarget(
            name: "mocking-tests",
            dependencies: [
                .product(name: "AWSS3", package: "aws-sdk-swift"),
                "mocking"
            ],
            path: "./Tests"
        )
        // snippet-end:[mocking.swift.package.testTarget]
    ]
    // snippet-end:[mocking.swift.package.targets]
)
