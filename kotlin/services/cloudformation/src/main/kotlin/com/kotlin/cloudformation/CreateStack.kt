// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudformation

// snippet-start:[cf.kotlin.create_stack.import]
import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.CreateStackRequest
import aws.sdk.kotlin.services.cloudformation.model.OnFailure
import kotlin.system.exitProcess
// snippet-end:[cf.kotlin.create_stack.import]

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <stackName> <roleARN> <location> <key> <value> 

    Where:
        stackName - The name of the AWS CloudFormation stack. 
        roleARN - The ARN of the role that has AWS CloudFormation permissions. 
        location - The location of file containing the template body. (for example, https://s3.amazonaws.com/<bucketname>/template.yml). 
        key - The key associated with the parameter. 
        value - The value associated with the parameter. 
    """

    if (args.size != 5) {
        println(usage)
        exitProcess(0)
    }

    val stackName = args[0]
    val roleARN = args[1]
    val location = args[2]
    createCFStack(stackName, roleARN, location)
}

// snippet-start:[cf.kotlin.create_stack.main]
suspend fun createCFStack(stackNameVal: String, roleARNVal: String?, location: String?) {
    val request =
        CreateStackRequest {
            stackName = stackNameVal
            templateUrl = location
            roleArn = roleARNVal
            onFailure = OnFailure.Rollback
        }

    CloudFormationClient { region = "us-east-1" }.use { cfClient ->
        cfClient.createStack(request)
        println("$stackNameVal was created")
    }
}
// snippet-end:[cf.kotlin.create_stack.main]
