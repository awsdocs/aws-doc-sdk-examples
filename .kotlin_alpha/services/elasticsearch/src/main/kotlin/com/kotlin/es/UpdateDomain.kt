//snippet-sourcedescription:[UpdateDomain.kt demonstrates how to update an Amazon Elasticsearch domain.]
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

//snippet-start:[es.kotlin.update_domain.import]
import aws.sdk.kotlin.services.elasticsearchservice.ElasticsearchClient
import aws.sdk.kotlin.services.elasticsearchservice.model.ElasticsearchClusterConfig
import aws.sdk.kotlin.services.elasticsearchservice.model.UpdateElasticsearchDomainConfigRequest
import aws.sdk.kotlin.services.elasticsearchservice.model.ElasticsearchException
import kotlin.system.exitProcess
//snippet-end:[es.kotlin.update_domain.import]

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
    updateSpeicficDomain(elasticsearchClient, domainName)
    elasticsearchClient.close()
}

//snippet-start:[es.kotlin.update_domain.main]
suspend fun updateSpeicficDomain(elasticsearchClient: ElasticsearchClient, domainNameVal: String) {

    try {
        val config= ElasticsearchClusterConfig {
            instanceCount = 3
        }

        val clusterConfig = UpdateElasticsearchDomainConfigRequest {
             elasticsearchClusterConfig = config
             domainName = domainNameVal
        }

        val response = elasticsearchClient.updateElasticsearchDomainConfig(clusterConfig)
        println("Domain update response from Amazon Elasticsearch Service:")
        println(response.toString())

    } catch (e: ElasticsearchException) {
        println(e.message)
        elasticsearchClient.close()
        System.exit(0)
    }
}
//snippet-end:[es.kotlin.update_domain.main]