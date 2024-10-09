// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A class containing functions that interact with AWS services.
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
    public let client: IAMClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.listusers.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }
    // snippet-end:[iam.swift.listusers.handler.init]

    /// Returns a list of the users on the AWS account in an array of
    /// ``MyUserRecord`` structures.
    ///
    /// - Returns: An array of user records.
    // snippet-start:[iam.swift.listusers.handler.ListUsers]
    public func listUsers() async throws -> [MyUserRecord] {
        var userList: [MyUserRecord] = []
        
        // Use "Paginated" to get all the users.
        // This lets the SDK handle the 'isTruncated' in "ListUsersOutput".
        let input = ListUsersInput()
        let output = client.listUsersPaginated(input: input)

        do {
            for try await page in output {
                guard let users = page.users else {
                    continue
                }
                for user in users {
                    if let id = user.userId, let name = user.userName {
                        userList.append(MyUserRecord(id: id, name: name))
                    }
                }
            }
        }
        catch {
            print("ERROR: listUsers:", dump(error))
            throw error
        }
       return userList
    }
    // snippet-end:[iam.swift.listusers.handler.ListUsers]
}
// snippet-end:[iam.swift.listusers.handler]
