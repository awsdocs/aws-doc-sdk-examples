// snippet-sourcedescription:[DescribeAccount.kt demonstrates how to get information about the Amazon Elastic Compute Cloud (Amazon EC2) account.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon EC2]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_account.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeAccountAttributesRequest
// snippet-end:[ec2.kotlin.describe_account.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    describeEC2Account()
}

// snippet-start:[ec2.kotlin.describe_account.main]
suspend fun describeEC2Account() {

    Ec2Client { region = "us-west-2" }.use { ec2 ->
        val accountResults = ec2.describeAccountAttributes(DescribeAccountAttributesRequest {})
        accountResults.accountAttributes?.forEach { attribute ->
            println("The name of the attribute is ${attribute.attributeName}")

            attribute.attributeValues?.forEach { myValue ->
                println("The value of the attribute is ${myValue.attributeValue}")
            }
        }
    }
}
// snippet-end:[ec2.kotlin.describe_account.main]
