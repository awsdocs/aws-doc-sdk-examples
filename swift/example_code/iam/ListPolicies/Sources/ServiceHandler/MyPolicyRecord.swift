/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation

/// A structure type used to return the desired information about an IAM policy.
///
/// - term ``id``: The user's unique, stable ID.
/// - term ``name``: The user's name. See [IAM
///   identifiers](https://docs.aws.amazon.com/IAM/latest/UserGuide/Using_Identifiers.html)
///   in the _IAM User Guide_.
///
// snippet-start:[iam.swift.listpolicies.policy-record]
public struct MyPolicyRecord: Equatable {
    public var name: String
    public var id: String
    public var arn: String

    init(name: String, id: String, arn: String) {
        self.name = name
        self.id = id
        self.arn = arn
    }

    func matches(name: String, id: String, arn: String) -> Bool {
        return id == self.id && name == self.name && arn == self.arn
    }
}
// snippet-end:[iam.swift.listpolicies.policy-record]