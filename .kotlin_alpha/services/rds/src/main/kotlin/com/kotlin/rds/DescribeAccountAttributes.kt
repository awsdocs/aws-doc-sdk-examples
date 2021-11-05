//snippet-sourcedescription:[DescribeAccountAttributes.kt demonstrates how to retrieve attributes that belong to an Amazon Relational Database Service (RDS) account.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.describe_account.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.DescribeAccountAttributesRequest
import aws.sdk.kotlin.services.rds.model.RdsException
import kotlin.system.exitProcess
// snippet-end:[rds.kotlin.describe_account.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val rdsClient = RdsClient{region="us-west-2"}
    getAccountAttributes(rdsClient)
    rdsClient.close()
}

// snippet-start:[rds.kotlin.describe_account.main]
suspend fun getAccountAttributes(rdsClient: RdsClient) {

    try {
        val response = rdsClient.describeAccountAttributes(DescribeAccountAttributesRequest{})
        response.accountQuotas?.forEach { quotas ->
        val response = response.accountQuotas
                println("Name is: ${quotas.accountQuotaName}")
                println("Max value is ${quotas.max}")
        }

    } catch (e: RdsException) {
        println(e.message)
        rdsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rds.kotlin.describe_account.main]