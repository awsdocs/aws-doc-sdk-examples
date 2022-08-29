// snippet-sourcedescription:[ListRules.kt demonstrates how to list your Amazon EventBridge rules.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EventBridge]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.eventbridge

// snippet-start:[eventbridge.kotlin._list_rules.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.ListRulesRequest
// snippet-end:[eventbridge.kotlin._list_rules.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllRules()
}

// snippet-start:[eventbridge.kotlin._list_rules.main]
suspend fun listAllRules() {

    val request = ListRulesRequest {
        eventBusName = "default"
        limit = 10
    }

    EventBridgeClient { region = "us-west-2" }.use { eventBrClient ->
        val response = eventBrClient.listRules(request)
        response.rules?.forEach { rule ->
            println("The rule name is ${rule.name}")
            println("The rule ARN is ${rule.arn}")
        }
    }
}
// snippet-end:[eventbridge.kotlin._list_rules.main]
