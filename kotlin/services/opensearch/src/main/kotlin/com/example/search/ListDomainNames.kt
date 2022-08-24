// snippet-sourcedescription:[ListDomainNames.kt demonstrates how to list Amazon OpenSearch Service domains.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon OpenSearch Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.search

// snippet-start:[opensearch.kotlin.list_domains.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.ListDomainNamesRequest
import aws.sdk.kotlin.services.opensearch.model.ListDomainNamesResponse
// snippet-end:[opensearch.kotlin.list_domains.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main() {
    listAllDomains()
}

// snippet-start:[opensearch.kotlin.list_domains.main]
suspend fun listAllDomains() {

    OpenSearchClient { region = "us-east-1" }.use { searchClient ->
        val response: ListDomainNamesResponse = searchClient.listDomainNames(ListDomainNamesRequest {})
        response.domainNames?.forEach { domain ->
            println("Domain name is " + domain.domainName)
        }
    }
}
// snippet-end:[opensearch.kotlin.list_domains.main]
