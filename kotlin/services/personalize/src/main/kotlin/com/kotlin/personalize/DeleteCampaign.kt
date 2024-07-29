// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.del_campaign.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.DeleteCampaignRequest
import kotlin.system.exitProcess
// snippet-end:[personalize.kotlin.del_campaign.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <campaignArn> 

    Where:
           campaignArn - The ARN of the campaign to delete.
     """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val campaignArn = args[0]
    deleteSpecificCampaign(campaignArn)
}

// snippet-start:[personalize.kotlin.del_campaign.main]
suspend fun deleteSpecificCampaign(campaignArnVal: String?) {
    val request =
        DeleteCampaignRequest {
            campaignArn = campaignArnVal
        }

    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        personalizeClient.deleteCampaign(request)
        println("$campaignArnVal was successfully deleted.")
    }
}
// snippet-end:[personalize.kotlin.del_campaign.main]
