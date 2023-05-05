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
    
    // snippet-start:[cognitoidentity.swift.init]
    /// Initialize and return a new ``CognitoIdentityHandler`` object,
    /// which is used to drive the AWS calls used for the example.
    ///
    /// - Returns: A new ``CognitoIdentityHandler`` object, ready to run the
    ///   demo code.
    public init() async throws {
        cognitoIdentityClient = try await CognitoIdentityClient()
    }
    // snippet-end:[cognitoidentity.swift.init]
    
    // snippet-start:[cognitoidentity.swift.get-pool-id]
    /// Return the ID of the identity pool with the specified name.
    ///
    /// - Parameters:
    ///   - name: The name of the identity pool whose ID should be returned.
    ///
    /// - Returns: A string containing the ID of the specified identity pool
    ///   or `nil` on error or if not found.
    ///
    func getIdentityPoolID(name: String) async throws -> String? {
        var token: String? = nil
        
        // Iterate over the identity pools until a match is found.

        repeat {
            /// `token` is a value returned by `ListIdentityPools()` if the
            /// returned list of identity pools is only a partial list. You
            /// use the `token` to tell Amazon Cognito that you want to
            /// continue where you left off previously. If you specify `nil`
            /// or you don't provide the token, Amazon Cognito will start at
            /// the beginning.
            
            let listPoolsInput = ListIdentityPoolsInput(maxResults: 25, nextToken: token)
            
            /// Read pages of identity pools from Cognito until one is found
            /// whose name matches the one specified in the `name` parameter.
            /// Return the matching pool's ID. Each time we ask for the next
            /// page of identity pools, we pass in the token given by the
            /// previous page.
            
            let output = try await cognitoIdentityClient.listIdentityPools(input: listPoolsInput)

            if let identityPools = output.identityPools {
                for pool in identityPools {
                    if pool.identityPoolName == name {
                        return pool.identityPoolId!
                    }
                }
            }
            
            token = output.nextToken
        } while token != nil
        
        return nil
    }
    // snippet-end:[cognitoidentity.swift.get-pool-id]
    
    // snippet-start:[cognitoidentity.swift.get-or-create-pool-id] 
    /// Return the ID of the identity pool with the specified name.
    ///
    /// - Parameters:
    ///   - name: The name of the identity pool whose ID should be returned
    ///
    /// - Returns: A string containing the ID of the specified identity pool.
    ///   Returns `nil` if there's an error or if the pool isn't found.
    ///
    public func getOrCreateIdentityPoolID(name: String) async throws -> String? {
        // See if the pool already exists. If it doesn't, create it.
        
        guard let poolId = try await self.getIdentityPoolID(name: name) else {
            return try await self.createIdentityPool(name: name)
        }
    
        return poolId
    }
    // snippet-end:[cognitoidentity.swift.get-or-create-pool-id]
    
    // snippet-start:[cognitoidentity.swift.create-identity-pool]
    /// Create a new identity pool and return its ID.
    ///
    /// - Parameters:
    ///     - name: The name to give the new identity pool.
    ///
    /// - Returns: A string containing the newly created pool's ID, or `nil`
    ///   if an error occurred.
    ///
    func createIdentityPool(name: String) async throws -> String? {
        let cognitoInputCall = CreateIdentityPoolInput(developerProviderName: "com.exampleco.CognitoIdentityDemo",
                                                       identityPoolName: name)

        let result = try await cognitoIdentityClient.createIdentityPool(input: cognitoInputCall)
        guard let poolId = result.identityPoolId else {
            return nil
        }
        
        return poolId
    }
    // snippet-end:[cognitoidentity.swift.create-identity-pool]

    // snippet-start:[cognitoidentity.swift.delete-identity-pool]
    /// Delete the specified identity pool.
    ///
    /// - Parameters:
    ///   - id: The ID of the identity pool to delete.
    ///
    func deleteIdentityPool(id: String) async throws {
        let input = DeleteIdentityPoolInput(
            identityPoolId: id
        )

        _ = try await cognitoIdentityClient.deleteIdentityPool(input: input)
    }
    // snippet-end:[cognitoidentity.swift.delete-identity-pool]
}
