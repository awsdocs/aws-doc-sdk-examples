// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

import XCTest
@testable import SwiftUtilities

final class ValueListTests: XCTestCase {
    let testHeader = "Table of random nonsense"
    
    /***** UTILITY FUNCTIONS *****/

    /// Create a test list using the `ValueList` type.
    ///
    /// - Parameter withHeader: Whether or not the list should have a header.
    ///   Default: `false`.
    /// - Returns: A `ValueList` with the test list's contents.
    func createTestList(withHeader: Bool = false) -> ValueList {
        let vl: ValueList
        
        if withHeader == false {
            vl = ValueList()
        } else {
            vl = ValueList(header: self.testHeader)
        }

        vl.addItem(title: "King of Rock and Roll", value: "Elvis")
        vl.addItem(title: "Life, the Universe, and everything", value: 42)
        vl.addItem(title: "Earth is round", value: true)
        return vl
    }

    /// Create a text representation of a list's header.
    ///
    /// - Parameters:
    ///   - itemCount: The number of items the list will hold.
    ///   - withHeader: Whether or not the list should have a header. Default:
    ///     `false`.
    /// - Returns: A `String` with the text of the list's header block.
    func createTestHeaderBlock(itemCount: Int = 0, withHeader: Bool = false) -> String {
        if withHeader == false {
            return ""
        }
        
        var divider = ""
        if itemCount > 0 {
            divider = "==========================================="
        } else {
            divider = "========================"
        }

        return "\(self.testHeader)\n\(divider)\n"
    }

    /// Returns a text representation of the test list's contents.
    ///
    /// - Parameter withHeader: Whether or not the list should have a header.
    ///   Default: `false`.
    /// - Returns: A `String` containing the entire text representation of the
    ///   test list.
    func createExpectedOutput(withHeader: Bool = false) -> String {
        let output = """
        King of Rock and Roll                 Elvis
        Life, the Universe, and everything    42
        Earth is round                        Yes\n
        """

        return "\(self.createTestHeaderBlock(itemCount: 3, withHeader: withHeader))\(output)"
    }

    /***** TESTS *****/

    /// Test that an empty list with no header is correct.
    func testEmpty() {
        let vl = ValueList()
        let output = vl.getFormattedOutput()

        XCTAssertEqual(output.count, 0, "Length of empty value list should be zero")
    }

    /// Test that a list with a header but no data is generated correctly.
    func testEmptyWithHeader() {
        let vl = ValueList(header: self.testHeader)
        let output = vl.getFormattedOutput()

        let expectedOutput = createTestHeaderBlock(withHeader: true)
        XCTAssertEqual(output, expectedOutput, "Empty table with header doesn't match expected string")
    }

    /// Test that a list with content but no header is generated correctly.
    func testOutputNoHeader() {
        let vl = createTestList()
        let expectedOutput = createExpectedOutput()
        let output = vl.getFormattedOutput()

        XCTAssertEqual(output, expectedOutput, "Table with no header doesn't match expected string")
    }

    /// Test that a list with content and a header is generated correctly.
    func testOutputWithHeader() {
        let vl = createTestList(withHeader: true)
        let expectedOutput = createExpectedOutput(withHeader: true)

        let output = vl.getFormattedOutput()
        XCTAssertEqual(output, expectedOutput, "Table with header doesn't match expected string")
    }
}