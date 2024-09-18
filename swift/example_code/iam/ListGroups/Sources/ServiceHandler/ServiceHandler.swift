// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
   A class containing functions that interact with AWS services.
*/

// snippet-start:[iam.swift.listgroups.handler]
// snippet-start:[iam.swift.listgroups.handler.imports]
import Foundation
import AWSIAM
import ClientRuntime
import AWSClientRuntime
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
    public init() async {
        do {
            client = try IAMClient(region: "us-east-1")
        } catch {
            print("ERROR: ", dump(error, name: "Initializing Amazon IAM client"))
            exit(1)
        }
    }
    // snippet-end:[iam.swift.listgroups.handler.init]

    /// Returns a list of all AWS Identity and Access Management (IAM) group
    /// names.
    ///
    /// - Returns: An array of user records.
    // snippet-start:[iam.swift.listgroups.handler.listgroups]
    public func listGroups() async throws -> [String] {
        var groupList: [String] = []
        var marker: String? = nil
        var isTruncated: Bool
        
        repeat {
            let input = ListGroupsInput(marker: marker)
            let output = try await client.listGroups(input: input)
            
            guard let groups = output.groups else {
                return groupList
            }

            for group in groups {
                if let name = group.groupName {
                    groupList.append(name)
                }
            }
            marker = output.marker
            isTruncated = output.isTruncated
        } while isTruncated == true
        return groupList
    }
    // snippet-end:[iam.swift.listgroups.handler.listgroups]
}
// snippet-end:[iam.swift.listgroups.handler]
