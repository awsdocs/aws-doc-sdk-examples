// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example

class QueuePayload {

    // The payload job token.
    private var token: String? = null

    // A dictionary of payload arguments.
    private var arguments: HashMap<String?, String?>? = null

    // Getter and Setter methods for token
    fun getToken(): String? = token

    // Getter and Setter methods for arguments
    fun getArguments(): HashMap<String?, String?>? = arguments
}
