/*
   Convenience extensions for the standard Swift `String` class.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation

public extension String {
    /// Returns a unique file name for use in testing. The file name is a
    /// UUID. You can optionally add a period and an extension to it. If
    /// `isValid` is false, the returned name isn't valid for use with Amazon
    /// Simple Storage Service (Amazon S3).
    ///
    /// - Parameters:
    ///   - prefix: String - A prefix to add to the name. If empty, none is
    ///     added.
    ///   - maxDigits: Int - The maximum number of random characters to use.
    ///     If this value is 0 or exceeds the UUID length, the entire UUID is
    ///     used.
    ///   - ext: String - The file extension to add. If empty, no extension is
    ///     added.
    ///   - isValid: Bool - If true, the returned filename is valid for Amazon
    ///     S3 use.
    /// - Returns: A string containing a unique filename for Amazon S3
    ///   testing.
    static func uniqueName(withPrefix prefix: String = "",
                           maxDigits: Int = 0,
                           withExtension ext: String = "",
                           isValid: Bool = true) -> String {
        var name = UUID().uuidString

        if maxDigits != 0 {
            if maxDigits < name.count {
                name = String(name.prefix(maxDigits))
            }
        }

        if prefix != "" {
            name = "\(prefix)-\(name)"
        }
        if ext != "" {
            name += ".\(ext)"
        }

        if isValid {
            return name.lowercased()
        } else {
            return ",12%\(name)"
        }
    }
}