// snippet-sourcedescription:[DeleteGroup.kt demonstrates how to delete an AWS X-Ray group.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[AWS X-Ray Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.xray

// snippet-start:[xray.kotlin_delete_group.import]
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.DeleteGroupRequest
import kotlin.system.exitProcess
// snippet-end:[xray.kotlin_delete_group.import]

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
            groupName - The name of the group. 
                
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val groupName = args[0]
    deleteSpecificGroup(groupName)
}

// snippet-start:[xray.kotlin_delete_group.main]
suspend fun deleteSpecificGroup(groupNameVal: String) {

    val groupRequest = DeleteGroupRequest {
        groupName = groupNameVal
    }

    XRayClient { region = "us-east-1" }.use { xRayClient ->
        xRayClient.deleteGroup(groupRequest)
        println("$groupNameVal was deleted!")
    }
}
// snippet-end:[xray.kotlin_delete_group.main]
