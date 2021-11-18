// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteHostedZone.kt demonstrates how to delete a hosted zone.]
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

// snippet-start:[route53.kotlin.delete_hosted_zone.import]
import aws.sdk.kotlin.services.route53.Route53Client
import aws.sdk.kotlin.services.route53.model.DeleteHostedZoneRequest
import aws.sdk.kotlin.services.route53.model.Route53Exception
import kotlin.system.exitProcess
// snippet-end:[route53.kotlin.delete_hosted_zone.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <hostedZoneId> 

    Where:
        hostedZoneId - the hosted zone id.
    """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
      }

    val hostedZoneId = args[0]
    val route53Client = Route53Client{region = "AWS_GLOBAL"}
    delHostedZone(route53Client, hostedZoneId)
    route53Client.close()
}

// snippet-start:[route53.kotlin.delete_hosted_zone.main]
suspend fun delHostedZone(route53Client: Route53Client, hostedZoneId: String?) {
        try {
            val deleteHostedZoneRequestRequest = DeleteHostedZoneRequest {
                id = hostedZoneId
            }

            route53Client.deleteHostedZone(deleteHostedZoneRequestRequest)
            println("The hosted zone with id $hostedZoneId was deleted")

        } catch (e: Route53Exception) {
            System.err.println(e.message)
            exitProcess(0)
        }
}
// snippet-end:[route53.kotlin.delete_hosted_zone.main]