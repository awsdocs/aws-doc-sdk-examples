//snippet-sourcedescription:[ListDomainNames.kt demonstrates how to list Amazon OpenSearch Service domains.]
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

// snippet-start:[opensearch.kotlin.list_domains.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.ListDomainNamesResponse
import aws.sdk.kotlin.services.opensearch.model.ListDomainNamesRequest
// snippet-end:[opensearch.kotlin.list_domains.import]

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