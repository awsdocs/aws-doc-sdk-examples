//snippet-sourcedescription:[GetPolicy.kt demonstrates how to get the details for an AWS Identity and Access Management (IAM) policy.]
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

// snippet-start:[iam.kotlin.get_policy.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.GetPolicyRequest
import aws.sdk.kotlin.services.iam.model.IamException
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.get_policy.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <policyARN> 
        Where:
           policyARN - a policy ARN value to delete.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val policyARN = args[0]
    val iamClient = IamClient{region="AWS_GLOBAL"}
    getIAMPolicy(iamClient, policyARN)
    iamClient.close()
}

// snippet-start:[iam.kotlin.get_policy.main]
suspend fun getIAMPolicy(iamClient: IamClient, policyArnVal: String?) {

    try {
        val request = GetPolicyRequest {
            policyArn = policyArnVal
        }

        val response = iamClient.getPolicy(request)
        println("Successfully retrieved policy ${response.policy?.policyName}")

    } catch (e: IamException) {
        println(e.message)
        iamClient.close()
        exitProcess(0)
    }
}
// snippet-end:[iam.kotlin.get_policy.main]