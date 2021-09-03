//snippet-sourcedescription:[ListActivities.kt demonstrates how to list existing activities for AWS Step Functions.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Step Functions]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[04/06/2021]
//snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.stepfunctions

// snippet-start:[stepfunctions.kotlin.list_activities.import]
import aws.sdk.kotlin.services.sfn.model.SfnException
import aws.sdk.kotlin.services.sfn.SfnClient
import aws.sdk.kotlin.services.sfn.model.ListActivitiesRequest
import kotlin.system.exitProcess
// snippet-end:[stepfunctions.kotlin.list_activities.import]

suspend fun main() {

    val sfnClient = SfnClient{region = "us-east-1" }
    listAllActivites(sfnClient)
    sfnClient.close()
}

// snippet-start:[stepfunctions.kotlin.list_activities.main]
suspend fun listAllActivites(sfnClient: SfnClient) {
        try {
            val activitiesRequest = ListActivitiesRequest {
                maxResults = 10
            }

            val response = sfnClient.listActivities(activitiesRequest)
            val items = response.activities
            if (items != null) {
                for (item in items) {
                    println("The activity ARN is " + item.activityArn)
                    println("The activity name is " + item.name)
                }
            }

        } catch (ex: SfnException) {
            println(ex.message)
            sfnClient.close()
            exitProcess(0)
        }
}
// snippet-end:[stepfunctions.kotlin.list_activities.main]