// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteStack.kt demonstrates how to delete an existing stack.]
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

// snippet-start:[cf.kotlin.delete_stack.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.DeleteStackRequest
import aws.sdk.kotlin.services.cloudformation.model.CloudFormationException
import kotlin.system.exitProcess
// snippet-end:[cf.kotlin.delete_stack.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <stackName> <roleARN> <location> <key> <value> 

    Where:
        stackName - the name of the AWS CloudFormation stack. 
      
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
    }

    val stackName =  args[0]
    val cfClient = CloudFormationClient{region="us-east-1"}
    deleteSpecificTemplate(cfClient, stackName)
    cfClient.close()
}

// snippet-start:[cf.kotlin.delete_stack.main]
suspend fun deleteSpecificTemplate(cfClient: CloudFormationClient, stackNameVal: String?) {
    try {
        val stackRequest = DeleteStackRequest {
        stackName = stackNameVal
        }

        cfClient.deleteStack(stackRequest)
        println("The AWS CloudFormation stack was successfully deleted!")

    } catch (e: CloudFormationException) {
        println(e.message)
        cfClient.close()
        exitProcess(0)
    }
}
// snippet-end:[cf.kotlin.delete_stack.main]