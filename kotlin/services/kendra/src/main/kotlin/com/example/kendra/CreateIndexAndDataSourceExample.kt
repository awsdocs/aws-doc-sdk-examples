//snippet-sourcedescription:[CreateIndexAndDataSourceExample.kt demonstrates how to create an Amazon Kendra index and data source.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kendra]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/10/2022]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra

// snippet-start:[kendra.kotlin.index.import]
import aws.sdk.kotlin.services.kendra.KendraClient
import aws.sdk.kotlin.services.kendra.model.DescribeIndexRequest
import aws.sdk.kotlin.services.kendra.model.CreateDataSourceRequest
import aws.sdk.kotlin.services.kendra.model.DataSourceType
import aws.sdk.kotlin.services.kendra.model.DataSourceConfiguration
import aws.sdk.kotlin.services.kendra.model.S3DataSourceConfiguration
import aws.sdk.kotlin.services.kendra.model.DescribeDataSourceRequest
import aws.sdk.kotlin.services.kendra.model.StartDataSourceSyncJobRequest
import aws.sdk.kotlin.services.kendra.model.DataSourceStatus
import aws.sdk.kotlin.services.kendra.model.CreateIndexRequest
import aws.sdk.kotlin.services.kendra.model.IndexStatus
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
// snippet-end:[kendra.kotlin.index.import]

/**
 Before running this Kotlin code example, set up your development environment,
 including your credentials.

 For more information, see the following documentation topic:
 https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <indexDescription> <indexName> <indexRoleArn> <dataSourceRoleArn> <dataSourceName> <dataSourceDescription> <s3BucketName>

        Where:
            indexDescription - A description for the index.
            indexName - The name for the new index.
            indexRoleArn - An AWS Identity and Access Management (IAM) role that gives Amazon Kendra permissions to access your Amazon CloudWatch logs and metrics.
            dataSourceRoleArn - The Amazon Resource Name (ARN) of an IAM role with permissions to access the data source.
            dataSourceName - The name for the new data source.
            dataSourceDescription - A description for the data source.
            s3BucketName - An Amazon S3 bucket used as your data source.
    """

    if (args.size != 7) {
        println(usage)
        exitProcess(1)
    }

    val indexDescription = args[0]
    val indexName = args[1]
    val indexRoleArn = args[2]
    val dataSourceRoleArn = args[3]
    val dataSourceName = args[4]
    val dataSourceDescription = args[5]
    val s3BucketName = args[6]

    val indexId = createIndex(indexDescription, indexName, indexRoleArn)
    println("The index is is $indexId")
    val dsIdValue = createDataSource(s3BucketName, dataSourceName, dataSourceDescription, indexId, dataSourceRoleArn)
    startDataSource(indexId,dsIdValue)
}
// snippet-start:[kendra.kotlin.index.main]
suspend fun createIndex( indexDescription: String, indexName: String, indexRoleArn: String): String {

        println("Creating an index named $indexName")
        val createIndexRequest = CreateIndexRequest {
            description = indexDescription
            name = indexName
            roleArn = indexRoleArn
        }

        KendraClient { region = "us-east-1" }.use { kendra ->
            val createIndexResponse = kendra.createIndex(createIndexRequest)
            val indexId = createIndexResponse.id
            println("Waiting until the index with index ID $indexId is created.")

            while (true) {
                val describeIndexRequest = DescribeIndexRequest {
                    id = indexId
                }
                val describeIndexResponse = kendra.describeIndex(describeIndexRequest)
                val status = describeIndexResponse.status
                println("Status is $status")
                if (status !== IndexStatus.Creating) {
                    break
                }
                TimeUnit.SECONDS.sleep(60)
            }
            return indexId.toString()
        }
}
// snippet-end:[kendra.kotlin.index.main]


// snippet-start:[kendra.kotlin.datasource.main]
suspend fun createDataSource(s3BucketName: String?, dataSourceName: String?, dataSourceDescription: String?, indexIdVal: String?, dataSourceRoleArn: String?): String {
    println("Creating an S3 data source")

        val createDataSourceRequest = CreateDataSourceRequest {
            indexId = indexIdVal
            name = dataSourceName
            description = dataSourceDescription
            roleArn = dataSourceRoleArn
            type = DataSourceType.S3
            configuration = DataSourceConfiguration {
                s3Configuration = S3DataSourceConfiguration {
                    bucketName = s3BucketName
                }
            }
        }

        KendraClient { region = "us-east-1" }.use { kendra ->

             val createDataSourceResponse = kendra.createDataSource(createDataSourceRequest)
             println("Response of creating data source $createDataSourceResponse")
             val dataSourceId = createDataSourceResponse.id
             println("Waiting for Kendra to create the data source $dataSourceId")

             val describeDataSourceRequest = DescribeDataSourceRequest {
                 indexId = indexIdVal
                 id = dataSourceId
             }

             var finished = false
             while (!finished) {

                val describeDataSourceResponse = kendra.describeDataSource(describeDataSourceRequest)
                val status = describeDataSourceResponse.status
                println("Status is $status")
                if (status !== DataSourceStatus.Creating)
                      finished = true
                delay(30000)
             }
            return dataSourceId.toString()
         }
}
// snippet-end:[kendra.kotlin.datasource.main]

// snippet-start:[kendra.kotlin.start.datasource.main]
suspend fun startDataSource(indexIdVal: String?, dataSourceId: String?) {

    println("Synchronize the data source $dataSourceId")
    val startDataSourceSyncJobRequest = StartDataSourceSyncJobRequest {
        indexId = indexIdVal
        id = dataSourceId
    }

    KendraClient { region = "us-east-1" }.use { kendra ->
        val startDataSourceSyncJobResponse = kendra.startDataSourceSyncJob(startDataSourceSyncJobRequest)
        println("Waiting for the data source to sync with the index $indexIdVal for execution ID ${startDataSourceSyncJobResponse.executionId}")
    }
    println("Index setup is complete")
}
// snippet-end:[kendra.kotlin.start.datasource.main]