/*
   Convenience extensions for the standard Swift `String` class.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation

public extension String {
    /// Returns a unique file name for use in testing. The file name is a UUID.
    /// You can optionally add a period and an extension to it. If `inValid` is
    /// true, the returned name isn't valid for use with Amazon Simple Storage
    /// Service (Amazon S3).
    ///
    /// - Parameters:
    ///   - prefix: String - A prefix to add to the name. If empty, none is
    ///     added.
    ///   - ext: String - The file extension to add. If empty, no extension is
    ///     added.
    ///   - isValid: Bool - If true, the returned filename is invalid for Amazon
    ///     S3 use.
    /// - Returns: A string containing a unique filename for Amazon S3 testing.
    static func uniqueName(withPrefix prefix: String = "",
                           withExtension ext: String = "",
                           isValid: Bool = true) -> String {
        var name = UUID().uuidString

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