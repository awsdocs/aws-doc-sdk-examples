// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cloudtrail

// snippet-start:[cloudtrail.kotlin._selectors.import]
import aws.sdk.kotlin.services.cloudtrail.CloudTrailClient
import aws.sdk.kotlin.services.cloudtrail.model.EventSelector
import aws.sdk.kotlin.services.cloudtrail.model.PutEventSelectorsRequest
import aws.sdk.kotlin.services.cloudtrail.model.ReadWriteType
import kotlin.system.exitProcess
// snippet-end:[cloudtrail.kotlin._selectors.import]

suspend fun main(args: Array<String>) {
    val usage = """

    Usage:
        <trailName>  

    Where:
        trailName - The name of the trail. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val trailName = args[0]
    setSelector(trailName)
}

// snippet-start:[cloudtrail.kotlin._selectors.main]
suspend fun setSelector(trailNameVal: String?) {
    val selector =
        EventSelector {
            readWriteType = ReadWriteType.fromValue("All")
        }

    CloudTrailClient { region = "us-east-1" }.use { cloudTrail ->
        cloudTrail.putEventSelectors(
            PutEventSelectorsRequest {
                trailName = trailNameVal
                eventSelectors = listOf(selector)
            },
        )
    }
}
// snippet-end:[cloudtrail.kotlin._selectors.main]
