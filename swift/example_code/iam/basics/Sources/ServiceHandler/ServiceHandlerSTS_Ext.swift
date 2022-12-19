/*
   Extensions to the `ServiceHandlerSTS` class to handle tasks we need
   for testing that aren't the purpose of this example.

   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import AWSSTS
import AWSIAM
import AWSClientRuntime
import ClientRuntime
import SwiftUtilities

public extension ServiceHandlerSTS {
    func getAccessKeyAccountNumber(key: IAMClientTypes.AccessKey) async throws -> String {
        let input = GetAccessKeyInfoInput(
            accessKeyId: key.accessKeyId
        )
        do {
            let output = try await stsClient.getAccessKeyInfo(input: input)

            guard let account = output.account else {
                throw ServiceHandlerError.keyError
            }
            return account
        }
    }
}