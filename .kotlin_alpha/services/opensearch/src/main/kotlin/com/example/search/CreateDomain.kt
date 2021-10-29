//snippet-sourcedescription:[CreateDomain.kt demonstrates how to create a new Amazon OpenSearch Service domain.]
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

// snippet-start:[opensearch.kotlin.create_domain.import]
import aws.sdk.kotlin.services.opensearch.OpenSearchClient
import aws.sdk.kotlin.services.opensearch.model.ClusterConfig
import aws.sdk.kotlin.services.opensearch.model.OpenSearchPartitionInstanceType
import aws.sdk.kotlin.services.opensearch.model.EbsOptions
import aws.sdk.kotlin.services.opensearch.model.VolumeType
import aws.sdk.kotlin.services.opensearch.model.NodeToNodeEncryptionOptions
import aws.sdk.kotlin.services.opensearch.model.CreateDomainRequest
import aws.sdk.kotlin.services.opensearch.model.OpenSearchException
import kotlin.system.exitProcess
// snippet-end:[opensearch.kotlin.create_domain.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <domainName>

    Where:
        domainName - The name of the domain to create.

    """

    if (args.size != 1) {
        println(usage)
        exitProcess(1)
    }
    val domainName = args[0]
    val searchClient = OpenSearchClient{region ="us-east-1"}
    createNewDomain(searchClient, domainName)
}

// snippet-start:[opensearch.kotlin.create_domain.main]
suspend fun createNewDomain(searchClient: OpenSearchClient, domainNameVal: String?) {

    try {
        val clusterConfigOb = ClusterConfig {
            dedicatedMasterEnabled = true
            dedicatedMasterCount = 3
            dedicatedMasterType = OpenSearchPartitionInstanceType.fromValue("t2.small.search")
            instanceType = OpenSearchPartitionInstanceType.fromValue("t2.small.search")
            instanceCount = 5
        }

        val ebsOptionsOb = EbsOptions {
            ebsEnabled = true
            volumeSize = 10
            volumeType = VolumeType.Gp2
        }

        val encryptionOptionsOb = NodeToNodeEncryptionOptions {
            enabled = true
        }

        val domainRequest= CreateDomainRequest {
            domainName = domainNameVal
            engineVersion = "OpenSearch_1.0"
            clusterConfig = clusterConfigOb
            ebsOptions = ebsOptionsOb
            nodeToNodeEncryptionOptions = encryptionOptionsOb
        }

        println("Sending domain creation request...")
        val createResponse = searchClient.createDomain(domainRequest)
        println("Domain status is ${createResponse.domainStatus.toString()}")
        println("Domain Id is ${createResponse.domainStatus?.domainId}")

    } catch (e: OpenSearchException) {
        System.err.println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[opensearch.kotlin.create_domain.main]
