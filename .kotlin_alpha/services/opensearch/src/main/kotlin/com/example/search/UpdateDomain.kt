//snippet-sourcedescription:[UpdateDomain.kt demonstrates how modify a cluster configuration of the specified domain.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon OpenSearch Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/26/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.search

// snippet-start:[opensearch.kotlin.update_domain.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.ClusterConfig
import aws.sdk.kotlin.services.opensearch.model.UpdateDomainConfigRequest
import aws.sdk.kotlin.services.opensearch.model.OpenSearchException
import kotlin.system.exitProcess
// snippet-end:[opensearch.kotlin.update_domain.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <domainName>

    Where:
        domainName - The name of the domain to create.

    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }
    val domainName = args[0]
    val searchClient = OpenSearchClient{region ="us-east-1"}
    updateSpecificDomain(searchClient, domainName)
}
// snippet-start:[opensearch.kotlin.update_domain.main]
suspend fun updateSpecificDomain(searchClient: OpenSearchClient, domainNameVal: String?) {
    try {
        val clusterConfigOb = ClusterConfig {
            instanceCount = 3
        }

        val updateDomainConfigRequest = UpdateDomainConfigRequest {
            domainName = domainNameVal
            clusterConfig = clusterConfigOb
        }

        println("Sending domain update request...")
        val updateResponse = searchClient.updateDomainConfig(updateDomainConfigRequest)
        println("Domain update response from Amazon OpenSearch Service:")
        println(updateResponse.toString())

    } catch (e: OpenSearchException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[opensearch.kotlin.update_domain.main]