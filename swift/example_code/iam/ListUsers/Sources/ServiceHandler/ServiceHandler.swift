/*
   A class containing functions that interact with AWS services.
   
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[iam.swift.listusers.handler]
// snippet-start:[iam.swift.listusers.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
// snippet-end:[iam.swift.listusers.handler.imports]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IamClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example. The Region string
    /// `AWS_GLOBAL` is used because users are shared across all Regions.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.listusers.handler.init]
    public init() async {
        do {
            client = try IamClient(region: "AWS_GLOBAL")
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.listusers.handler.init]

    /// Returns a list of the users on the AWS account in an array of
    /// ``MyUserRecord`` structures.
    ///
    /// - Returns: An array of user records.
    // snippet-start:[iam.swift.listusers.handler.listusers]
    public func listUsers() async throws -> [MyUserRecord] {
        var userList: [MyUserRecord] = []
        var marker: String? = nil
        var isTruncated: Bool
        
        repeat {
            let input = ListUsersInput(marker: marker)
            let output = try await client.listUsers(input: input)
            
            guard let users = output.users else {
                return userList
            }

            for user in users {
                if let id = user.userId, let name = user.userName {
                    userList.append(MyUserRecord(id: id, name: name))
                }
            }
            marker = output.marker
            isTruncated = output.isTruncated
        } while isTruncated == true
        return userList
    }
    // snippet-end:[iam.swift.listusers.handler.listusers]
}
// snippet-end:[iam.swift.listusers.handler]
