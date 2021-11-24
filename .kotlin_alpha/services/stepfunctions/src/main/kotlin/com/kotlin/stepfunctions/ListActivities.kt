//snippet-sourcedescription:[ListActivities.kt demonstrates how to list existing activities for AWS Step Functions.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Step Functions]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.list_activities.import]
import aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.ListActivitiesRequest
// snippet-end:[stepfunctions.kotlin.list_activities.import]

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