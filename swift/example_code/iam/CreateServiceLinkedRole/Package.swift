// swift-tools-version:5.6
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.createservicelinkedrole.package.attributes]
    name: "createservicelinkedrole",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.createservicelinkedrole.package.attributes]
// snippet-start:[iam.swift.createservicelinkedrole.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.3.0"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            from: "1.1.0"
        ),
        .package(
            name: "SwiftUtilities",
            path: "../../../modules/SwiftUtilities"
        ),
    ],
// snippet-end:[iam.swift.createservicelinkedrole.package.dependencies]
// snippet-start:[iam.swift.createservicelinkedrole.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.createservicelinkedrole.package.target.executable]
        .executableTarget(
            name: "createservicelinkedrole",
            dependencies: [
                "ServiceHandler",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/CreateServiceLinkedRole",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work
            ]
        ),
// snippet-end:[iam.swift.createservicelinkedrole.package.target.executable]
// snippet-start:[iam.swift.createservicelinkedrole.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "aws-sdk-swift"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.createservicelinkedrole.package.target.handler]
// snippet-start:[iam.swift.createservicelinkedrole.package.target.tests]
        .testTarget(
            name: "createservicelinkedrole-tests",
            dependencies: [
                "createservicelinkedrole",
                "SwiftUtilities"
            ],
            path: "./Tests/CreateServiceLinkedRoleTests",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work
            ]
        )
// snippet-end:[iam.swift.createservicelinkedrole.package.target.tests]
    ]
// snippet-end:[iam.swift.createservicelinkedrole.package.targets]
)
