//snippet-sourcedescription:[ListUsers.kt demonstrates how to list users in the specified user pool.]
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

//snippet-start:[cognito.kotlin.ListUsers.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ListUsersRequest
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.ListUsers.import]

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
    listAllUsers(userPoolId)
}

//snippet-start:[cognito.kotlin.ListUsers.main]
 suspend fun listAllUsers(userPoolId: String) {

    val request = ListUsersRequest {
        this.userPoolId = userPoolId
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
            val response = cognitoClient.listUsers(request)
            response.users?.forEach { user ->
                println("The user name is ${user.username}")
            }
    }
 }
//snippet-end:[cognito.kotlin.ListUsers.main]