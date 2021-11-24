//snippet-sourcedescription:[CreateUserPool.kt demonstrates how to create a user pool for Amazon Cognito.]
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

//snippet-start:[cognito.kotlin.create_user_pool.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.CreateUserPoolRequest
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.create_user_pool.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: <userPoolName>
    
        Where:
            userPoolName - the ID given to your user pool.
        """

     if (args.size != 1) {
          println(usage)
          exitProcess(0)
      }

    val userPoolName = args[0]
    val userPoolId = createPool(userPoolName)
    print("The new user pool Id is $userPoolId")
    }

//snippet-start:[cognito.kotlin.create_user_pool.main]
 suspend fun createPool(userPoolName:String): String? {

    val request = CreateUserPoolRequest{
        this.poolName = userPoolName
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
            val createUserPoolResponse = cognitoClient.createUserPool(request)
            return createUserPoolResponse.userPool?.id
    }
 }
//snippet-end:[cognito.kotlin.create_user_pool.main]