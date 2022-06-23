// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetTemplate.kt demonstrates how to retrieve a template.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudFormation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/03/2021]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.kotlin.cloudformation

// snippet-start:[cf.kotlin._template.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.GetTemplateRequest
import kotlin.system.exitProcess
// snippet-end:[cf.kotlin._template.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <stackName> 

        Where:
            stackName - the name of the AWS CloudFormation stack. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }

    val stackName = args[0]
    getSpecificTemplate(stackName)
 }

// snippet-start:[cf.kotlin._template.main]
suspend fun getSpecificTemplate( stackNameVal: String) {

        val request = GetTemplateRequest {
            stackName = stackNameVal
        }

        CloudFormationClient { region = "us-east-1" }.use { cfClient ->
         val response = cfClient.getTemplate(request)
         val body = response.templateBody
         println(body)
        }
}
// snippet-end:[cf.kotlin._template.main]