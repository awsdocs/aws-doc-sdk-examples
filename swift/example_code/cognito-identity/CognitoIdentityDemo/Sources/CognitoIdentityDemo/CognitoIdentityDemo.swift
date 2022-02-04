/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation
import CognitoIdentityHandler

// Instantiate the main identity functions object

@main
struct CognitoIdentityDemo {
    static func main() async {
        
        let identityDemo = CognitoIdentityHandler()

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
