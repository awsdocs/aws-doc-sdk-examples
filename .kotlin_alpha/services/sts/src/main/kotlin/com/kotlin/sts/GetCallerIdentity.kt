// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetCallerIdentity.kt demonstrates how to obtain details about the IAM user whose credentials are used to call the operation.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Security Token Service (AWS STS)]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/05/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sts

// snippet-start:[sts.kotlin.get_call_id.import]
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.model.GetCallerIdentityRequest
// snippet-end:[sts.kotlin.get_call_id.import]


/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.
For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getCallerId()

}

// snippet-start:[sts.kotlin.get_call_id.main]
suspend fun getCallerId() {

    StsClient { region = "us-east-1" }.use { stsClient ->
      val response = stsClient.getCallerIdentity(GetCallerIdentityRequest{})
      println("The user id is ${response.userId}")
      println("The ARN value is ${response.arn}")
    }
}
// snippet-end:[sts.kotlin.get_call_id.main]