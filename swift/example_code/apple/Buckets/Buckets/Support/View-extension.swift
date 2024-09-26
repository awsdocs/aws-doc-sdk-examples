// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import Foundation
import SwiftUI

/// Extends the SwiftUI `View` class to present error alerts if an error
/// has occurred.
extension View {
    /// Presents an error alert box if one has occurred. An error is considered
    /// to have occurred if the `View` has an `error` variable which
    /// isn't `nil`.
    ///
    /// - Parameters:
    ///   - error: A binding for the error that occurred, or `nil` if no error
    ///     has occurred.
    ///   - buttonTitle: The title to use for the button that dismisses
    ///     the alert.
    func errorAlert(error: Binding<Error?>, buttonTitle: String = "Continue") -> some View {
        let localizedAlertError = LocalizedAlertError(error: error.wrappedValue)
        
        return alert(isPresented: .constant(localizedAlertError != nil), error: localizedAlertError) { _ in
            Button(buttonTitle) {
                error.wrappedValue = nil
            }
        } message: { error in
            Text(error.recoverySuggestion ?? "")
        }
    }
}

