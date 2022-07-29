// snippet-sourcedescription:[UpdateDomain.kt demonstrates how modify a cluster configuration of the specified domain.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon OpenSearch Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.search

// snippet-start:[opensearch.kotlin.update_domain.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.ClusterConfig
import aws.sdk.kotlin.services.opensearch.model.UpdateDomainConfigRequest
import kotlin.system.exitProcess
// snippet-end:[opensearch.kotlin.update_domain.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <domainName>

    Where:
        domainName - The name of the domain to update.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }
    val domainName = args[0]
    updateSpecificDomain(domainName)
}
// snippet-start:[opensearch.kotlin.update_domain.main]
suspend fun updateSpecificDomain(domainNameVal: String?) {

    val clusterConfigOb = ClusterConfig {
        instanceCount = 3
    }

    val request = UpdateDomainConfigRequest {
        domainName = domainNameVal
        clusterConfig = clusterConfigOb
    }

    println("Sending domain update request...")
    OpenSearchClient { region = "us-east-1" }.use { searchClient ->
        val updateResponse = searchClient.updateDomainConfig(request)
        println("Domain update response from Amazon OpenSearch Service:")
        println(updateResponse.toString())
    }
}
// snippet-end:[opensearch.kotlin.update_domain.main]
