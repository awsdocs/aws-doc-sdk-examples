//snippet-sourcedescription:[ListAccountAliases.kt demonstrates how to list all aliases associated with an AWS account.]
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

// snippet-start:[iam.kotlin.list_account_aliases.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.IamException
import aws.sdk.kotlin.services.iam.model.ListAccountAliasesRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.list_account_aliases.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val iamClient = IamClient{region="AWS_GLOBAL"}
    listAliases(iamClient)
    iamClient.close()
}

// snippet-start:[iam.kotlin.list_account_aliases.main]
suspend  fun listAliases(iamClient: IamClient) {
    try {
        val response = iamClient.listAccountAliases(ListAccountAliasesRequest{})
        response.accountAliases?.forEach { alias ->
            println("Retrieved account alias $alias")
        }

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
}
// snippet-end:[iam.kotlin.list_account_aliases.main]