//snippet-sourcedescription:[CreateCampaign.kt demonstrates how to create an Amazon Pinpoint campaign.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

//snippet-start:[pinpoint.kotlin.createcampaign.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.Schedule
import aws.sdk.kotlin.services.pinpoint.model.Message
import aws.sdk.kotlin.services.pinpoint.model.Action
import aws.sdk.kotlin.services.pinpoint.model.MessageConfiguration
import aws.sdk.kotlin.services.pinpoint.model.WriteCampaignRequest
import aws.sdk.kotlin.services.pinpoint.model.CreateCampaignRequest
import aws.sdk.kotlin.services.pinpoint.model.CreateCampaignResponse
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.createcampaign.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: <appId> <segmentId>

    Where:
        appId - the ID of the application to create the campaign in.
        segmentId - the ID of the segment to create the campaign from.
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val appId = args[0]
    val segmentId = args[1]
    createPinCampaign(appId, segmentId)
}

//snippet-start:[pinpoint.kotlin.createcampaign.main]
suspend fun createPinCampaign(appId: String, segmentIdVal: String) {

            val scheduleOb = Schedule {
                startTime = "IMMEDIATE"
            }

            val defaultMessageOb = Message {
                action = Action.OpenApp
                body = "My message body"
                title = "My message title"
            }

            val messageConfigurationOb = MessageConfiguration {
                defaultMessage = defaultMessageOb
            }

            val writeCampaign = WriteCampaignRequest {
                description = "My description"
                schedule = scheduleOb
                name = "MyCampaign"
                segmentId = segmentIdVal
                messageConfiguration= messageConfigurationOb
            }

            PinpointClient { region = "us-west-2" }.use { pinpoint ->
             val result: CreateCampaignResponse = pinpoint.createCampaign(CreateCampaignRequest {
                 applicationId = appId
                 writeCampaignRequest = writeCampaign
             })
             println("Campaign ID is ${result.campaignResponse?.id}")
        }
 }
//snippet-end:[pinpoint.kotlin.createcampaign.main]