// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 A class containing functions that interact with AWS services.
 */

// snippet-start:[iam.swift.listgroups.handler]
// snippet-start:[iam.swift.listgroups.handler.imports]
import AWSClientRuntime
import AWSIAM
import ClientRuntime
import Foundation

// snippet-end:[iam.swift.listgroups.handler.imports]

/// A class containing all the code that interacts with the AWS SDK for Swift.
public class ServiceHandler {
    public let client: IAMClient

    /// Initialize and return a new ``ServiceHandler`` object, which is used
    /// to drive the AWS calls used for the example.
    ///
    /// - Returns: A new ``ServiceHandler`` object, ready to be called to
    ///            execute AWS operations.
    // snippet-start:[iam.swift.listgroups.handler.init]
    public init() async throws {
        do {
            client = try await IAMClient()
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            throw error
        }
    }

    // snippet-end:[iam.swift.listgroups.handler.init]

    /// Returns a list of all AWS Identity and Access Management (IAM) group
    /// names.
    ///
    /// - Returns: An array of user records.
    // snippet-start:[iam.swift.listgroups.handler.ListGroups]
    public func listGroups() async throws -> [String] {
        var groupList: [String] = []

        // Use "Paginated" to get all the groups.
        // This lets the SDK handle the 'isTruncated' property in "ListGroupsOutput".
        let input = ListGroupsInput()

        let pages = client.listGroupsPaginated(input: input)
        do {
            for try await page in pages {
                guard let groups = page.groups else {
                    print("Error: no groups returned.")
                    continue
                }

                for group in groups {
                    if let name = group.groupName {
                        groupList.append(name)
                    }
                }
            }
        } catch {
            print("ERROR: listGroups:", dump(error))
            throw error
        }
        return groupList
    }
    // snippet-end:[iam.swift.listgroups.handler.ListGroups]
}

// snippet-end:[iam.swift.listgroups.handler]
