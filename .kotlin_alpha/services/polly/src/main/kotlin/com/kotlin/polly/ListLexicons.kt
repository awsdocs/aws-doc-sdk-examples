// snippet-sourcedescription:[ListLexicons demonstrates how to produce a list of pronunciation lexicons stored in an AWS Region.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06/02/2021]
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

    val polly = PollyClient{ region = "us-west-2" }
    listLexicons(polly)
    polly.close()
}

// snippet-start:[polly.kotlin.list_icons.main]
suspend fun listLexicons(client: PollyClient) {
        try {

            val listLexiconsResult = client.listLexicons(ListLexiconsRequest{})
            val lexiconDescription = listLexiconsResult.lexicons
            if (lexiconDescription != null) {
                for (lexDescription in lexiconDescription) {
                    println("The name of the Lexicon is ${lexDescription.name}")
                }
            }

        } catch (ex: PollyException) {
            println(ex.message)
            client.close()
            exitProcess(0)
        }
}
// snippet-end:[polly.kotlin.list_icons.main]