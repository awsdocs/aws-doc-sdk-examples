//snippet-sourcedescription:[CreateUserPool.kt demonstrates how to create a user pool for Amazon Cognito.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[08/01/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

//snippet-start:[cognito.kotlin.create_user_pool.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.CreateUserPoolRequest
import aws.sdk.kotlin.services.cognitoidentity.model.CognitoIdentityException
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.create_user_pool.import]

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
    val cognitoClient = CognitoIdentityProviderClient { region = "us-east-1" }
    val userPoolId = createPool(cognitoClient,userPoolName)
    print("The new user pool Id is $userPoolId")
    cognitoClient.close()
}

//snippet-start:[cognito.kotlin.create_user_pool.main]
 suspend fun createPool(cognitoClient: CognitoIdentityProviderClient, userPoolName:String): String? {

        try {

            val createUserPoolRequest = CreateUserPoolRequest{
                this.poolName = userPoolName
            }

            val createUserPoolResponse = cognitoClient.createUserPool(createUserPoolRequest)
            return createUserPoolResponse.userPool?.id;

        } catch (ex: CognitoIdentityException) {
            println(ex.message)
            cognitoClient.close()
            exitProcess(0)
        }
        return ""
  }
//snippet-end:[cognito.kotlin.create_user_pool.main]