//snippet-sourcedescription:[DeleteNamedQueryExample.kt demonstrates how to delete a named query by using the named query Id value.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/14/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

//snippet-start:[athena.kotlin.DeleteNamedQueryExample.import]
import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.DeleteNamedQueryRequest
import kotlin.system.exitProcess
//snippet-end:[athena.kotlin.DeleteNamedQueryExample.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <queryId> 

    Where:
        queryId - the id of the Amazon Athena query (for example, b34e7780-903b-4842-9d2c-6c99bebc82aa).
        
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val queryId = args[0]
    val athenaClient = AthenaClient { region = "us-west-2" }
    deleteQueryName(athenaClient,queryId)
    athenaClient.close()
}

//snippet-start:[athena.kotlin.DeleteNamedQueryExample.main]
suspend fun deleteQueryName(athenaClient: AthenaClient, sampleNamedQueryId: String?) {
    try {
        val deleteNamedQueryRequest = DeleteNamedQueryRequest {
            namedQueryId = sampleNamedQueryId
        }

        athenaClient.deleteNamedQuery(deleteNamedQueryRequest)
        println("$sampleNamedQueryId was deleted!")

    } catch (ex: AwsServiceException) {
        println(ex.message)
        athenaClient.close()
        exitProcess(0)
    }
}
//snippet-end:[athena.kotlin.DeleteNamedQueryExample.main]
