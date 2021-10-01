// snippet-sourcedescription:[DescribeRule.java demonstrates how to describe an Amazon EventBridge rule.]
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

// snippet-start:[eventbridge.kotlin._describe_rule.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.DescribeRuleRequest
import aws.sdk.kotlin.services.eventbridge.model.EventBridgeException
import kotlin.system.exitProcess
// snippet-end:[eventbridge.kotlin._describe_rule.import]

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
        ruleName - the name of the rule to create. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val ruleName = args[0]
    val eventbridgeClient = EventBridgeClient{region="us-east-1"}
    describeSpecificRule(eventbridgeClient, ruleName)
    eventbridgeClient.close()
}

// snippet-start:[eventbridge.kotlin._describe_rule.main]
suspend fun describeSpecificRule(eventBrClient: EventBridgeClient, ruleName: String?) {
        try {

            val ruleRequest = DescribeRuleRequest {
                name = ruleName
                eventBusName = "default"
            }

            val ruleResponse = eventBrClient.describeRule(ruleRequest)
            println("The rule ARN is ${ruleResponse.arn}")
            println("The rule description is ${ruleResponse.description}")

        } catch (ex: EventBridgeException) {
            println(ex.message)
            eventBrClient.close()
            exitProcess(0)
        }
}
// snippet-end:[eventbridge.kotlin._describe_rule.main]
