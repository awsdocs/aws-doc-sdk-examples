//snippet-sourcedescription:[ListSecrets.kt demonstrates how to list all of the secrets that are stored by Secrets Manager.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Secrets Manager]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/02/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.secrets

//snippet-start:[secretsmanager.kotlin.list_secrets.import]
import aws.sdk.kotlin.services.secretsmanager.model.SecretsManagerException
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.ListSecretsRequest
import aws.sdk.kotlin.services.secretsmanager.model.SecretListEntry
import kotlin.system.exitProcess
//snippet-end:[secretsmanager.kotlin.list_secrets.import]

suspend fun main() {

    val secretsClient = SecretsManagerClient { region = "us-east-1" }
    listAllSecrets(secretsClient)
    secretsClient.close()
}

//snippet-start:[secretsmanager.kotlin.list_secrets.main]
suspend fun listAllSecrets(secretsClient: SecretsManagerClient) {

        try {
            val requestOb = ListSecretsRequest{}
            val secretsResponse = secretsClient.listSecrets(requestOb)

            val secrets: List<SecretListEntry>? = secretsResponse.secretList
            if (secrets != null) {
                for (secret in secrets) {
                    println("The secret name is ${secret.name}")
                    println("The secret description is ${secret.description}")
                }
            }
        } catch (ex: SecretsManagerException) {
            println(ex.message)
            secretsClient.close()
            exitProcess(0)
        }
}
//snippet-end:[secretsmanager.kotlin.list_secrets.main]