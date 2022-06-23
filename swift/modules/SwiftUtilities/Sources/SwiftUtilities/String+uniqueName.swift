/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation

public extension String {
    /// Returns a unique filename for use in testing. The filename
    /// is just a UUID, optionally with a period and an extension
    /// added. The returned name is invalid for S3 use if isValid is
    /// true.
    ///
    /// - Parameters:
    ///   - prefix: String - A prefix to add to the name. If empty, none is added.
    ///   - ext: String - The file extension to add. If empty, no extension is added.
    ///   - isValid: Bool - If true, the returned filename is invalid for S3 use.
    /// - Returns: A string containing a unique filename for S3 testing.
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