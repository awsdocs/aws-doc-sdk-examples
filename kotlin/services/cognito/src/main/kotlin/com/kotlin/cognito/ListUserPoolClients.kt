//snippet-sourcedescription:[ListUserPoolClients.kt demonstrates how to list existing user pool clients that are available in the specified AWS Region in your current AWS account.]
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

//snippet-start:[cognito.kotlin.ListUserPoolClients.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ListUserPoolClientsRequest
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.ListUserPoolClients.import]

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
    listAllUserPoolClients(userPoolId)
}

//snippet-start:[cognito.kotlin.ListUserPoolClients.main]
suspend fun listAllUserPoolClients(userPoolId: String) {

    val request = ListUserPoolClientsRequest{
        this.userPoolId = userPoolId
     }

     CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
        val response = cognitoClient.listUserPoolClients(request)
        response.userPoolClients?.forEach { pool ->
           println("Client ID is ${pool.clientId}")
           println("Client Name is ${pool.clientName}")
        }
     }
 }
//snippet-end:[cognito.kotlin.ListUserPoolClients.main]