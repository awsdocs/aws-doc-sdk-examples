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
import aws.sdk.kotlin.services.opensearch.model.OpenSearchException
import aws.sdk.kotlin.services.opensearch.model.ListDomainNamesRequest
import kotlin.system.exitProcess
// snippet-end:[opensearch.kotlin.list_domains.import]

suspend fun main() {

  val searchClient = OpenSearchClient{region ="us-east-1"}
    listAllDomains(searchClient)
}

// snippet-start:[opensearch.kotlin.list_domains.main]
suspend fun listAllDomains(searchClient: OpenSearchClient) {

    try {
        val response: ListDomainNamesResponse = searchClient.listDomainNames(ListDomainNamesRequest {})
        val domainInfoList = response.domainNames
        if (domainInfoList != null) {
            for (domain in domainInfoList)
                println("Domain name is " + domain.domainName)
        }
    } catch (e: OpenSearchException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[opensearch.kotlin.list_domains.main]