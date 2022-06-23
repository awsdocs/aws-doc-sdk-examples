//snippet-sourcedescription:[ListAccessKeys.kt demonstrates how to list access keys associated with an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Identity and Access Management (IAM)]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.list_access_keys.import]
import aws.sdk.kotlin.services.iam.IamClient
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
    listKeys(userName)
    }

// snippet-start:[iam.kotlin.list_access_keys.main]
suspend fun listKeys(userNameVal: String?) {

      val request = ListAccessKeysRequest {
          userName = userNameVal
      }
      IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.listAccessKeys(request)
            response.accessKeyMetadata?.forEach { md ->
                println("Retrieved access key ${md.accessKeyId}")
            }
      }
}
// snippet-end:[iam.kotlin.list_access_keys.main]