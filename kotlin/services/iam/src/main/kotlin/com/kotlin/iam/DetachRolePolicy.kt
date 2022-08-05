// snippet-sourcedescription:[DetachRolePolicy.kt demonstrates how to detach a policy from an AWS Identity and Access Management (IAM) role.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Identity and Access Management (IAM)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.detach_role_policy.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.DetachRolePolicyRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.detach_role_policy.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <roleName> <policyArn>
        Where:
            roleName - A role name that you can obtain from the AWS Management Console. 
            policyArn - A policy ARN that you can obtain from the AWS Management Console. 
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val roleName = args[0]
    val policyArn = args[1]
    detachPolicy(roleName, policyArn)
}

// snippet-start:[iam.kotlin.detach_role_policy.main]
suspend fun detachPolicy(roleNameVal: String, policyArnVal: String) {

    val request = DetachRolePolicyRequest {
        roleName = roleNameVal
        policyArn = policyArnVal
    }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        iamClient.detachRolePolicy(request)
        println("Successfully detached policy $policyArnVal from role $roleNameVal")
    }
}
// snippet-end:[iam.kotlin.detach_role_policy.main]
