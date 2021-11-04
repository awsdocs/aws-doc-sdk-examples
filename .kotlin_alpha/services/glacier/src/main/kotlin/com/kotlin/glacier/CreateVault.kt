//snippet-sourcedescription:[CreateVault.kt demonstrates how to create an Amazon Glacier vault.]
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

// snippet-start:[glacier.kotlin.create.import]
import aws.sdk.kotlin.services.glacier.GlacierClient
import aws.sdk.kotlin.services.glacier.model.CreateVaultRequest
import aws.sdk.kotlin.services.glacier.model.GlacierException
import kotlin.system.exitProcess
// snippet-end:[glacier.kotlin.create.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
            <vaultName>

        Where:
            vaultName - the name of the vault to create.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
     }

    val vaultName = args[0]
    val glacierClient = GlacierClient { region = "us-east-1" }
    createGlacierVault(glacierClient, vaultName)
    glacierClient.close()
}

// snippet-start:[glacier.kotlin.create.main]
suspend fun createGlacierVault(glacier: GlacierClient, vaultNameVal: String?) {
    try {

        val vaultRequest = CreateVaultRequest {vaultName = vaultNameVal}
        val createVaultResult = glacier.createVault(vaultRequest)
        println("The URI of the new vault is ${createVaultResult.location}")

    } catch (e: GlacierException) {
        println(e.message)
        glacier.close()
        exitProcess(0)
    }
}
// snippet-end:[glacier.kotlin.create.main]