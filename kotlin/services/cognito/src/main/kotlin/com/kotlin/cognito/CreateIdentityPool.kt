// snippet-sourcedescription:[CreateIdentityPool.kt demonstrates how to create a new Amazon Cognito identity pool. The identity pool is a store of user identity information that is specific to your AWS account.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Cognito]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

// snippet-start:[cognito.kotlin.create_identity_pool.import]
import aws.sdk.kotlin.services.cognitoidentity.CognitoIdentityClient
import aws.sdk.kotlin.services.cognitoidentity.model.CreateIdentityPoolRequest
import kotlin.system.exitProcess
// snippet-end:[cognito.kotlin.create_identity_pool.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: <identityPoolName>
    
        Where:
            identityPoolName - The name to give your identity pool.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val identityPoolName = args[0]
    val idPool = createIdPool(identityPoolName)
    println("Identity pool Id value is $idPool")
}

// snippet-start:[cognito.kotlin.create_identity_pool.main]
suspend fun createIdPool(identityPoolName: String?): String? {

    val request = CreateIdentityPoolRequest {
        this.allowUnauthenticatedIdentities = false
        this.identityPoolName = identityPoolName
    }

    CognitoIdentityClient { region = "us-east-1" }.use { cognitoIdentityClient ->

        val response = cognitoIdentityClient.createIdentityPool(request)
        return response.identityPoolId
    }
}
// snippet-end:[cognito.kotlin.create_identity_pool.main]
