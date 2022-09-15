// snippet-sourcedescription:[CreateGroup.kt demonstrates how to create an AWS X-Ray group with a filter expression.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[AWS X-Ray Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.xray

// snippet-start:[xray.kotlin_create_group.import]
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.CreateGroupRequest
import kotlin.system.exitProcess
// snippet-end:[xray.kotlin_create_group.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        
        Usage: 
            <groupName>
        
        Where:
            groupName - The name of the group to create 
                
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val groupName = args[0]
    createNewGroup(groupName)
}

// snippet-start:[xray.kotlin_create_group.main]
suspend fun createNewGroup(groupNameVal: String?) {

    val groupRequest = CreateGroupRequest {
        filterExpression = "fault = true AND http.url CONTAINS \"example/game\" AND responsetime >= 5"
        groupName = groupNameVal
    }

    XRayClient { region = "us-east-1" }.use { xRayClient ->
        val groupResponse = xRayClient.createGroup(groupRequest)
        println("The Group ARN is " + (groupResponse.group?.groupArn))
    }
}
// snippet-end:[xray.kotlin_create_group.main]
