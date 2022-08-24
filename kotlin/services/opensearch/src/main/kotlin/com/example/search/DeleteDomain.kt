// snippet-sourcedescription:[DeleteDomain.kt demonstrates how to delete an Amazon OpenSearch Service domain.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon OpenSearch Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.search

// snippet-start:[opensearch.kotlin.delete_domain.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.DeleteDomainRequest
import kotlin.system.exitProcess
// snippet-end:[opensearch.kotlin.delete_domain.import]

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
        domainName - The name of the domain to delete.

    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }
    val domainName = args[0]
    deleteSpecificDomain(domainName)
}

// snippet-start:[opensearch.kotlin.delete_domain.main]
suspend fun deleteSpecificDomain(domainNameVal: String) {

    val request = DeleteDomainRequest {
        domainName = domainNameVal
    }
    OpenSearchClient { region = "us-east-1" }.use { searchClient ->
        searchClient.deleteDomain(request)
        println("$domainNameVal was successfully deleted.")
    }
}
// snippet-end:[opensearch.kotlin.delete_domain.main]
