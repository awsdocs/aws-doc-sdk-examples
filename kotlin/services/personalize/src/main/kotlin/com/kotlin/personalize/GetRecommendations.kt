// snippet-sourcedescription:[GetRecommendations.kt demonstrates how to return a list of recommended items.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Code Sample]
// snippet-service:[Amazon Personalize]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/27/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.personalize

// snippet-start:[personalize.kotlin.get_recommendations.import]
import aws.sdk.kotlin.services.personalizeruntime.PersonalizeRuntimeClient
import aws.sdk.kotlin.services.personalizeruntime.model.GetRecommendationsRequest
import kotlin.system.exitProcess
// snippet-end:[personalize.kotlin.get_recommendations.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <campaignArn> <userId>

    Where:
           campaignArn - The ARN of the campaign.
           userId - The user ID to provide recommendations for.
     """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val campaignArn = args[0]
    val userId = args[1]
    getRecs(campaignArn, userId)
}

// snippet-start:[personalize.kotlin.get_recommendations.main]
suspend fun getRecs(campaignArnVal: String?, userIdVal: String?) {

    val request = GetRecommendationsRequest {
        campaignArn = campaignArnVal
        numResults = 20
        userId = userIdVal
    }

    PersonalizeRuntimeClient { region = "us-east-1" }.use { personalizeRuntimeClient ->
        val response = personalizeRuntimeClient.getRecommendations(request)
        response.itemList?.forEach { item ->
            println("Item Id is ${item.itemId}")
            println("Item score is ${item.score}")
        }
    }
}
// snippet-end:[personalize.kotlin.get_recommendations.main]
