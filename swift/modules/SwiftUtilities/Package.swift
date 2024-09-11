// swift-tools-version: 5.6
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "SwiftUtilities",
    products: [
        // Products define the executables and libraries a package produces.
        // They also make their own executables and libraries visible to other
        // packages.
        .library(
            name: "SwiftUtilities",
            targets: ["SwiftUtilities"]),
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        // .package(url: /* package url */, from: "1.0.0"),
        .package(
            url: "https://github.com/vadymmarkov/Fakery",
            from: "5.0.0"
        ),
    ],
    targets: [
        // Targets can depend on other targets in this package. They can also
        // depend on products in packages this package depends on.
        .target(
            name: "SwiftUtilities",
            dependencies: [
                "Fakery"
            ]
        ),
        .testTarget(
            name: "SwiftUtilitiesTests",
            dependencies: [
                "SwiftUtilities",
                "Fakery"
            ]
        ),
    ]
)
