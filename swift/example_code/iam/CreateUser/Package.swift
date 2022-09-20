// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.createuser.package.attributes]
    name: "createuser",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.createuser.package.attributes]
// snippet-start:[iam.swift.createuser.package.dependencies]
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
    ],
// snippet-end:[iam.swift.createuser.package.dependencies]
// snippet-start:[iam.swift.createuser.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.createuser.package.target.executable]
        .executableTarget(
            name: "createuser",
            dependencies: [
                "ServiceHandler",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/CreateUser"
        ),
// snippet-end:[iam.swift.createuser.package.target.executable]
// snippet-start:[iam.swift.createuser.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "AWSSwiftSDK"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.createuser.package.target.handler]
// snippet-start:[iam.swift.createuser.package.target.tests]
        .testTarget(
            name: "createuser-tests",
            dependencies: [
                "createuser",
                "SwiftUtilities"
            ],
            path: "./Tests/CreateUserTests",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work
            ]
        )
// snippet-end:[iam.swift.createuser.package.target.tests]
    ]
// snippet-end:[iam.swift.createuser.package.targets]
)
