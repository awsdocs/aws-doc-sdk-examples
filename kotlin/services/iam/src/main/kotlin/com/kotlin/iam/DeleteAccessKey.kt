//snippet-sourcedescription:[DeleteAccessKey.kt demonstrates how to delete an access key from an AWS Identity and Access Management (IAM) user.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Identity and Access Management (IAM)]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.delete_access_key.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.DeleteAccessKeyRequest
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.delete_access_key.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <username> <accessKey>
        Where:
            username - the name of the user.
            accessKey - the access key ID for the secret access key you want to delete.
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val userName = args[0]
    val accessKey = args[1]
    deleteKey(userName, accessKey)
}

// snippet-start:[iam.kotlin.delete_access_key.main]
suspend fun deleteKey(userNameVal: String, accessKey: String) {

    val request = DeleteAccessKeyRequest {
        accessKeyId =accessKey
        userName = userNameVal
    }

    IamClient { region = "AWS_GLOBAL" }.use { iamClient ->
        iamClient.deleteAccessKey(request)
        println( "Successfully deleted access key $accessKey from $userNameVal")
    }
}
// snippet-end:[iam.kotlin.delete_access_key.main]