//snippet-sourcedescription:[CreateGroup.kt demonstrates how to create an AWS X-Ray group with a filter expression.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS X-Ray Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

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

suspend fun main(args:Array<String>) {

    val usage = """
        
        Usage: 
            <groupName>
        
        Where:
            groupName - the name of the group to create 
                
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