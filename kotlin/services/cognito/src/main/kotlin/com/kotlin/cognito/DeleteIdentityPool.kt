// snippet-sourcedescription:[DeleteIdentityPool.kt demonstrates how to delete an existing Amazon Cognito identity pool.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Cognito]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

// snippet-start:[cognito.kotlin.deleteidpool.import]
import aws.sdk.kotlin.services.cognitoidentity.CognitoIdentityClient
import aws.sdk.kotlin.services.cognitoidentity.model.DeleteIdentityPoolRequest
import kotlin.system.exitProcess
// snippet-end:[cognito.kotlin.deleteidpool.import]

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
            identityPoolName - The name of the identity pool.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val identityPoold = args[0]
    deleteIdPool(identityPoold)
}

// snippet-start:[cognito.kotlin.deleteidpool.main]
suspend fun deleteIdPool(identityPoold: String?) {

    val request = DeleteIdentityPoolRequest {
        this.identityPoolId = identityPoold
    }

    CognitoIdentityClient { region = "us-east-1" }.use { cognitoIdclient ->

        cognitoIdclient.deleteIdentityPool(request)
        println("The identity pool was successfully deleted")
    }
}
// snippet-end:[cognito.kotlin.deleteidpool.main]
