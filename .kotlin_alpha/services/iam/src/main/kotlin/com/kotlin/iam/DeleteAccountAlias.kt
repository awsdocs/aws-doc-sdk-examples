//snippet-sourcedescription:[DeleteAccountAlias.kt demonstrates how to delete an alias from an AWS account.]
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

// snippet-start:[iam.kotlin.delete_account_alias.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.DeleteAccountAliasRequest
import aws.sdk.kotlin.services.iam.model.IamException
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.delete_account_alias.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <alias> 
        Where:
            alias - the account alias to delete (for example, myawsaccount).  

        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val alias =  args[0]
    val iamClient = IamClient{region="AWS_GLOBAL"}
    deleteIAMAccountAlias(iamClient, alias)
    iamClient.close()
}

// snippet-start:[iam.kotlin.delete_account_alias.main]
suspend fun deleteIAMAccountAlias(iamClient: IamClient, alias: String) {
    try {
        val request = DeleteAccountAliasRequest {
            accountAlias =alias
        }
        iamClient.deleteAccountAlias(request)
        println("Successfully deleted account alias $alias")

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
    println("Done")
}
// snippet-end:[iam.kotlin.delete_account_alias.main]