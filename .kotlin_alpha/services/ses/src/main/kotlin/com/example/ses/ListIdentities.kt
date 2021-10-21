// snippet-sourcedescription:[ListIdentities.kt demonstrates how to obtain a list of identities for your AWS account.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06/23/2020]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.kotlin.ses

// snippet-start:[ses.kotlin.identities.import]
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.ListIdentitiesRequest
import aws.sdk.kotlin.services.ses.model.SesException
import kotlin.system.exitProcess
// snippet-end:[ses.kotlin.identities.import]

suspend fun main() {

    val sesClient = SesClient{region="us-east-1"}
    listSESIdentities(sesClient)
}

// snippet-start:[ses.kotlin.identities.main]
suspend fun listSESIdentities(sesClient: SesClient) {
    try {
        val identitiesResponse = sesClient.listIdentities(ListIdentitiesRequest{})
        val identities = identitiesResponse.identities
        if (identities != null) {
            for (identity in identities) {
                println("The identity is $identity")
            }
        }

    } catch (e: SesException) {
        println(e.message)
        sesClient.close()
        exitProcess(0)
    }
}
// snippet-end:[ses.kotlin.identities.main]
