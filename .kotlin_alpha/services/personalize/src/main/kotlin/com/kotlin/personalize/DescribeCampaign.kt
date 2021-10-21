//snippet-sourcedescription:[DescribeCampaign.kt demonstrates how to describe an Amazon Personalize campaign.]
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

//snippet-start:[personalize.kotlin.describe_campaign.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.DescribeCampaignRequest
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.describe_campaign.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        <campaignArn>

    Where:
           campaignArn - The ARN of the campaign to describe.
     """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val campaignArn = args[0]
    val personalizeClient = PersonalizeClient{ region = "us-east-1" }
    describeSpecificCampaign(personalizeClient,campaignArn)
    personalizeClient.close()
}

//snippet-start:[personalize.kotlin.describe_campaign.main]
suspend fun describeSpecificCampaign(personalizeClient: PersonalizeClient, campaignArnVal: String?) {
        try {
            val campaignRequest = DescribeCampaignRequest {
                campaignArn = campaignArnVal
            }

            val campaignResponse = personalizeClient.describeCampaign(campaignRequest)
            val myCampaign = campaignResponse.campaign
            if (myCampaign != null) {
                println("The campaign name is ${myCampaign.name}")
                println("The campaign status is ${myCampaign.status}")
            }

        } catch (ex: PersonalizeException) {
            println(ex.message)
            personalizeClient.close()
            exitProcess(0)
        }
}
//snippet-end:[personalize.kotlin.describe_campaign.main]