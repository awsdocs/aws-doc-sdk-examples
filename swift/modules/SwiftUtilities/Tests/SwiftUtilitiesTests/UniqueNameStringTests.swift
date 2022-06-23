/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import XCTest
@testable import SwiftUtilities

final class UniqueNameStringTests: XCTestCase {
    /// Test to ensure that calling uniqueName() with no
    /// inputs simply returns a lowercased UUID.
    func testSimpleUniqueName() {
        let ts = String.uniqueName()
        XCTAssertTrue(ts.lowercased() == ts)

        let tsUUID = UUID(uuidString: String.uniqueName())
        XCTAssertNotNil(tsUUID)
    }

    /// Ensure that the `withPrefix` property works by
    /// checking that the returned value:
    ///
    /// * Is non-nil
    /// * Starts with the specified prefix
    /// * That removing the prefix results in a valid lowercased
    ///   UUID
    func testUniqueNamePrefixed() {
        let prefix = "prefix"
        let ts = String.uniqueName(withPrefix: prefix)
        XCTAssertNotNil(ts)

        XCTAssertTrue(ts.hasPrefix("\(prefix)-"))

        let lcUUID = ts.dropFirst(prefix.count+1)
        XCTAssertTrue(lcUUID.lowercased() == lcUUID)

        XCTAssertNotNil(UUID(uuidString: lcUUID.uppercased()))
    }

    /// Ensure that the `withExtension` property works by
    /// checking that the returned value:
    ///
    /// * Is non-nil
    /// * Ends with a period followed by the extension
    /// * That removing the extension (and period) results in 
    ///   a valid lowercased UUID
    func testUniqueNameWithExtension() {
        let ext = "txt"
        let ts = String.uniqueName(withExtension: ext)
        XCTAssertNotNil(ts)

        XCTAssertTrue(ts.hasSuffix(".\(ext)"))

        let lcUUID = ts.dropLast(ext.count+1)
        XCTAssertTrue(lcUUID.lowercased() == lcUUID)

        XCTAssertNotNil(UUID(uuidString: lcUUID.uppercased()))
    }

    /// Ensure that the `isValid` property works by
    /// checking that when false, the returned value:
    ///
    /// * Is non-nil
    /// * Starts with the prefix that invalidates the name
    /// * That removing the prefix results in a valid
    ///   lowercased UUID
    func testInvalidUniqueName() {
        let invalidatePrefix = ",12%"
        let ts = String.uniqueName(isValid: false)
        XCTAssertNotNil(ts)

        XCTAssertTrue(ts.hasPrefix(invalidatePrefix))

        let uuidStr = String(ts.dropFirst(invalidatePrefix.count))
        XCTAssertTrue(uuidStr.uppercased() == uuidStr)

        XCTAssertNotNil(UUID(uuidString: uuidStr))
    }
}
