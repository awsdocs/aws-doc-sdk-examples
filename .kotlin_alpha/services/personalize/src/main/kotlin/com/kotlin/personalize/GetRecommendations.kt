//snippet-sourcedescription:[GetRecommendations.kt demonstrates how to return a list of recommended items.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/02/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.personalize

//snippet-start:[personalize.kotlin.get_recommendations.import]
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
import aws.sdk.kotlin.services.personalizeruntime.PersonalizeRuntimeClient
import aws.sdk.kotlin.services.personalizeruntime.model.GetRecommendationsRequest
import aws.sdk.kotlin.services.personalizeruntime.model.PredictedItem
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.get_recommendations.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

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
    val personalizeRuntimeClient = PersonalizeRuntimeClient{region = "us-east-1" }
    getRecs(personalizeRuntimeClient, campaignArn, userId )
    personalizeRuntimeClient.close()
}

//snippet-start:[personalize.kotlin.get_recommendations.main]
suspend fun getRecs(personalizeRuntimeClient: PersonalizeRuntimeClient, campaignArnVal: String?, userIdVal: String?) {
        try {

            val recommendationsRequest = GetRecommendationsRequest {
                campaignArn = campaignArnVal
                numResults = 20
                userId = userIdVal
            }

            val recommendationsResponse =  personalizeRuntimeClient.getRecommendations(recommendationsRequest)
            val items: List<PredictedItem>? = recommendationsResponse.itemList
            if (items != null) {
                for (item in items) {
                    println("Item Id is ${item.itemId}")
                    println("Item score is ${item.score}")
                }
            }

        } catch (ex: PersonalizeException) {
            println(ex.message)
            personalizeRuntimeClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[personalize.kotlin.get_recommendations.main]