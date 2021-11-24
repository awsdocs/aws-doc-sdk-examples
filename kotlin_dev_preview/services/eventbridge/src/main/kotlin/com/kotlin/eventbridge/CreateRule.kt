// snippet-sourcedescription:[CreateRule.kt demonstrates how to create an Amazon EventBridge rule.]
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

// snippet-start:[eventbridge.kotlin._create_rule.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.PutRuleRequest
import kotlin.system.exitProcess
// snippet-end:[eventbridge.kotlin._create_rule.import]

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
    createEBRule(ruleName)
 }

// snippet-start:[eventbridge.kotlin._create_rule.main]
suspend fun createEBRule(ruleNameVal: String) {

     val request = PutRuleRequest {
         name = ruleNameVal
         eventBusName = "default"
         eventPattern = "{\"source\":[\"aws.s3\"],\"detail-type\":[\"AWS API Call via CloudTrail\"],\"detail\":{\"eventSource\":[\"s3.amazonaws.com\"],\"eventName\":[\"DeleteBucket\"]}}"
         description = "A test rule created by the AWS SDK for Kotlin"
      }

      EventBridgeClient { region = "us-west-2" }.use { eventBrClient ->
            val ruleResponse = eventBrClient.putRule(request)
            println("The ARN of the new rule is ${ruleResponse.ruleArn}")
      }
}
// snippet-end:[eventbridge.kotlin._create_rule.main]
