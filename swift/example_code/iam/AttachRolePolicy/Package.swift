// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.attachrolepolicy.package.attributes]
    name: "attachrolepolicy",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.attachrolepolicy.package.attributes]
// snippet-start:[iam.swift.attachrolepolicy.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.3.0"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            from: "1.1.4"
        ),
        .package(
            name: "SwiftUtilities",
            path: "../../../modules/SwiftUtilities"
        ),
        .package(
            url: "https://github.com/scottrhoyt/SwiftyTextTable.git",
            from: "0.9.0"
        ),
    ],
// snippet-end:[iam.swift.attachrolepolicy.package.dependencies]
// snippet-start:[iam.swift.attachrolepolicy.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.attachrolepolicy.package.target.executable]
        .executableTarget(
            name: "attachrolepolicy",
            dependencies: [
                "ServiceHandler",
                "SwiftyTextTable",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/AttachRolePolicy",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work.
            ]
        ),
// snippet-end:[iam.swift.attachrolepolicy.package.target.executable]
// snippet-start:[iam.swift.attachrolepolicy.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "aws-sdk-swift"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.attachrolepolicy.package.target.handler]
// snippet-start:[iam.swift.attachrolepolicy.package.target.tests]
        .testTarget(
            name: "attachrolepolicy-tests",
            dependencies: [
                "attachrolepolicy",
                "SwiftUtilities",
                "SwiftyTextTable",
            ],
            path: "./Tests/AttachRolePolicyTests",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work.
            ]
        )
// snippet-end:[iam.swift.attachrolepolicy.package.target.tests]
    ]
// snippet-end:[iam.swift.attachrolepolicy.package.targets]
)
