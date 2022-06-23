//snippet-sourcedescription:[ListSecrets.kt demonstrates how to list all of the secrets that are stored by Secrets Manager.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Secrets Manager]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.secrets

//snippet-start:[secretsmanager.kotlin.list_secrets.import]
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.ListSecretsRequest
//snippet-end:[secretsmanager.kotlin.list_secrets.import]

suspend fun main() {
    listAllSecrets()
}

//snippet-start:[secretsmanager.kotlin.list_secrets.main]
suspend fun listAllSecrets() {

        SecretsManagerClient { region = "us-east-1" }.use { secretsClient ->
        val response = secretsClient.listSecrets(ListSecretsRequest{})
            response.secretList?.forEach { secret ->
                    println("The secret name is ${secret.name}")
                    println("The secret description is ${secret.description}")
            }
        }
}
//snippet-end:[secretsmanager.kotlin.list_secrets.main]