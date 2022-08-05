// snippet-sourcedescription:[DeleteSecret.kt demonstrates how to delete a secret.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Secrets Manager]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.secrets

// snippet-start:[secretsmanager.kotlin.delete_secret.import]
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.DeleteSecretRequest
import kotlin.system.exitProcess
// snippet-end:[secretsmanager.kotlin.delete_secret.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <secretName> 

    Where:
         secretName - The name of the secret (for example, tutorials/MyFirstSecret).
     """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val secretName = args[0]
    deleteSpecificSecret(secretName)
}

// snippet-start:[secretsmanager.kotlin.delete_secret.main]
suspend fun deleteSpecificSecret(secretName: String) {

    val request = DeleteSecretRequest {
        secretId = secretName
    }

    SecretsManagerClient { region = "us-east-1" }.use { secretsClient ->
        secretsClient.deleteSecret(request)
        println("$secretName is deleted.")
    }
}
// snippet-end:[secretsmanager.kotlin.delete_secret.main]
