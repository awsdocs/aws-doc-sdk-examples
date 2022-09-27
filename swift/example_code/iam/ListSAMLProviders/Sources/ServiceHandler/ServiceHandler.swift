/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.listsamlproviders.handler]
// snippet-start:[iam.swift.listsamlproviders.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.listsamlproviders.handler.imports]

/// Errors returned by `ServiceHandler` functions.
enum ServiceHandlerError: Error {
    case noSuchGroup
    case noSuchRole
    case noSuchPolicy
    case noSuchUser
}

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IamClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example. The region string
    /// `AWS_GLOBAL` is used because users are shared across all regions.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.listsamlproviders.handler.init]
    public init() async {
        do {
            client = try IamClient(region: "AWS_GLOBAL")
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.listsamlproviders.handler.init]

    /// Returns a list of the IAM account's SAML policies.
    ///
    /// - Returns: An array of `IamClientTypes.SAMLProviderListEntry` objects,
    ///   each describing one entry in the SAML provider list.
    // snippet-start:[iam.swift.listsamlproviders.handler.listsamlproviders]
    public func listSAMLProviders() async throws -> [IamClientTypes.SAMLProviderListEntry] {
        let input = ListSAMLProvidersInput()
        do {
            let output = try await client.listSAMLProviders(input: input)

            guard let providerList = output.sAMLProviderList else {
                throw ServiceHandlerError.noSuchPolicy
            }
            return providerList
        } catch {
            throw error
        }
    }
    // snippet-end:[iam.swift.listsamlproviders.handler.listsamlproviders]
}
// snippet-end:[iam.swift.listsamlproviders.handler]
