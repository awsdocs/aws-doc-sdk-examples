// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.listpolicies.package.attributes]
    name: "listpolicies",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.listpolicies.package.attributes]
// snippet-start:[iam.swift.listpolicies.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            name: "AWSSwiftSDK",
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.2.5"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            .branch("main")
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
// snippet-end:[iam.swift.listpolicies.package.dependencies]
// snippet-start:[iam.swift.listpolicies.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.listpolicies.package.target.executable]
        .executableTarget(
            name: "listpolicies",
            dependencies: [
                "ServiceHandler",
                "SwiftyTextTable",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/ListPolicies"
        ),
// snippet-end:[iam.swift.listpolicies.package.target.executable]
// snippet-start:[iam.swift.listpolicies.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "AWSSwiftSDK"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.listpolicies.package.target.handler]
// snippet-start:[iam.swift.listpolicies.package.target.tests]
        .testTarget(
            name: "listpolicies-tests",
            dependencies: [
                "listpolicies",
                "SwiftUtilities",
                "SwiftyTextTable",
            ],
            path: "./Tests/ListPoliciesTests"
        )
// snippet-end:[iam.swift.listpolicies.package.target.tests]
    ]
// snippet-end:[iam.swift.listpolicies.package.targets]
)
