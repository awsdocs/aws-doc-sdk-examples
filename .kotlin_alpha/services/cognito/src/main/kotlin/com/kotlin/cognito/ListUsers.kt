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
import aws.sdk.kotlin.services.cognitoidentity.model.CognitoIdentityException
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
    val cognitoClient = CognitoIdentityProviderClient { region = "us-east-1" }
    listAllUsers(cognitoClient,userPoolId)
    cognitoClient.close()
}

//snippet-start:[cognito.kotlin.ListUsers.main]
 suspend fun listAllUsers(cognitoClient: CognitoIdentityProviderClient, userPoolId: String) {

        try {
            val listUsersRequest = ListUsersRequest {
                this.userPoolId = userPoolId
            }

            val response = cognitoClient.listUsers(listUsersRequest)
            response.users?.forEach { user ->
                println("The user name is ${user.username}")
            }

        } catch (ex: CognitoIdentityException) {
            println(ex.message)
            cognitoClient.close()
            exitProcess(0)
        }
 }
//snippet-end:[cognito.kotlin.ListUsers.main]