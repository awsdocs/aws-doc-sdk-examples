//snippet-sourcedescription:[CreateSamplingRule.kt demonstrates how to create a rule to control sampling behavior for instrumented applications.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS X-Ray Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.xray

// snippet-start:[xray.kotlin_create_rule.import]
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.CreateSamplingRuleRequest
import aws.sdk.kotlin.services.xray.model.CreateSamplingRuleResponse
import aws.sdk.kotlin.services.xray.model.SamplingRule
import kotlin.system.exitProcess
// snippet-end:[xray.kotlin_create_rule.import]

suspend fun main(args:Array<String>) {

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
    createRule(ruleName)
}

// snippet-start:[xray.kotlin_create_rule.main]
suspend fun createRule(ruleNameVal: String?) {


        val rule = SamplingRule {
            ruleName = ruleNameVal
                priority = 1
                httpMethod = "*"
                serviceType = "*"
                serviceName = "*"
                urlPath = "*"
                version = 1
                host = "*"
                resourceArn = "*"
        }

        val ruleRequest = CreateSamplingRuleRequest {
            samplingRule = rule
        }

        XRayClient { region = "us-east-1" }.use { xRayClient ->
            val ruleResponse: CreateSamplingRuleResponse = xRayClient.createSamplingRule(ruleRequest)
            println("The ARN of the new rule is ${ruleResponse.samplingRuleRecord?.samplingRule?.ruleArn}")
        }
 }
// snippet-end:[xray.kotlin_create_rule.main]