//snippet-sourcedescription:[ListIdentities.kt demonstrates how to list identities that belong to an Amazon Cognito identity pool.]
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

//snippet-start:[cognito.kotlin.listidentities.import]
import aws.sdk.kotlin.services.cognitoidentity.CognitoIdentityClient
import aws.sdk.kotlin.services.cognitoidentity.model.ListIdentitiesRequest
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.listidentities.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
    Usage:
        <identityPoolId>

    Where:
        identityPoolId - the id value of your identity pool (for example, us-east-1:00eb915b-c521-417b-af0d-ebad008axxxx).\n\n" ;
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val identityPoolId = args[0]
    listPoolIdentities(identityPoolId)
    }

//snippet-start:[cognito.kotlin.listidentities.main]
suspend fun listPoolIdentities(identityPoolId: String?) {

    val request =ListIdentitiesRequest{
        this.identityPoolId = identityPoolId
        maxResults = 15
    }

    CognitoIdentityClient { region = "us-east-1" }.use { cognitoIdentityClient ->
            val response = cognitoIdentityClient.listIdentities(request)
            response.identities?.forEach { identity ->
                println("The identity Id value is ${identity.identityId}")
            }
    }
}
//snippet-end:[cognito.kotlin.listidentities.main]