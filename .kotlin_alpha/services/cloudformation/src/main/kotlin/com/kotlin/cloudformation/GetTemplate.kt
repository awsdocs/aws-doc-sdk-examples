// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetTemplate.kt demonstrates how to retrieve a template.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CloudFormation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/31/2021]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.kotlin.cloudformation

// snippet-start:[cf.kotlin._template.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.GetTemplateRequest
import aws.sdk.kotlin.services.cloudformation.model.CloudFormationException
import kotlin.system.exitProcess
// snippet-end:[cf.kotlin._template.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <stackName> 

        Where:
            stackName - the name of the AWS CloudFormation stack. 
    """

    if (args.size != 1) {
        println(usage);
        System.exit(1);
    }

    val stackName = args[0]
    val cfClient = CloudFormationClient{region="us-east-1"}
    getSpecificTemplate(cfClient, stackName)
    cfClient.close()
}

// snippet-start:[cf.kotlin._template.main]
suspend fun getSpecificTemplate(cfClient: CloudFormationClient, stackNameVal: String) {
    try {
        val typeRequest = GetTemplateRequest {
            stackName = stackNameVal
        }

        val response = cfClient.getTemplate(typeRequest)
        val body: String? = response.templateBody
        println(body)

    } catch (e: CloudFormationException) {
        println(e.message)
        cfClient.close()
        exitProcess(0)
    }
}
// snippet-end:[cf.kotlin._template.main]
