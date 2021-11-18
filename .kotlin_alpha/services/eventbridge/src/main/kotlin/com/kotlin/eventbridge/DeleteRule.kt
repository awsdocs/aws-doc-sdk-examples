// snippet-sourcedescription:[DeleteRule.java demonstrates how to delete an Amazon EventBridge rule.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EventBridge]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/04/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.eventbridge

// snippet-start:[eventbridge.kotlin._delete_rule.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.DeleteRuleRequest
import aws.sdk.kotlin.services.eventbridge.model.DisableRuleRequest
import aws.sdk.kotlin.services.eventbridge.model.EventBridgeException
import kotlin.system.exitProcess
// snippet-end:[eventbridge.kotlin._delete_rule.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    
    Usage:
        <ruleName> 

    Where:
        ruleName - the name of the rule. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val ruleName = args[0]
    val eventbridgeClient = EventBridgeClient{region="us-east-1"}
    deleteEBRule(eventbridgeClient, ruleName)
    eventbridgeClient.close()
}

// snippet-start:[eventbridge.kotlin._delete_rule.main]
suspend fun deleteEBRule(eventBrClient: EventBridgeClient, ruleName: String) {
        try {

            // Disable the rule - an Enabled Rule cannot be deleted.
            val disableRuleRequest = DisableRuleRequest {
                name = ruleName
                eventBusName = "default"
            }

            eventBrClient.disableRule(disableRuleRequest)
            val ruleRequest = DeleteRuleRequest {
                name = ruleName
                eventBusName = "default"
            }

            eventBrClient.deleteRule(ruleRequest)
            println("Rule $ruleName was successfully deleted!")

        } catch (ex: EventBridgeException) {
            println(ex.message)
            eventBrClient.close()
            exitProcess(0)
        }
}
// snippet-end:[eventbridge.kotlin._delete_rule.main]
