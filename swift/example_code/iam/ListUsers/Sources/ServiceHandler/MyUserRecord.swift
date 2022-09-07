/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import Foundation

/// A structure type used to return the desired information about an IAM user.
///
/// - term ``id``: The user's unique, stable ID.
/// - term ``name``: The user's name. See [IAM
///   identifiers](https://docs.aws.amazon.com/IAM/latest/UserGuide/Using_Identifiers.html)
///   in the _IAM User Guide_.
///
// snippet-start:[iam.swift.listusers.user-record]
public struct MyUserRecord: Equatable {
    public var id: String
    public var name: String

    init(id: String, name: String) {
        self.id = id
        self.name = name
    }

    func matches(id: String, name: String) -> Bool {
        return id == self.id && name == self.name
    }
}
// snippet-end:[secretsmanager.swift.listusers.user-record]