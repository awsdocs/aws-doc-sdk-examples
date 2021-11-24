//snippet-sourcedescription:[DeleteDomain.kt demonstrates how to delete an Amazon OpenSearch Service domain.]
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

// snippet-start:[opensearch.kotlin.delete_domain.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.DeleteDomainRequest
import kotlin.system.exitProcess
// snippet-end:[opensearch.kotlin.delete_domain.import]

suspend fun main(args:Array<String>) {

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