// snippet-sourcedescription:[DescribeAccountAttributes.kt demonstrates how to retrieve attributes that belong to an Amazon Relational Database Service (RDS) account.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.rds

// snippet-start:[rds.kotlin.describe_account.import]
import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.DescribeAccountAttributesRequest
// snippet-end:[rds.kotlin.describe_account.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main() {
    getAccountAttributes()
}

// snippet-start:[rds.kotlin.describe_account.main]
suspend fun getAccountAttributes() {

    RdsClient { region = "us-west-2" }.use { rdsClient ->
        val response = rdsClient.describeAccountAttributes(DescribeAccountAttributesRequest {})
        response.accountQuotas?.forEach { quotas ->
            val response = response.accountQuotas
            println("Name is: ${quotas.accountQuotaName}")
            println("Max value is ${quotas.max}")
        }
    }
}
// snippet-end:[rds.kotlin.describe_account.main]
