// swift-tools-version:5.6
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import PackageDescription

let package = Package(
// snippet-start:[ddb.swift.listtables.package.attributes]
    name: "listtables",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[ddb.swift.listtables.package.attributes]
// snippet-start:[ddb.swift.listtables.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.28.0"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            branch: "main"
        ),
        .package(
            name: "SwiftUtilities",
            path: "../../../modules/SwiftUtilities"
        ),
    ],
// snippet-end:[ddb.swift.listtables.package.dependencies]
// snippet-start:[ddb.swift.listtables.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[ddb.swift.listtables.package.target.executable]
        .executableTarget(
            name: "listtables",
            dependencies: [
                .product(name: "AWSDynamoDB", package: "aws-sdk-swift"),
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources"
         ),
// snippet-end:[ddb.swift.listtables.package.target.executable]
// snippet-start:[ddb.swift.listtables.package.target.tests]
        .testTarget(
            name: "listtables-tests",
            dependencies: [
                .product(name: "AWSDynamoDB", package: "aws-sdk-swift"),
                "listtables",
                "SwiftUtilities"
            ],
            path: "./Tests"
        )
// snippet-end:[ddb.swift.listtables.package.target.tests]
    ]
// snippet-end:[ddb.swift.listtables.package.targets]
)
