// snippet-sourcedescription:[ListIdentities.kt demonstrates how to obtain a list of identities for your AWS account.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/05/2021]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.kotlin.ses

// snippet-start:[ses.kotlin.identities.import]
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.ListIdentitiesRequest
// snippet-end:[ses.kotlin.identities.import]

suspend fun main() {
    listSESIdentities()
}

// snippet-start:[ses.kotlin.identities.main]
suspend fun listSESIdentities() {

    SesClient { region = "us-east-1" }.use { sesClient ->
        val response = sesClient.listIdentities(ListIdentitiesRequest{})
        response.identities?.forEach { identity ->
                 println("The identity is $identity")

        }

    }
}
// snippet-end:[ses.kotlin.identities.main]