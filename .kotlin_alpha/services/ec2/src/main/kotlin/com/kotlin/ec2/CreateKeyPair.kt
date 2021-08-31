//snippet-sourcedescription:[CreateKeyPair.kt demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) key pair.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/22/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.ec2

// snippet-start:[ec2.kotlin.create_key_pair.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.CreateKeyPairRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.create_key_pair.import]

suspend fun main(args:Array<String>) {

    val usage = """

    Usage:
        <keyName> 

    Where:
        keyName - a key pair name (for example, TestKeyPair). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val keyName = args[0]
    val ec2Client = Ec2Client{region = "us-west-2"}
    createEC2KeyPair(ec2Client, keyName)
    ec2Client.close()
}

// snippet-start:[ec2.kotlin.create_key_pair.main]
suspend fun createEC2KeyPair(ec2: Ec2Client, keyNameVal: String?) {
    try {
        val request = CreateKeyPairRequest {
            keyName = keyNameVal
        }

        val response = ec2.createKeyPair(request)
        println("The key ID is ${response.keyPairId}")

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[ec2.kotlin.create_key_pair.main]