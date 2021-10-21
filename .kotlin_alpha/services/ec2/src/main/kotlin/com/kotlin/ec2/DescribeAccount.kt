//snippet-sourcedescription:[DescribeAccount.kt demonstrates how to get information about the Amazon Elastic Compute Cloud (Amazon EC2) account.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/21/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.ec2

// snippet-start:[ec2.kotlin.describe_account.import]
import aws.sdk.kotlin.services.ec2.Ec2Client
import aws.sdk.kotlin.services.ec2.model.DescribeAccountAttributesRequest
import aws.sdk.kotlin.services.ec2.model.Ec2Exception
import kotlin.system.exitProcess
// snippet-end:[ec2.kotlin.describe_account.import]


/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val ec2Client = Ec2Client{region = "us-east-1"}
    describeEC2Account(ec2Client)
}

// snippet-start:[ec2.kotlin.describe_account.main]
suspend fun describeEC2Account(ec2: Ec2Client) {

    try {
        val accountResults = ec2.describeAccountAttributes(DescribeAccountAttributesRequest{})
        val accountList = accountResults.accountAttributes
        val iter= accountList?.listIterator()
        if (iter != null) {
            while (iter.hasNext()) {
                val attribute = iter.next()
                println("The name of the attribute is ${attribute.attributeName}")
                val values =  attribute.attributeValues
                val iterVals = values?.listIterator()
                if (iterVals != null) {
                    while (iterVals.hasNext()) {
                        val myValue = iterVals.next()
                        println("The value of the attribute is ${myValue.attributeValue}")
                    }
                }
            }
        }

    } catch (e: Ec2Exception) {
        println(e.message)
        exitProcess(0)
    }
 }
// snippet-end:[ec2.kotlin.describe_account.main]