// snippet-sourcedescription:[ListRules.java demonstrates how to list your Amazon EventBridge rules.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EventBridge]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[03/04/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.eventbridge

// snippet-start:[eventbridge.kotlin._list_rules.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.ListRulesRequest
import aws.sdk.kotlin.services.eventbridge.model.EventBridgeException
import kotlin.system.exitProcess
// snippet-end:[eventbridge.kotlin._list_rules.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val eventbridgeClient = EventBridgeClient{region="us-east-1"}
    listAllRules(eventbridgeClient)
    eventbridgeClient.close()
}

// snippet-start:[eventbridge.kotlin._list_rules.main]
suspend  fun listAllRules(eventBrClient: EventBridgeClient) {
        try {
            val rulesRequest = ListRulesRequest {
                eventBusName = "default"
                limit = 10
            }

            val response = eventBrClient.listRules(rulesRequest)
            val rules = response.rules
            if (rules != null) {
                for (rule in rules) {
                    println("The rule name is ${rule.name}")
                    println("The rule ARN is ${rule.arn}")
                }
            }

        } catch (ex: EventBridgeException) {
            println(ex.message)
            eventBrClient.close()
            exitProcess(0)
        }
 }
// snippet-end:[eventbridge.kotlin._list_rules.main]