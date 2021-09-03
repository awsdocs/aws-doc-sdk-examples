// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetAccessKeyInfo.kt demonstrates how to return the account identifier for the specified access key ID by using AWS Security Token Service (AWS STS).]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Security Token Service (AWS STS)]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06/07/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sts

// snippet-start:[sts.kotlin.get_access_key.import]
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.model.GetAccessKeyInfoRequest
import aws.sdk.kotlin.services.sts.model.StsException
import kotlin.system.exitProcess
// snippet-end:[sts.kotlin.get_access_key.import]


/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.
For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            accessKeyId> 

        Where:
            accessKeyId - the identifier of an access key (for example, XXXXX3JWY3BXW7POHDLA). 
        """

    if (args.size != 1) {
       println(usage)
       exitProcess(0)
    }

    val accessKeyId = args[0]
    val stsClient = StsClient{region ="us-east-1"}
    getKeyInfo(stsClient, accessKeyId)
}

// snippet-start:[sts.kotlin.get_access_key.main]
suspend fun getKeyInfo(stsClient: StsClient, accessKeyIdVal: String?) {
    try {
        val accessRequest = GetAccessKeyInfoRequest {
            accessKeyId = accessKeyIdVal
        }

        val accessResponse = stsClient.getAccessKeyInfo(accessRequest)
        println("The account associated with the access key is ${accessResponse.account}")

    } catch (e: StsException) {
        println(e.message)
        stsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[sts.kotlin.get_access_key.main]