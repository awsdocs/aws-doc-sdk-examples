//snippet-sourcedescription:[ListDomains.kt demonstrates how to list all Elasticsearch domains owned by the current user's account.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Elasticsearch Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/29/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.es

//snippet-start:[es.kotlin.list_domain.import]
import aws.sdk.kotlin.services.elasticsearchservice.ElasticsearchClient
import aws.sdk.kotlin.services.elasticsearchservice.model.ElasticsearchException
import aws.sdk.kotlin.services.elasticsearchservice.model.ListDomainNamesRequest
//snippet-end:[es.kotlin.list_domain.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val elasticsearchClient = ElasticsearchClient{region="us-east-1"}
    listAllDomains(elasticsearchClient)
}

//snippet-start:[es.kotlin.list_domain.main]
suspend fun listAllDomains(elasticsearchClient: ElasticsearchClient) {
    try {

        val response = elasticsearchClient.listDomainNames(ListDomainNamesRequest{})
        val myDomains = response.domainNames
        if (myDomains != null) {
            for (domain in myDomains) {
                System.out.println("Domain name is ${domain.domainName}.")
            }
        }

    } catch (e: ElasticsearchException) {
        println(e.message)
        elasticsearchClient.close()
        System.exit(0)
    }
}
//snippet-end:[es.kotlin.list_domain.main]