// snippet-sourcedescription:[CreateKeyPair.kt demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) key pair.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.ec2

// snippet-start:[ec2.kotlin.create_key_pair.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.CreateKeyPairRequest
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.create_key_pair.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """

    Usage:
        <keyName> 

    Where:
        keyName - A key pair name (for example, TestKeyPair). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val keyName = args[0]
    createEC2KeyPair(keyName)
}

// snippet-start:[ec2.kotlin.create_key_pair.main]
suspend fun createEC2KeyPair(keyNameVal: String) {

    val request = CreateKeyPairRequest {
        keyName = keyNameVal
    }

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val response = ec2.createKeyPair(request)
        println("The key ID is ${response.keyPairId}")
    }
}
// snippet-end:[ec2.kotlin.create_key_pair.main]
