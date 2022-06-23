/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import Fakery

public extension String {
    /// Return a new randomly-generated LoremIpsum-style string
    /// with the specified number of paragraphs.
    /// - Parameter paragraphs: An integer specifying the number
    ///   of paragraphs to return. The default is 5.
    /// - Returns: A `String` containing the specified number of
    ///   random Lorem Ipsum paragraphs. Each paragraph contains
    ///   between 4 and 8 sentences, each of between 3 and 9
    ///   words.
    static func withLoremText(paragraphs: Int = 5) -> String {
        var text = ""

        if paragraphs > 0 {
            let faker = Faker(locale: "en-US")

            // Instead of asking Fakery for a given number of paragraphs,
            // generate them ourselves, so we can specify their lengths.

            for i in 1...paragraphs {
                if i != 1 {
                    text += "\n"
                } 
                let sentenceCount = Int.random(in: 4...8)
                for n in 1...sentenceCount {
                    if n != 1 {
                        text += " "
                    }
                    text += faker.lorem.sentence(
                            wordsAmount: Int.random(in: 3...9))
                }
            }
        }
        return text
    }
}