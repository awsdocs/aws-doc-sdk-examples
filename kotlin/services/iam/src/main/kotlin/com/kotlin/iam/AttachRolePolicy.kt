//snippet-sourcedescription:[AttachRolePolicy.kt demonstrates how to attach a policy to an existing AWS Identity and Access Management (IAM) role.]
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

// snippet-start:[iam.kotlin.attach_role_policy.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.AttachRolePolicyRequest
import aws.sdk.kotlin.services.iam.model.AttachedPolicy
import aws.sdk.kotlin.services.iam.model.ListAttachedRolePoliciesRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.attach_role_policy.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <roleName> <policyArn>
        Where:
            roleName - a role name that you can obtain from the AWS Management Console. 
            policyArn - a policy ARN that you can obtain from the AWS Management Console. 
        """

     if (args.size != 2) {
         println(usage)
         exitProcess(0)
     }

    val roleName = args[0]
    val policyArn = args[1]
    attachIAMRolePolicy(roleName, policyArn)
    }

// snippet-start:[iam.kotlin.attach_role_policy.main]
suspend fun attachIAMRolePolicy(roleNameVal: String, policyArnVal: String) {

    val request = ListAttachedRolePoliciesRequest {
        roleName = roleNameVal
    }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        val response = iamClient.listAttachedRolePolicies(request)
        val attachedPolicies = response.attachedPolicies

        // Ensure that the policy is not attached to this role.
        val checkStatus: Int
        if (attachedPolicies != null) {
            checkStatus = checkList(attachedPolicies, policyArnVal)
            if (checkStatus == -1)
                return
        }

       val policyRequest = AttachRolePolicyRequest {
            roleName = roleNameVal
            policyArn = policyArnVal
        }
        iamClient.attachRolePolicy(policyRequest)
        println("Successfully attached policy $policyArnVal to role $roleNameVal")
      }
}

fun checkList(attachedPolicies:List<AttachedPolicy>, policyArnVal:String) :Int {

    for (policy in attachedPolicies) {
        val polArn = policy.policyArn.toString()

        if (polArn.compareTo(policyArnVal) == 0) {
            println("The policy is already attached to this role.")
            return -1
        }
    }
    return 0
}
// snippet-end:[iam.kotlin.attach_role_policy.main]