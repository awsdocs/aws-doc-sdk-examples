// snippet-sourcedescription:[ListCampaigns.kt demonstrates how to list Amazon Personalize campaigns.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.list_campaigns.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListCampaignsRequest
import kotlin.system.exitProcess
// snippet-end:[personalize.kotlin.list_campaigns.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <solutionArn>

    Where:
         solutionArn - The ARN of the solution.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val solutionArn = args[0]
    listAllCampaigns(solutionArn)
}

// snippet-start:[personalize.kotlin.list_campaigns.main]
suspend fun listAllCampaigns(solutionArnVal: String?) {

    val request = ListCampaignsRequest {
        maxResults = 10
        solutionArn = solutionArnVal
    }
    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        val response = personalizeClient.listCampaigns(request)
        response.campaigns?.forEach { campaign ->
            println("Campaign name is ${campaign.name}")
            println("Campaign ARN is ${campaign.campaignArn}")
        }
    }
}
// snippet-end:[personalize.kotlin.list_campaigns.main]
