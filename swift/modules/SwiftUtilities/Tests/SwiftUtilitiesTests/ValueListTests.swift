// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

import XCTest
@testable import SwiftUtilities

final class ValueListTests: XCTestCase {
    let testHeader = "Table of random nonsense"
    let dividerNoContent = "========================"
    let dividerWithContent = "==========================================="

    /// List of each row's title strings.
    let rowTitles = [
        "King of Rock and Roll",
        "Life, the Universe, and everything",
        "Earth is round"
    ]

    /// List of each row's value strings.
    let rowValues = [
        "Elvis",
        "42",
        "Yes"
    ]

    /// Each complete row in text format.
    let textRows = [
        "King of Rock and Roll                 Elvis",
        "Life, the Universe, and everything    42",
        "Earth is round                        Yes"
    ]

    /***** UTILITY FUNCTIONS *****/

    /// Concatenate the specified strings with newlines separating them.
    ///
    /// - Parameter strings: One or more strings to concatenate with newlines
    ///   after them.
    ///
    /// - Returns: A `String` containing each of the specified string values,
    ///   separated by newline characters.
    func concatStrings(_ strings: String...) -> String {
        var output = ""

        for str in strings {
            output += "\(str)\n"
        }
        return output
    }

    /// Concatenate each row in an array of strings, separated by newlines.
    ///
    /// - Parameter array: The array of `String` values to concatenate.
    ///
    /// - Returns: A `String` containing each of the source array's values
    ///   separated by newline characters.
    func concatArrayStrings(_ array: [String]) -> String {
        var output = ""

        for str in array {
            output += "\(str)\n"
        }
        return output
    }

    /***** TESTS *****/

    /// Test that an empty list with no header is correct.
    func testEmpty() {
        let vl = ValueList()
        let output = vl.getFormattedOutput()

        XCTAssertEqual(output.count, 0, "Length of empty value list should be zero")
    }

    /// Test that a list with a header, but no data, is generated correctly.
    func testEmptyWithHeader() {
        let vl = ValueList(header: self.testHeader)
        let expectedOutput = concatStrings(self.testHeader, self.dividerNoContent)

        // Get the formatted output and check it.
        let output = vl.getFormattedOutput()
        XCTAssertEqual(output, expectedOutput, "Empty table with header doesn't match expected string")
    }

    /// Test that a list with content, but no header, is generated correctly.
    func testOutputNoHeader() {
        let vl = ValueList()
        let expectedOutput = concatArrayStrings(self.textRows)

        // Add the content to the ValueList.
        for index in 0...(rowTitles.count - 1) {
            vl.addItem(title: rowTitles[index], value: rowValues[index])
        }

        // Get the formatted output and check it.
        let output = vl.getFormattedOutput()
        XCTAssertEqual(output, expectedOutput, "Table with no header doesn't match expected string")
    }

    /// Test that a list with content and a header is generated correctly.
    func testOutputWithHeader() {
        let vl = ValueList(header: self.testHeader)
        let expectedOutput = concatStrings(self.testHeader, self.dividerWithContent) + concatArrayStrings(self.textRows)

        // Add the content to the ValueList.
        for index in 0...(rowTitles.count - 1) {
            vl.addItem(title: rowTitles[index], value: rowValues[index])
        }

        // Get the formatted output and check it.
        let output = vl.getFormattedOutput()
        XCTAssertEqual(output, expectedOutput, "Table with header doesn't match expected string")
    }
}