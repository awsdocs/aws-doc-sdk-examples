// snippet-sourcedescription:[GetGroups.kt demonstrates how to retrieve all active group details.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[AWS X-Ray Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.xray

// snippet-start:[xray.kotlin_get_groups.import]
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.GetGroupsRequest
// snippet-end:[xray.kotlin_get_groups.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getAllGroups()
}

// snippet-start:[xray.kotlin_get_groups.main]
suspend fun getAllGroups() {

    XRayClient { region = "us-east-1" }.use { xRayClient ->
        val response = xRayClient.getGroups(GetGroupsRequest {})
        response.groups?.forEach { group ->
            println("The AWS X-Ray group name is ${group.groupName}")
        }
    }
}
// snippet-end:[xray.kotlin_get_groups.main]
