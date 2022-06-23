// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListHostedZones.kt demonstrates how to list hosted zones.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/5/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

//snippet-start:[route.kotlin.list_zones.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.ListHostedZonesRequest
//snippet-end:[route.kotlin.list_zones.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listZones()
}

//snippet-start:[route.kotlin.list_zones.main]
suspend fun listZones() {

        Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
            val response = route53Client.listHostedZones(ListHostedZonesRequest{})
            response.hostedZones?.forEach { check ->
                 println("The name is ${check.name}")
            }
        }
 }
//snippet-end:[route.kotlin.list_zones.main]