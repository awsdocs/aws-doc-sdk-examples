//snippet-sourcedescription:[CreateDomain.kt demonstrates how to create an Amazon Elasticsearch domain.]
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

//snippet-start:[es.kotlin.create_domain.import]
import aws.sdk.kotlin.services.elasticsearchservice.ElasticsearchClient
import aws.sdk.kotlin.services.elasticsearchservice.model.ElasticsearchClusterConfig
import aws.sdk.kotlin.services.elasticsearchservice.model.EsPartitionInstanceType
import aws.sdk.kotlin.services.elasticsearchservice.model.EbsOptions
import aws.sdk.kotlin.services.elasticsearchservice.model.VolumeType
import aws.sdk.kotlin.services.elasticsearchservice.model.NodeToNodeEncryptionOptions
import aws.sdk.kotlin.services.elasticsearchservice.model.CreateElasticsearchDomainRequest
import aws.sdk.kotlin.services.elasticsearchservice.model.ElasticsearchException
import kotlin.system.exitProcess
//snippet-end:[es.kotlin.create_domain.import]

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
    createDomain(elasticsearchClient, domainName)
}

//snippet-start:[es.kotlin.create_domain.main]
suspend fun createDomain(elasticsearchClient: ElasticsearchClient, domainNameVal: String) {

    try {
        val pol = """{
        "Version": "2012-10-17",
        "Statement": [
        {
        "Effect": "Deny",
        "Principal": {
        "AWS": "*"
        },
        "Action": "es:ESHttp*",
        "Resource": "arn:aws:es:us-east-1:xxxxx8047983:domain/my-test-domain"
        }
    ]
    }"""
        val config = ElasticsearchClusterConfig {
             dedicatedMasterEnabled = true
             dedicatedMasterCount = 3
             dedicatedMasterType = EsPartitionInstanceType.T2SmallElasticsearch
             instanceType = EsPartitionInstanceType.T2SmallElasticsearch
             instanceCount = 5
        }

        val ebsOptionsOb = EbsOptions {
             ebsEnabled = true
             volumeSize = 10
             volumeType = VolumeType.Gp2
        }

        val encryptionOptions = NodeToNodeEncryptionOptions {
            enabled = false
        }

        val createElasticsearchDomainRequest = CreateElasticsearchDomainRequest {
            domainName = domainNameVal
            elasticsearchVersion = "6.3"
            elasticsearchClusterConfig = config
            ebsOptions = ebsOptionsOb
            accessPolicies = pol
            nodeToNodeEncryptionOptions = encryptionOptions
        }

        val response = elasticsearchClient.createElasticsearchDomain(createElasticsearchDomainRequest)
        println("Domain creation response from Amazon Elasticsearch Service:")
        println(response.domainStatus.toString())

    } catch (e: ElasticsearchException) {
        println(e.message)
        elasticsearchClient.close()
        System.exit(0)
    }
}
//snippet-end:[es.kotlin.create_domain.main]
