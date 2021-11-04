//snippet-sourcedescription:[ListVaults.kt demonstrates how to list all the Amazon Glacier vaults.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glacier

// snippet-start:[glacier.kotlin.list_vaults.import]
import aws.sdk.kotlin.services.glacier.GlacierClient
import aws.sdk.kotlin.services.glacier.model.GlacierException
import aws.sdk.kotlin.services.glacier.model.ListVaultsRequest
import kotlin.system.exitProcess
// snippet-end:[glacier.kotlin.list_vaults.import]

suspend fun main() {
    val glacierClient = GlacierClient { region = "us-east-1" }
    listAllVault(glacierClient )
    glacierClient.close()
}

// snippet-start:[glacier.kotlin.list_vaults.main]
suspend fun listAllVault(glacier: GlacierClient) {

    println("Your Amazon Glacier vaults:")
    try {
            val response = glacier.listVaults(ListVaultsRequest{})
             response.vaultList?.forEach { vault ->
                 println("* ${vault.vaultName}")
             }

    } catch (e: GlacierException) {
        println(e.message)
        glacier.close()
        exitProcess(0)
    }
}
// snippet-end:[glacier.kotlin.list_vaults.main]