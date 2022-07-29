// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteHostedZone.kt demonstrates how to delete a hosted zone.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Route 53]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.route

// snippet-start:[route53.kotlin.delete_hosted_zone.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.DeleteHostedZoneRequest
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.delete_hosted_zone.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <hostedZoneId> 

    Where:
        hostedZoneId - The hosted zone id.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val hostedZoneId = args[0]
    delHostedZone(hostedZoneId)
}

// snippet-start:[route53.kotlin.delete_hosted_zone.main]
suspend fun delHostedZone(hostedZoneId: String?) {

    val deleteHostedZoneRequestRequest = DeleteHostedZoneRequest {
        id = hostedZoneId
    }

    Route53Client { region = "AWS_GLOBAL" }.use { route53Client ->
        route53Client.deleteHostedZone(deleteHostedZoneRequestRequest)
        println("The hosted zone with id $hostedZoneId was deleted")
    }
}
// snippet-end:[route53.kotlin.delete_hosted_zone.main]
