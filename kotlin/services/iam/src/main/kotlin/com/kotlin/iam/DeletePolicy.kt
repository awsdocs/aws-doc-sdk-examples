// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.iam

// snippet-start:[iam.kotlin.delete_policy.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.DeletePolicyRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.delete_policy.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <policyARN> 
        Where:
           policyARN - A policy ARN value to delete.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val policyARN = args[0]
    deleteIAMPolicy(policyARN)
}

// snippet-start:[iam.kotlin.delete_policy.main]
suspend fun deleteIAMPolicy(policyARNVal: String?) {
    val request =
        DeletePolicyRequest {
            policyArn = policyARNVal
        }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        iamClient.deletePolicy(request)
        println("Successfully deleted $policyARNVal")
    }
}
// snippet-end:[iam.kotlin.delete_policy.main]
