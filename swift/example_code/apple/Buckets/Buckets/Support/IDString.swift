// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import Foundation
import SwiftUI

/// An identifiable string type that associates a UUID with a string.
/// This allows the string to be uniquely identified even if there are
/// multiple strings with the same value.
///
/// This is particularly useful when adding items to a SwiftUI `List`.
public struct IDString: Identifiable {
    /// The string's unique ID.
    public let id = UUID()
    /// The value of the string.
    let text: String
    
    /// Initialize the new string with the specified text. The ID is
    /// generated automatically.
    /// - Parameter text: The text to assign to the new string.
    init(_ text: String) {
        self.text = text
    }
}
