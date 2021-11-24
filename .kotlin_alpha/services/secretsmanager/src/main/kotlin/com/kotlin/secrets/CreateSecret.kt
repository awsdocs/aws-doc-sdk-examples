//snippet-sourcedescription:[CreateSecret.kt demonstrates how to create a secret for AWS Secrets Manager.]
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

//snippet-start:[secretsmanager.kotlin.create_secret.import]
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.CreateSecretRequest
import kotlin.system.exitProcess
//snippet-end:[secretsmanager.kotlin.create_secret.import]

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <secretName> <secretValue>

    Where:
         secretName - the name of the secret (for example, tutorials/MyFirstSecret).
         secretValue - the secret value.
      """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val secretName = args[0]
    val secretValue = args[1]
    val secArn = createNewSecret(secretName, secretValue)
    println("The secret ARN value is $secArn")
}

//snippet-start:[secretsmanager.kotlin.create_secret.main]
suspend fun createNewSecret(secretName: String?, secretValue: String?): String? {

        val request  = CreateSecretRequest {
            name = secretName
            description = "This secret was created by the AWS Secrets Manager Kotlin API"
            secretString = secretValue
        }

        SecretsManagerClient { region = "us-east-1" }.use { secretsClient ->
            val response = secretsClient.createSecret(request)
            return response.arn
        }
   }
//snippet-end:[secretsmanager.kotlin.create_secret.main]

