// snippet-sourcedescription:[DeleteSamplingRule.kt demonstrates how to create a rule to control sampling behavior for instrumented applications.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[AWS X-Ray Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.xray

// snippet-start:[xray.kotlin_delete_rule.import]
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.DeleteSamplingRuleRequest
import kotlin.system.exitProcess
// snippet-end:[xray.kotlin_delete_rule.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        
        Usage: 
            <ruleName>
        
        Where:
            ruleName - The name of the rule. 
                
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val ruleName = args[0]
    deleteRule(ruleName)
}

// snippet-start:[xray.kotlin_delete_rule.main]
suspend fun deleteRule(ruleNameVal: String?) {

    val ruleRequest = DeleteSamplingRuleRequest {
        ruleName = ruleNameVal
    }

    XRayClient { region = "us-east-1" }.use { xRayClient ->
        xRayClient.deleteSamplingRule(ruleRequest)
        println("$ruleNameVal was deleted")
    }
}
// snippet-end:[xray.kotlin_delete_rule.main]
