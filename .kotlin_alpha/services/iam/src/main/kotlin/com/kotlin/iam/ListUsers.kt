//snippet-sourcedescription:[ListUsers.kt demonstrates how to list all AWS Identity and Access Management (IAM) users.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Identity and Access Management (IAM)]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/27/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.iam

// snippet-start:[iam.kotlin.list_users.import]
import aws.sdk.kotlin.services.iam.IamClient
import aws.sdk.kotlin.services.iam.model.IamException
import aws.sdk.kotlin.services.iam.model.ListUsersRequest
import aws.sdk.kotlin.services.iam.model.ListUsersResponse
import kotlin.system.exitProcess
// snippet-end:[iam.kotlin.list_users.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val iamClient = IamClient{region="AWS_GLOBAL"}
    listAllUsers(iamClient)
    iamClient.close()
}

// snippet-start:[iam.kotlin.list_users.main]
suspend fun listAllUsers(iamClient: IamClient) {
        try {
            var done = false
            var newMarker: String? = null
            while (!done) {

                var response: ListUsersResponse
                response = if (newMarker == null) {
                    val request = ListUsersRequest { }
                    iamClient.listUsers(request)

                } else {
                    val request = ListUsersRequest {
                        marker = newMarker
                    }
                    iamClient.listUsers(request)
                }

                for (user in response.users!!) {
                   println("Retrieved user ${user.userName}")
                }
                if (!response.isTruncated) {
                    done = true
                } else {
                    newMarker = response.marker
                }
            }

        } catch (e: IamException) {
            println(e.message)
            iamClient.close()
            exitProcess(0)
        }
 }
// snippet-end:[iam.kotlin.list_users.main]