// snippet-sourcedescription:[ListActivities.kt demonstrates how to list existing activities for AWS Step Functions.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Step Functions]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.list_activities.import]
import aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.ListActivitiesRequest
// snippet-end:[stepfunctions.kotlin.list_activities.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllActivites()
}

// snippet-start:[stepfunctions.kotlin.list_activities.main]
suspend fun listAllActivites() {

    val activitiesRequest = ListActivitiesRequest {
        maxResults = 10
    }

    SfnClient { region = "us-east-1" }.use { sfnClient ->
        val response = sfnClient.listActivities(activitiesRequest)
        response.activities?.forEach { item ->
            println("The activity ARN is ${item.activityArn}")
            println("The activity name is ${item.name}")
        }
    }
}
// snippet-end:[stepfunctions.kotlin.list_activities.main]
