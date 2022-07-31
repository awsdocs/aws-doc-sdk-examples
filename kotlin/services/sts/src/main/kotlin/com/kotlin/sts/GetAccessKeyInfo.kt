// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetAccessKeyInfo.kt demonstrates how to return the account identifier for the specified access key ID by using AWS Security Token Service (AWS STS).]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Security Token Service (AWS STS)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sts

// snippet-start:[sts.kotlin.get_access_key.import]
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.model.GetAccessKeyInfoRequest
import kotlin.system.exitProcess
// snippet-end:[sts.kotlin.get_access_key.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            accessKeyId> 

        Where:
            accessKeyId - The identifier of an access key (for example, XXXXX3JWY3BXW7POHDLA). 
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val accessKeyId = args[0]
    getKeyInfo(accessKeyId)
}

// snippet-start:[sts.kotlin.get_access_key.main]
suspend fun getKeyInfo(accessKeyIdVal: String?) {

    val accessRequest = GetAccessKeyInfoRequest {
        accessKeyId = accessKeyIdVal
    }

    StsClient { region = "us-east-1" }.use { stsClient ->
        val accessResponse = stsClient.getAccessKeyInfo(accessRequest)
        println("The account associated with the access key is ${accessResponse.account}")
    }
}
// snippet-end:[sts.kotlin.get_access_key.main]
