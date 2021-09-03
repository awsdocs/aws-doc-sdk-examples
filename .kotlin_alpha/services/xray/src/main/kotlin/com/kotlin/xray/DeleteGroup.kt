//snippet-sourcedescription:[DeleteGroup.kt demonstrates how to delete an AWS X-Ray group.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS X-Ray Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[04/12/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.xray

// snippet-start:[xray.kotlin_delete_group.import]
import aws.sdk.kotlin.services.xray.model.XRayException
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.DeleteGroupRequest
import kotlin.system.exitProcess
// snippet-end:[xray.kotlin_delete_group.import]

suspend fun main(args:Array<String>) {

    val usage = """
        
        Usage: 
            <groupName>
        
        Where:
            groupName - the name of the group. 
                
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val groupName = args[0]
    val xRayClient = XRayClient{region = "us-east-1"}
    deleteSpecificGroup(xRayClient,groupName)
    xRayClient.close()

}

// snippet-start:[xray.kotlin_delete_group.main]
suspend fun deleteSpecificGroup(xRayClient: XRayClient, groupNameVal: String) {
        try {
            val groupRequest = DeleteGroupRequest {
                groupName = groupNameVal
            }

            xRayClient.deleteGroup(groupRequest)
            println("$groupNameVal was deleted!")

        } catch (ex: XRayException) {
            println(ex.message)
            xRayClient.close()
            exitProcess(0)
        }
    }
// snippet-end:[xray.kotlin_delete_group.main]