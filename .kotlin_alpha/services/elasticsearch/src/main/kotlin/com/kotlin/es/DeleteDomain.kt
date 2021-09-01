//snippet-sourcedescription:[DeleteDomain.kt demonstrates how to delete an Amazon Elasticsearch domain.]
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

//snippet-start:[es.kotlin.delete_domain.import]
import aws.sdk.kotlin.services.elasticsearchservice.ElasticsearchClient
import aws.sdk.kotlin.services.elasticsearchservice.model.DeleteElasticsearchDomainRequest
import aws.sdk.kotlin.services.elasticsearchservice.model.ElasticsearchException
import kotlin.system.exitProcess
//snippet-end:[es.kotlin.delete_domain.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        <domainName> 

    Where:
        domainName - the name of the domain.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }
    val domainName = args[0]
    val elasticsearchClient = ElasticsearchClient{region="us-east-1"}
    deleteDomain(elasticsearchClient, domainName)
}

//snippet-start:[es.kotlin.delete_domain.main]
suspend fun deleteDomain(client: ElasticsearchClient, domainNameVal: String) {
    try {
        val deleteRequest = DeleteElasticsearchDomainRequest {
            domainName = domainNameVal
        }

        println("Sending domain deletion request...")
        val deleteResponse = client.deleteElasticsearchDomain(deleteRequest)
        println("Domain deletion response from Amazon Elasticsearch Service:")
        System.out.println(deleteResponse.toString())

    } catch (e: ElasticsearchException) {
        println(e.message)
        client.close()
        System.exit(0)
    }
}
//snippet-end:[es.kotlin.delete_domain.main]