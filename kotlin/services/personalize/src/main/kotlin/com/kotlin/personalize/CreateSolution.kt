// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.create_solution.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.CreateSolutionRequest
import kotlin.system.exitProcess
// snippet-end:[personalize.kotlin.create_solution.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <datasetGroupArn> <solutionName> <recipeArn>

    Where:
         datasetGroupArn - The ARN of the data set group.
         solutionName - The name of the solution to create.
         recipeArn - The ARN of the recipe (for example, arn:aws:personalize:::recipe/aws-user-personalization).

    """
    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val datasetGroupArn = args[0]
    val solutionName = args[1]
    val recipeArn = args[2]
    val solutionArn = createPersonalizeSolution(datasetGroupArn, solutionName, recipeArn)
    println("The Amazon Personalize solution ARN is $solutionArn")
}

// snippet-start:[personalize.kotlin.create_solution.main]
suspend fun createPersonalizeSolution(
    datasetGroupArnVal: String?,
    solutionName: String?,
    recipeArnVal: String?,
): String? {
    val request =
        CreateSolutionRequest {
            name = solutionName
            datasetGroupArn = datasetGroupArnVal
            recipeArn = recipeArnVal
        }

    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        val solutionResponse = personalizeClient.createSolution(request)
        return solutionResponse.solutionArn
    }
}
// snippet-end:[personalize.kotlin.create_solution.main]
