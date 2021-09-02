//snippet-sourcedescription:[DescribeSolution.kt demonstrates how to describe an Amazon Personalize solution.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[06/02/2021]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

//snippet-start:[personalize.kotlin.describe_solution.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.DescribeSolutionRequest
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.describe_solution.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>){

    val usage = """
    Usage:
        <solutionArn>

    Where:
         solutionArn - the ARN of the solution to describe.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val solutionArn = args[0]
    val personalizeClient = PersonalizeClient{ region = "us-east-1" }
    describeSpecificSolution(personalizeClient,solutionArn)
    personalizeClient.close()
}

//snippet-start:[personalize.kotlin.describe_solution.main]
suspend  fun describeSpecificSolution(personalizeClient: PersonalizeClient, solutionArnVal: String?) {
        try {
            val solutionRequest = DescribeSolutionRequest {
                solutionArn= solutionArnVal
            }

            val response = personalizeClient.describeSolution(solutionRequest)
            println("The solution name is ${response.solution?.name}")

        } catch (ex: PersonalizeException) {
            println(ex.message)
            personalizeClient.close()
            exitProcess(0)
        }
  }
//snippet-end:[personalize.kotlin.describe_solution.main]