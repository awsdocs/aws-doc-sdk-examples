// snippet-sourcedescription:[ListLexicons demonstrates how to produce a list of pronunciation lexicons stored in an AWS Region.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/05/2021]
// snippet-sourceauthor:[scmacdon AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.polly

// snippet-start:[polly.kotlin.list_icons.import]
import aws.sdk.kotlin.services.polly.PollyClient
import aws.sdk.kotlin.services.polly.model.ListLexiconsRequest
import aws.sdk.kotlin.services.polly.model.PollyException
import kotlin.system.exitProcess
// snippet-end:[polly.kotlin.list_icons.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    listLexicons()
    }

// snippet-start:[polly.kotlin.list_icons.main]
suspend fun listLexicons() {

        PollyClient { region = "us-west-2" }.use { polly ->
            val response = polly.listLexicons(ListLexiconsRequest{})
            response.lexicons?.forEach { lexDescription ->
                   println("The name of the Lexicon is ${lexDescription.name}")
            }
        }
}
// snippet-end:[polly.kotlin.list_icons.main]