//snippet-sourcedescription:[DescribeUserPool.kt demonstrates how to obtain information about an existing user pool.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

//snippet-start:[cognito.kotlin.DescribeUserPool.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.DescribeUserPoolRequest
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.DescribeUserPool.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: <userPoolId>
    
        Where:
            userPoolId - the ID given to your user pool.
        """

      if (args.size != 1) {
          println(usage)
          exitProcess(0)
      }

    val userPoolId = args[0]
    describePool(userPoolId)
   }

//snippet-start:[cognito.kotlin.DescribeUserPool.main]
 suspend fun describePool(userPoolId: String) {

    val request = DescribeUserPoolRequest{
        this.userPoolId = userPoolId
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
            val response = cognitoClient.describeUserPool(request)
            val poolARN = response.userPool?.arn
            println("The user pool ARN is $poolARN")
    }
 }
//snippet-end:[cognito.kotlin.DescribeUserPool.main]