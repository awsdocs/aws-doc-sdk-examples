// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription

let package = Package(
// snippet-start:[iam.swift.basics.package.attributes]
    name: "basics",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[iam.swift.basics.package.attributes]
// snippet-start:[iam.swift.basics.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.3.0"
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
// snippet-end:[iam.swift.basics.package.dependencies]
// snippet-start:[iam.swift.basics.package.targets]
    targets: [
        // A target defines a module or a test suite. A target can depend on
        // other targets in this package. They can also depend on products in
        // other packages that this package depends on.
// snippet-start:[iam.swift.basics.package.target.executable]
        .executableTarget(
            name: "basics",
            dependencies: [
                "ServiceHandler",
                "SwiftUtilities",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/Basics",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work.
            ]
        ),
// snippet-end:[iam.swift.basics.package.target.executable]
// snippet-start:[iam.swift.basics.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSIAM", package: "aws-sdk-swift"),
                .product(name: "AWSS3", package: "aws-sdk-swift"),
                .product(name: "AWSSTS", package: "aws-sdk-swift"),
                "SwiftUtilities",
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[iam.swift.basics.package.target.handler]
// snippet-start:[iam.swift.basics.package.target.tests]
        .testTarget(
            name: "basics-tests",
            dependencies: [
                "basics",
                "SwiftUtilities"
            ],
            path: "./Tests/BasicsTests",
            linkerSettings: [
                .linkedLibrary("rt")    // Include librt for Dispatch to work.
            ]
        )
// snippet-end:[iam.swift.basics.package.target.tests]
    ]
// snippet-end:[iam.swift.basics.package.targets]
)
