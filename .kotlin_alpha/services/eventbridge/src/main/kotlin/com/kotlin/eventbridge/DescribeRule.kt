// snippet-sourcedescription:[DescribeRule.kt demonstrates how to describe an Amazon EventBridge rule.]
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

// snippet-start:[eventbridge.kotlin._describe_rule.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.DescribeRuleRequest
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
        ruleName - the name of the rule. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val ruleName = args[0]
    describeSpecificRule(ruleName)
    }

// snippet-start:[eventbridge.kotlin._describe_rule.main]
suspend fun describeSpecificRule(ruleName: String) {

           val request = DescribeRuleRequest {
               name = ruleName
               eventBusName = "default"
           }

           EventBridgeClient { region = "us-west-2" }.use { eventBrClient ->
              val ruleResponse = eventBrClient.describeRule(request)
              println("The rule ARN is ${ruleResponse.arn}")
              println("The rule description is ${ruleResponse.description}")
           }
}
// snippet-end:[eventbridge.kotlin._describe_rule.main]
