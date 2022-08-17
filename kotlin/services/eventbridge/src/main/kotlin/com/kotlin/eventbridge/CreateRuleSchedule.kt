// snippet-sourcedescription:[CreateRuleSchedule.kt demonstrates how to create an Amazon EventBridge rule that has a target and runs on a schedule.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EventBridge]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.eventbridge

// snippet-start:[eventbridge.kotlin._create_schedule_rule.import]
import aws.sdk.kotlin.services.eventbridge.EventBridgeClient
import aws.sdk.kotlin.services.eventbridge.model.PutRuleRequest
import aws.sdk.kotlin.services.eventbridge.model.PutTargetsRequest
import aws.sdk.kotlin.services.eventbridge.model.RuleState
import aws.sdk.kotlin.services.eventbridge.model.Target
// snippet-end:[eventbridge.kotlin._create_schedule_rule.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
        Usage:
            <ruleName> <cronExpression> <lambdaARN> <json> <targetId>
        Where:
            ruleName - The name of the rule to create. 
            cronExpression - The scheduling expression. For example, rate(5 minutes)   
            lambdaARN - The ARN value of a Lambda function that is the target    
            json  - The JSON to pass the Lambda function    
            targetId - The ID of the target within the specified rule 
    """

    if (args.size != 5) {
        println(usage)
        System.exit(1)
    }

    val ruleName = args[0]
    val cronExpression = args[1]
    val lambdaARN = args[2]
    val json = args[3]
    val targetId = args[4]

    createScRule(ruleName, cronExpression)
    putRuleTarget(ruleName, lambdaARN, json, targetId)
}

// snippet-start:[eventbridge.kotlin._create_schedule_rule.main]
suspend fun createScRule(ruleName: String?, cronExpression: String?) {
    val ruleRequest = PutRuleRequest {
        name = ruleName
        eventBusName = "default"
        scheduleExpression = cronExpression
        state = RuleState.Enabled
        description = "A test rule that runs on a schedule created by the Kotlin API"
    }

    EventBridgeClient { region = "us-west-2" }.use { eventBrClient ->
        val ruleResponse = eventBrClient.putRule(ruleRequest)
        println("The ARN of the new rule is ${ruleResponse.ruleArn}")
    }
}
// snippet-end:[eventbridge.kotlin._create_schedule_rule.main]

// snippet-start:[eventbridge.kotlin._create_schedule_rule_target.main]
suspend fun putRuleTarget(ruleName: String?, lambdaARN: String, json: String?, targetId: String) {
    val lambdaTarget = Target {
        arn = lambdaARN
        id = targetId
        input = json
    }

    val targetsRequest = PutTargetsRequest {
        eventBusName = "default"
        rule = ruleName
        targets = listOf(lambdaTarget)
    }

    EventBridgeClient { region = "us-west-2" }.use { eventBrClient ->
        eventBrClient.putTargets(targetsRequest)
        println("The $lambdaARN was successfully used as a target")
    }
}
// snippet-end:[eventbridge.kotlin._create_schedule_rule_target.main]
