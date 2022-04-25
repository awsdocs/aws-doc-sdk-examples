/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cognitoidentity.swift.main-imports]
import Foundation
import CognitoIdentityHandler
// snippet-end:[cognitoidentity.swift.main-imports]

// Instantiate the main identity functions object

// snippet-start:[cognitoidentity.swift.main-struct]
@main
struct FindOrCreateIdentityPool {
    static func main() async {
        
        let identityDemo = await CognitoIdentityHandler()

        // Get the ID of the identity pool, creating it if necesssary

        do {
            guard let poolID = try await identityDemo.getOrCreateIdentityPoolID(name: "SuperSpecialPool") else {
                print("*** Unable to find or create SuperSpecialPool!")
                exit(1)
            }

            print("*** Found or created SuperSpecialPool with ID \(poolID)")
        } catch {
            dump(error, name: "Getting identity pool ID from main program")
        }
    }
}
// snippet-end:[cognitoidentity.swift.main-struct]