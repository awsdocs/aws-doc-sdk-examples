// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.ses

// snippet-start:[ses.kotlin.identities.import]
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.ListIdentitiesRequest
// snippet-end:[ses.kotlin.identities.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listSESIdentities()
}

// snippet-start:[ses.kotlin.identities.main]
suspend fun listSESIdentities() {
    SesClient { region = "us-east-1" }.use { sesClient ->
        val response = sesClient.listIdentities(ListIdentitiesRequest {})
        response.identities?.forEach { identity ->
            println("The identity is $identity")
        }
    }
}
// snippet-end:[ses.kotlin.identities.main]
