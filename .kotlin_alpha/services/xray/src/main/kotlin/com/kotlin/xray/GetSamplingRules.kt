//snippet-sourcedescription:[GetSamplingRules.kt demonstrates how to retrieve sampling rules.]
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

// snippet-start:[xray.kotlin_get_rules.import]
import aws.sdk.kotlin.services.xray.XRayClient
import aws.sdk.kotlin.services.xray.model.GetSamplingRulesRequest
// snippet-end:[xray.kotlin_get_rules.import]

suspend fun main() {

    getRules()
}

// snippet-start:[xray.kotlin_get_rules.main]
suspend fun getRules() {

        XRayClient { region = "us-east-1" }.use { xRayClient ->
            val response = xRayClient.getSamplingRules( GetSamplingRulesRequest{} )
            response.samplingRuleRecords?.forEach { record ->
                    println("The rule name is ${record.samplingRule?.ruleName}")
                    println("The related service is: ${record.samplingRule?.serviceName}")
            }

        }
 }
// snippet-end:[xray.kotlin_get_rules.main]