// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.listrolepolicies.package.attributes]
    name: "listrolepolicies",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.listrolepolicies.package.attributes]
// snippet-start:[iam.swift.listrolepolicies.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            name: "AWSSwiftSDK",
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.2.5"
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
// snippet-end:[iam.swift.listrolepolicies.package.dependencies]
// snippet-start:[iam.swift.listrolepolicies.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.listrolepolicies.package.target.executable]
        .executableTarget(
            name: "listrolepolicies",
            dependencies: [
                "ServiceHandler",
                "SwiftyTextTable",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/ListRolePolicies"
        ),
// snippet-end:[iam.swift.listrolepolicies.package.target.executable]
// snippet-start:[iam.swift.listrolepolicies.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "AWSSwiftSDK"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.listrolepolicies.package.target.handler]
// snippet-start:[iam.swift.listrolepolicies.package.target.tests]
        .testTarget(
            name: "listrolepolicies-tests",
            dependencies: [
                "listrolepolicies",
                "SwiftUtilities",
                "SwiftyTextTable",
            ],
            path: "./Tests/ListRolePoliciesTests"
        )
// snippet-end:[iam.swift.listrolepolicies.package.target.tests]
    ]
// snippet-end:[iam.swift.listrolepolicies.package.targets]
)
