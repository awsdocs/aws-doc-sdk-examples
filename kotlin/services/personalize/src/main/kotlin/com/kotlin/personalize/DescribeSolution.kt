// snippet-sourcedescription:[DescribeSolution.kt demonstrates how to describe an Amazon Personalize solution.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.describe_solution.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.DescribeSolutionRequest
import kotlin.system.exitProcess
// snippet-end:[personalize.kotlin.describe_solution.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <solutionArn>

    Where:
         solutionArn - The ARN of the solution to describe.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val solutionArn = args[0]
    describeSpecificSolution(solutionArn)
}

// snippet-start:[personalize.kotlin.describe_solution.main]
suspend fun describeSpecificSolution(solutionArnVal: String?) {

    val request = DescribeSolutionRequest {
        solutionArn = solutionArnVal
    }

    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        val response = personalizeClient.describeSolution(request)
        println("The solution name is ${response.solution?.name}")
    }
}
// snippet-end:[personalize.kotlin.describe_solution.main]
