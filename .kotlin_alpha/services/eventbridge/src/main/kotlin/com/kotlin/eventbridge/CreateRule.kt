// snippet-sourcedescription:[CreateRule.kt demonstrates how to create an Amazon EventBridge rule.]
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

// snippet-start:[eventbridge.kotlin._create_rule.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.PutRuleRequest
import aws.sdk.kotlin.services.eventbridge.model.EventBridgeException
import kotlin.system.exitProcess
// snippet-end:[eventbridge.kotlin._create_rule.import]

suspend fun main(args: Array<String>) {

    val usage = """
    
    Usage:
        <ruleName> 

    Where:
        ruleName - the name of the rule to create. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val ruleName = args[0]
    val eventbridgeClient = EventBridgeClient{region="us-east-1"}
    createEBRule(eventbridgeClient, ruleName)
    eventbridgeClient.close()
}

// snippet-start:[eventbridge.kotlin._create_rule.main]
suspend fun createEBRule(eventBrClient: EventBridgeClient, ruleNameVal: String?) {
        try {

            val ruleRequest = PutRuleRequest {
                 name = ruleNameVal
                 eventBusName = "default"
                 eventPattern = "{\"source\":[\"aws.s3\"],\"detail-type\":[\"AWS API Call via CloudTrail\"],\"detail\":{\"eventSource\":[\"s3.amazonaws.com\"],\"eventName\":[\"DeleteBucket\"]}}"
                 description = "A test rule created by the AWS SDK for Kotlin"
            }

            val ruleResponse = eventBrClient.putRule(ruleRequest)
            println("The ARN of the new rule is ${ruleResponse.ruleArn}")

        } catch (ex: EventBridgeException) {
            println(ex.message)
            eventBrClient.close()
            exitProcess(0)
        }
}
// snippet-end:[eventbridge.kotlin._create_rule.main]
