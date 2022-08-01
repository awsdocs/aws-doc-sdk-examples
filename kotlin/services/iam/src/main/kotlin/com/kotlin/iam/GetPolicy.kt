// snippet-sourcedescription:[GetPolicy.kt demonstrates how to get the details for an AWS Identity and Access Management (IAM) policy.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Identity and Access Management (IAM)]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.get_policy.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.GetPolicyRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.get_policy.import]

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
    getIAMPolicy(policyARN)
}

// snippet-start:[iam.kotlin.get_policy.main]
suspend fun getIAMPolicy(policyArnVal: String?) {

    val request = GetPolicyRequest {
        policyArn = policyArnVal
    }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.getPolicy(request)
        println("Successfully retrieved policy ${response.policy?.policyName}")
    }
}
// snippet-end:[iam.kotlin.get_policy.main]
