// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudformation

// snippet-start:[cf.kotlin._template.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.GetTemplateRequest
import kotlin.system.exitProcess
// snippet-end:[cf.kotlin._template.import]

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <stackName> 

        Where:
            stackName - The name of the AWS CloudFormation stack. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val stackName = args[0]
    getSpecificTemplate(stackName)
}

// snippet-start:[cf.kotlin._template.main]
suspend fun getSpecificTemplate(stackNameVal: String) {
    val request =
        GetTemplateRequest {
            stackName = stackNameVal
        }

    CloudFormationClient { region = "us-east-1" }.use { cfClient ->
        val response = cfClient.getTemplate(request)
        val body = response.templateBody
        println(body)
    }
}
// snippet-end:[cf.kotlin._template.main]
