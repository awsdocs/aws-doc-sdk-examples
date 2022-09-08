// snippet-sourcedescription:[ListDatasetGroups.kt demonstrates how to list Amazon Personalize data set groups.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.list_dsgroups.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListDatasetGroupsRequest
// snippet-end:[personalize.kotlin.list_dsgroups.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listDSGroups()
}

// snippet-start:[personalize.kotlin.list_dsgroups.main]
suspend fun listDSGroups() {

    val request = ListDatasetGroupsRequest {
        maxResults = 15
    }
    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        val response = personalizeClient.listDatasetGroups(request)
        response.datasetGroups?.forEach { group ->
            println("The data set name is ${group.name}")
            println("The data set ARN is ${group.datasetGroupArn}")
        }
    }
}
// snippet-end:[personalize.kotlin.list_dsgroups.main]
