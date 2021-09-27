//snippet-sourcedescription:[FindReservedNodeOffer.kt demonstrates how to find additional Amazon Redshift nodes for purchase.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/31/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.redshift

// snippet-start:[redshift.kotlin._nodes.import]
import aws.sdk.kotlin.services.redshift.RedshiftClient
import aws.sdk.kotlin.services.redshift.model.DescribeReservedNodesRequest
import aws.sdk.kotlin.services.redshift.model.RedshiftException
import aws.sdk.kotlin.services.redshift.model.ReservedNodeOffering
import aws.sdk.kotlin.services.redshift.model.ReservedNode
import aws.sdk.kotlin.services.redshift.model.DescribeReservedNodeOfferingsRequest
import kotlin.system.exitProcess
// snippet-end:[redshift.kotlin._nodes.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val redshiftClient = RedshiftClient { region = "us-west-2" }
    listReservedNodes(redshiftClient)
    findReservedNodeOffer(redshiftClient)
    redshiftClient.close()
}

// snippet-start:[redshift.kotlin._nodes.main]
suspend fun listReservedNodes(redshiftClient: RedshiftClient) {
    try {
        val reservedNodesResponse = redshiftClient.describeReservedNodes(DescribeReservedNodesRequest{})
        println("Listing nodes already purchased.")

        for (node in reservedNodesResponse.reservedNodes!!) {
            printReservedNodeDetails(node)
        }

    } catch (e: RedshiftException) {
        println(e.message)
        redshiftClient.close()
        exitProcess(0)
    }
}

suspend fun findReservedNodeOffer(redshiftClient: RedshiftClient) {
    try {
        val nodeTypeToPurchase = "dc2.large"
        val fixedPriceLimit = 10000.00
        val matchingNodes = mutableListOf<ReservedNodeOffering>()

        val response = redshiftClient.describeReservedNodeOfferings(DescribeReservedNodeOfferingsRequest{})
        var count = 0
        println("Finding nodes to purchase.")
        for (offering in response.reservedNodeOfferings!!) {

            if (offering.nodeType.equals(nodeTypeToPurchase)) {
                if (offering.fixedPrice < fixedPriceLimit) {
                    matchingNodes.add(offering)
                    printOfferingDetails(offering)
                    count += 1
                }
            }
        }
        if (count == 0) {
            println("No reserved node offering matches found.")
        } else {
            println("Found $count matches.")
        }

    } catch (e: RedshiftException) {
        println(e.message)
        redshiftClient.close()
        exitProcess(0)
    }
}

private fun printReservedNodeDetails(node: ReservedNode) {
    println("Purchased Node Details:")
    println("Id: ${node.reservedNodeOfferingId}")
    println("State: ${node.state}")
    println("Node Type: ${node.nodeType}")
    println("Start Time: ${node.startTime}")
    println("Fixed Price: ${node.fixedPrice}")
    println("Offering Type: ${node.offeringType}")
    println("Duration: ${node.duration}")
}

private fun printOfferingDetails(offering: ReservedNodeOffering) {

    println("Offering Match:")
    println("Id: ${offering.reservedNodeOfferingId}")
    println("Node Type: ${offering.nodeType}")
    println("Fixed Price: ${offering.fixedPrice}")
    println("Offering Type: ${offering.offeringType}")
    println("Duration: ${offering.duration}")
}
// snippet-end:[redshift.kotlin._nodes.main]