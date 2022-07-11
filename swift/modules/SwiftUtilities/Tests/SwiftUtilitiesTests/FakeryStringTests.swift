/*
   Tests for the Fakery extension to the String class.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import XCTest
@testable import SwiftUtilities

final class FakeryStringTests: XCTestCase {
    let defaultNumParagraphs = 5

    /// Get the number of words in the specified string.
    /// - Parameter str: The string to count words in.
    /// - Returns: An integer value indicating the word count.
    private func countWords(inString str: String) -> Int {
        let components = str.components(separatedBy: .whitespacesAndNewlines)
        let words = components.filter { !$0.isEmpty }
        return words.count
    }

    /// Count the sentences in the specified string.
    /// - Parameter str: The string to count sentences in.
    /// - Returns: An integer indicating the sentence count.
    private func countSentences(inString str: String) -> Int {
        let cset = CharacterSet(charactersIn: ". \n")
        let components = str.components(separatedBy: cset)
        let sentences = components.filter { !$0.isEmpty }
        return sentences.count
    }

    /// Count the paragraphs in the string given.
    /// - Parameter str: The string to count paragraphs in.
    /// - Returns: An integer giving the string's paragraph count.
    private func countParagraphs(inString str: String) -> Int {
        let components = str.components(separatedBy: "\n")
        let paragraphs = components.filter { !$0.isEmpty }
        return paragraphs.count
    }

    /// Confirm that creating a Lorem Ipsum string results in the
    /// expected number of paragraphs by default.
    func testStringWithLoremText() {
        let ts = String.withLoremText()

        XCTAssertTrue(self.countParagraphs(inString: ts) == defaultNumParagraphs, "Asking for LoremIpsum string with no paragraph count should get \(defaultNumParagraphs) paragraphs")
    }

    /// Ensure that asking for a three-paragraph string works.
    func testStringWithLoremTextThreeParagraphs() {
        let ts = String.withLoremText(paragraphs: 3)

        XCTAssertTrue(self.countParagraphs(inString: ts) == 3, "Asking for 3 paragraphs should return three paragraphs")
    }

    /// Ensure that specifying an invalid number of paragraphs, such as 0
    /// or a negative value, returns an empty string as expected.
    func testStringWithLoremTextInvalidNumberOfParagraphs() {
        let ts1 = String.withLoremText(paragraphs: 0)

        XCTAssertTrue(ts1.count == 0, "String of zero paragraphs should be empty")

        let ts2 = String.withLoremText(paragraphs: -3)
        XCTAssertTrue(ts2.count == 0, "String with negative number of paragraphs should be empty")
    }
}