//snippet-sourcedescription:[DescribeSecret.kt demonstrates how to describe a secret.]
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

//snippet-start:[secretsmanager.kotlin.describe_secret.import]
import aws.sdk.kotlin.services.secretsmanager.model.SecretsManagerException
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.DescribeSecretRequest
import kotlin.system.exitProcess
//snippet-end:[secretsmanager.kotlin.describe_secret.import]

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <secretName> 

    Where:
         secretName - the name of the secret (for example, tutorials/MyFirstSecret).
       
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val secretName = args[0]
    val secretsClient = SecretsManagerClient { region = "us-east-1" }
    describeGivenSecret(secretsClient, secretName)
    secretsClient.close()
}


//snippet-start:[secretsmanager.kotlin.describe_secret.main]
  suspend fun describeGivenSecret(secretsClient: SecretsManagerClient, secretName: String?) {
        try {

            val secretRequest = DescribeSecretRequest {
                secretId = secretName
            }

            val secretResponse = secretsClient.describeSecret(secretRequest)
            val secArn = secretResponse.description
            println("The secret description is $secArn")


        } catch (ex: SecretsManagerException) {
            println(ex.message)
            secretsClient.close()
            exitProcess(0)
        }
}
//snippet-end:[secretsmanager.kotlin.describe_secret.main]