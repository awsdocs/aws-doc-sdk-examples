/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[cognitoidentity.swift.handler-imports]
import Foundation
import AWSCognitoIdentity
import ClientRuntime
// snippet-end:[cognitoidentity.swift.handler-imports]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class CognitoIdentityHandler {
    let cognitoIdentityClient: CognitoIdentityClient
    
    /// Initialize and return a new ``CognitoIdentityHandler`` object, which is used to drive the AWS calls
    /// used for the example.
    /// - Returns: A new ``CognitoIdentityHandler`` object, ready to run the
    ///   demo code.
    // snippet-start:[cognitoidentity.swift.init]
    public init() async {
        do {
            cognitoIdentityClient = try await CognitoIdentityClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing CognitoIdentityClient"))
            exit(1)
        }
    }
    // snippet-end:[cognitoidentity.swift.init]
    
    /// Returns the ID of the identity pool with the specified name.
    /// - Parameters:
    ///   - name: The name of the identity pool whose ID should be returned
    /// - Returns: A string containing the ID of the specified identity pool or `nil` on error or if not found
    ///
    // snippet-start:[cognitoidentity.swift.get-pool-id]
    func getIdentityPoolID(name: String) async throws -> String? {
        var token: String? = nil
        
        // Iterate over the identity pools until a match is found.
        repeat {
            /// `token` is a value returned by `ListIdentityPools()` if the returned list
            /// of identity pools is only a partial list. You use the `token` to tell Cognito that
            /// you want to continue where you left off previously; specifying `nil` or not providing
            /// it means "start at the beginning."
            
            let listPoolsInput = ListIdentityPoolsInput(maxResults: 25, nextToken: token)
            
            /// Read pages of identity pools from Cognito until one is found
            /// whose name matches the one specified in the `name` parameter.
            /// Return the matching pool's ID. Each time we ask for the next
            /// page of identity pools, we pass in the token given by the
            /// previous page.
            
            do {
                let output = try await cognitoIdentityClient.listIdentityPools(input: listPoolsInput)

                if let identityPools = output.identityPools {
                    for pool in identityPools {
                        if pool.identityPoolName == name {
                            return pool.identityPoolId!
                        }
                    }
                }
                
                token = output.nextToken
            } catch {
                print("ERROR: ", dump(error, name: "Trying to get list of identity pools"))
            }
        } while token != nil
        
        return nil
    }
    // snippet-end:[cognitoidentity.swift.get-pool-id]
    
    /// Returns the ID of the identity pool with the specified name.
    /// - Parameters:
    ///   - name: The name of the identity pool whose ID should be returned
    /// - Returns: A string containing the ID of the specified identity pool or `nil` on error or if not found
    ///
    // snippet-start:[cognitoidentity.swift.get-or-create-pool-id]
    public func getOrCreateIdentityPoolID(name: String) async throws -> String? {
        // See if the pool already exists
        
        do {
            guard let poolId = try await self.getIdentityPoolID(name: name) else {
                let poolId = try await self.createIdentityPool(name: name)
                return poolId
            }
      
            return poolId
           
        } catch {
            print("ERROR: ", dump(error, name: "Trying to get the identity pool ID"))
            return nil
        }
    }
    // snippet-end:[cognitoidentity.swift.get-or-create-pool-id]
    
    /// Create a new identity pool, returning its ID.
    /// - Parameters:
    ///     - name: The name to give the new identity pool
    /// - Returns: A string containing the newly created pool's ID, or `nil` if an error occurred
    ///
    // snippet-start:[cognitoidentity.swift.create-identity-pool]
    func createIdentityPool(name: String) async throws -> String? {
        let cognitoInputCall = CreateIdentityPoolInput(developerProviderName: "com.exampleco.CognitoIdentityDemo",
                                                       identityPoolName: name)

        do {
            let result = try await cognitoIdentityClient.createIdentityPool(input: cognitoInputCall)
            guard let poolId = result.identityPoolId  else {
                return nil
            }
            
            return poolId
            
        } catch {
            print("ERROR: ", dump(error, name: "Error attempting to create the identity pool"))
        }
        
        return nil
    }
    // snippet-end:[cognitoidentity.swift.create-identity-pool]
}
