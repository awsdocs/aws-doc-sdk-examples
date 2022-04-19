// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to
// build this package.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import PackageDescription
let package = Package(
// snippet-start:[s3.swift.basics.package.attributes]
    name: "s3-basics",
    platforms: [
        .macOS(.v11),
        .iOS(.v13)
    ],
// snippet-end:[s3.swift.basics.package.attributes]
// snippet-start:[s3.swift.basics.package.dependencies]
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        .package(
            name: "AWSSwiftSDK",
            url: "https://github.com/awslabs/aws-sdk-swift",
            from: "0.2.2"
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            .branch("main")
        ),
        .package(
            name: "Fakery",
            url: "https://github.com/vadymmarkov/Fakery",
            from: "5.0.0"
        ),
    ],
// snippet-end:[s3.swift.basics.package.dependencies]
// snippet-start:[s3.swift.basics.package.targets]
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
// snippet-start:[s3.swift.basics.package.target.executable]
        .executableTarget(
            name: "s3-basics",
            dependencies: [
                "ServiceHandler",
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ],
            path: "./Sources/basics"
        ),
// snippet-end:[s3.swift.basics.package.target.executable]
// snippet-start:[s3.swift.basics.package.target.handler]
        .target(
            name: "ServiceHandler",
            dependencies: [
                .product(name: "AWSS3", package: "AWSSwiftSDK"),
            ],
            path: "./Sources/ServiceHandler"
        ),
// snippet-end:[s3.swift.basics.package.target.handler]
// snippet-start:[s3.swift.basics.package.target.tests]
        .testTarget(
            name: "basics-tests",
            dependencies: [
                "s3-basics",
                "Fakery"
            ],
            path: "./Tests/basics-tests"
        )
// snippet-end:[s3.swift.basics.package.target.tests]
    ]
// snippet-end:[s3.swift.basics.package.targets]
)
