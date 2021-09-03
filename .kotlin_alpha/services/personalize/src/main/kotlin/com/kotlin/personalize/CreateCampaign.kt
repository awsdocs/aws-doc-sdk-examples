//snippet-sourcedescription:[CreateCampaign.kt demonstrates how to create an Amazon Personalize campaign.]
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

//snippet-start:[personalize.kotlin.create_campaign.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.CreateCampaignRequest
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
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
    val personalizeClient = PersonalizeClient{ region = "us-east-1" }
    val campaignARN = createPersonalCompaign(personalizeClient,solutionVersionArn, campaignName)
    println("The campaign ARN is $campaignARN")
    personalizeClient.close()
}

//snippet-start:[personalize.kotlin.create_campaign.main]
suspend fun createPersonalCompaign(personalizeClient: PersonalizeClient, solutionVersionArnVal: String?, campaignName: String?) : String? {
    try {
        val createCampaignRequest = CreateCampaignRequest {
            minProvisionedTps = 1
            solutionVersionArn = solutionVersionArnVal
            name = campaignName
        }
        val campaignResponse = personalizeClient.createCampaign(createCampaignRequest)
        return campaignResponse.campaignArn

    } catch (ex: PersonalizeException) {
        println(ex.message)
        personalizeClient.close()
        exitProcess(0)
    }
}
//snippet-end:[personalize.kotlin.create_campaign.main]