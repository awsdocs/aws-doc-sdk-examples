// swift-tools-version:5.9
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// The swift-tools-version declares the minimum version of Swift required to
// build this package.


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.listusers.package.attributes]
    name: "listusers",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.listusers.package.attributes]
// snippet-start:[iam.swift.listusers.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.20.0"
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
// snippet-end:[iam.swift.listusers.package.dependencies]
// snippet-start:[iam.swift.listusers.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.listusers.package.target.executable]
        .executableTarget(
            name: "listusers",
            dependencies: [
                "ServiceHandler",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/ListUsers"
        ),
// snippet-end:[iam.swift.listusers.package.target.executable]
// snippet-start:[iam.swift.listusers.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "aws-sdk-swift"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.listusers.package.target.handler]
// snippet-start:[iam.swift.listusers.package.target.tests]
        .testTarget(
            name: "listusers-tests",
            dependencies: [
                "listusers",
                "SwiftUtilities"
            ],
            path: "./Tests/ListUsersTests"
        )
// snippet-end:[iam.swift.listusers.package.target.tests]
    ]
// snippet-end:[iam.swift.listusers.package.targets]
)
