//snippet-sourcedescription:[ListAccessKeys.kt demonstrates how to list access keys associated with an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Identity and Access Management (IAM)]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/27/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.list_access_keys.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.IamException
import aws.sdk.kotlin.services.iam.model.ListAccessKeysRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.list_access_keys.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <username> 
        Where:
             username - the name of the user for which access keys are retrieved. 
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val userName = args[0]
    val iamClient = IamClient{region="AWS_GLOBAL"}
    listKeys(iamClient, userName)
    iamClient.close()
}

// snippet-start:[iam.kotlin.list_access_keys.main]
suspend fun listKeys(iamClient: IamClient, userNameVal: String?) {

    try {
           val request = ListAccessKeysRequest {
                 userName = userNameVal
              }

           val response = iamClient.listAccessKeys(request)
           for (metadata in response.accessKeyMetadata!!) {
                   println("Retrieved access key ${metadata.accessKeyId}")
            }

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
}
// snippet-end:[iam.kotlin.list_access_keys.main]