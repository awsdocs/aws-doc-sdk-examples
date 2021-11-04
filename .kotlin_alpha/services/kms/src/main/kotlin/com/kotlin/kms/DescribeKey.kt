//snippet-sourcedescription:[DescribeKey.kt demonstrates how to obtain information about an AWS Key Management Service (AWS KMS) key.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_describe_key.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.DescribeKeyRequest
import aws.sdk.kotlin.services.kms.model.KmsException
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_describe_key.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {


    val usage = """
        Usage:
            <keyId> 
        Where:
            keyId -  a key id value to describe (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val keyId = args[0]
    val keyClient = KmsClient{region="us-west-2"}
    describeSpecifcKey(keyClient,keyId)
    keyClient.close()
}

// snippet-start:[kms.kotlin_describe_key.main]
suspend  fun describeSpecifcKey(kmsClient: KmsClient, keyIdVal: String?) {
        try {

            val keyRequest = DescribeKeyRequest {
                keyId = keyIdVal
            }

            val response = kmsClient.describeKey(keyRequest)
            println("The key description is " + (response.keyMetadata?.description))
            println("The key ARN is " + response.keyMetadata?.arn)

        } catch (ex: KmsException) {
            println(ex.message)
            kmsClient.close()
            exitProcess(0)
        }
 }
// snippet-end:[kms.kotlin_describe_key.main]