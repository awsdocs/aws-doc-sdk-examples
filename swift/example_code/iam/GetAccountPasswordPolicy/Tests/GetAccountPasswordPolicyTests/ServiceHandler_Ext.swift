/*
   Extensions to the `ServiceHandler` class to handle tasks we need
   for testing that aren't the purpose of this example.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import AWSIAM
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

@testable import ServiceHandler

public extension ServiceHandler {

    func updateAccountPasswordPolicy(allowPasswordChange: Bool = false,
                                     minLength: Int = 8,
                                     passwordReuseDistance: Bool = false,
                                     requireLettersLower: Bool = false,
                                     requireLettersUpper: Bool = false,
                                     requireSymbols: Bool = false,
                                     hardExpiration: Bool = false,
                                     maxAge: Int = 0) async throws {
         let input = UpdateAccountPasswordInput(
            allowUsersToChangePassword: allowPasswordChange,
            hardExpiry: hardExpiration,
            maxPasswordAge: maxAge,
            minimumPasswordLength: minLength,
            passwordReusePrevention: passwordReuseDistance,
            requireLowercaseCharacters: requireLettersLower,
            requireUppercaseCharacters: requireLettersUpper
         )
         do {
            _ = try await client.updateAccountPasswordPolicy(input: input)
         } catch {
            throw error
         }
    }
}