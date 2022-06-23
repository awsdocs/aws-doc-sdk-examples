//snippet-sourcedescription:[PutRule.kt demonstrates how to create an Amazon CloudWatch event-routing rule.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.put_rule.import]
import aws.sdk.kotlin.services.cloudwatchevents.CloudWatchEventsClient
import aws.sdk.kotlin.services.cloudwatchevents.model.PutRuleRequest
import aws.sdk.kotlin.services.cloudwatchevents.model.RuleState
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.put_rule.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <ruleName> <roleArn>

    Where:
       ruleName - a rule name (for example, myrule).
       roleArn - a role ARN value (for example, arn:aws:iam::xxxxxx047983:user/MyUser).
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
     }

    val ruleName = args[0]
    val roleArn = args[1]
    putCWRule(ruleName, roleArn)
}
// snippet-start:[cloudwatch.kotlin.put_rule.main]
suspend fun putCWRule(ruleNameVal: String, roleArnVal: String) {

    val request =  PutRuleRequest {
        name = ruleNameVal
        roleArn = roleArnVal
        scheduleExpression = "rate(5 minutes)"
        state = RuleState.Enabled
    }

    CloudWatchEventsClient { region = "us-east-1" }.use { cwe ->
        val response = cwe.putRule(request)
        println( "Successfully created CloudWatch events ${roleArnVal}rule with ARN ${response.ruleArn}")
    }
}
// snippet-end:[cloudwatch.kotlin.put_rule.main]