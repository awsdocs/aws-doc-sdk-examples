// snippet-sourcedescription:[DeleteSolution.kt demonstrates how to delete an Amazon Personalize solution.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.delete_solution.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.DeleteSolutionRequest
import kotlin.system.exitProcess
// snippet-end:[personalize.kotlin.delete_solution.import]

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
         solutionArn - The ARN of the solution to delete.
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val solutionArn = args[0]
    deleteGivenSolution(solutionArn)
}

// snippet-start:[personalize.kotlin.delete_solution.main]
suspend fun deleteGivenSolution(solutionArnVal: String?) {

    val request = DeleteSolutionRequest {
        solutionArn = solutionArnVal
    }

    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        personalizeClient.deleteSolution(request)
        println("$solutionArnVal was successfully deleted.")
    }
}
// snippet-end:[personalize.kotlin.delete_solution.main]
