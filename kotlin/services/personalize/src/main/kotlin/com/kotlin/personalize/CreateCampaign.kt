//snippet-sourcedescription:[CreateCampaign.kt demonstrates how to create an Amazon Personalize campaign.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

//snippet-start:[personalize.kotlin.create_campaign.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.CreateCampaignRequest
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.create_campaign.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        <solutionVersionArn> <campaignName>

    Where:
         solutionVersionArn - The ARN of the solution version.
         campaignName - The name of the Amazon Personalize campaign to create.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val solutionVersionArn = args[0]
    val campaignName = args[1]
    val campaignARN = createPersonalCompaign(solutionVersionArn, campaignName)
    println("The campaign ARN is $campaignARN")
    }

//snippet-start:[personalize.kotlin.create_campaign.main]
suspend fun createPersonalCompaign(solutionVersionArnVal: String?, campaignName: String?) : String? {

      val request = CreateCampaignRequest {
          minProvisionedTps = 1
          solutionVersionArn = solutionVersionArnVal
          name = campaignName
      }

      PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        val campaignResponse = personalizeClient.createCampaign(request)
        return campaignResponse.campaignArn
     }
}
//snippet-end:[personalize.kotlin.create_campaign.main]