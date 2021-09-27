// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListHostedZones.kt demonstrates how to list hosted zones.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[5-10-2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

//snippet-start:[route.kotlin.list_zones.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.HostedZone
import aws.sdk.kotlin.services.route53.model.ListHostedZonesRequest
import aws.sdk.kotlin.services.route53.model.Route53Exception
import kotlin.system.exitProcess
//snippet-end:[route.kotlin.list_zones.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val route53Client = Route53Client{region = "AWS_GLOBAL"}
    listZones(route53Client)
    route53Client.close()
}

//snippet-start:[route.kotlin.list_zones.main]
suspend fun listZones(route53Client: Route53Client) {
        try {
            val zonesResponse = route53Client.listHostedZones(ListHostedZonesRequest{})
            val checklist: List<HostedZone>? = zonesResponse.hostedZones
            if (checklist != null) {
                for (check in checklist) {
                    println("The name is ${check.name}")
                }
            }

        } catch (e: Route53Exception) {
            println(e.message)
            exitProcess(0)
        }
 }
//snippet-end:[route.kotlin.list_zones.main]