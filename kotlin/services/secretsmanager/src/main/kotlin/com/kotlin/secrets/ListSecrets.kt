// snippet-sourcedescription:[ListSecrets.kt demonstrates how to list all of the secrets that are stored by Secrets Manager.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Secrets Manager]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.secrets

// snippet-start:[secretsmanager.kotlin.list_secrets.import]
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.ListSecretsRequest
// snippet-end:[secretsmanager.kotlin.list_secrets.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllSecrets()
}

// snippet-start:[secretsmanager.kotlin.list_secrets.main]
suspend fun listAllSecrets() {

    SecretsManagerClient { region = "us-east-1" }.use { secretsClient ->
        val response = secretsClient.listSecrets(ListSecretsRequest {})
        response.secretList?.forEach { secret ->
            println("The secret name is ${secret.name}")
            println("The secret description is ${secret.description}")
        }
    }
}
// snippet-end:[secretsmanager.kotlin.list_secrets.main]
