//snippet-sourcedescription:[ListSolutions.kt demonstrates how to list Amazon Personalize solutions.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

//snippet-start:[personalize.kotlin.list_solutions.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListSolutionsRequest
import aws.sdk.kotlin.services.personalize.model.SolutionSummary
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.list_solutions.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        <datasetGroupArn>

    Where:
        datasetGroupArn - The ARN of the data set group.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val datasetGroupArn = args[0]
    val personalizeClient = PersonalizeClient{ region = "us-east-1" }
    listAllSolutions(personalizeClient,datasetGroupArn)
    personalizeClient.close()
}

//snippet-start:[personalize.kotlin.list_solutions.main]
suspend fun listAllSolutions(personalizeClient: PersonalizeClient, datasetGroupArnValue: String?) {
        try {
            val solutionsRequest = ListSolutionsRequest{
                maxResults = 10
                datasetGroupArn = datasetGroupArnValue
                }

            val response = personalizeClient.listSolutions(solutionsRequest)
            response.solutions?.forEach { solution ->
                    println("The solution ARN is ${solution.solutionArn}")
                    println("The solution name is ${solution.name}")
            }

        } catch (ex: PersonalizeException) {
            println(ex.message)
            personalizeClient.close()
            exitProcess(0)
        }
 }
//snippet-end:[personalize.kotlin.list_solutions.main]