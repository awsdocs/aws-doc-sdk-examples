// snippet-sourcedescription:[ListRecipes.kt demonstrates how to list Amazon Personalize recipes.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Personalize]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.personalize

// snippet-start:[personalize.kotlin.list_recipes.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListRecipesRequest
// snippet-end:[personalize.kotlin.list_recipes.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    listAllRecipes()
}

// snippet-start:[personalize.kotlin.list_recipes.main]
suspend fun listAllRecipes() {

    val request = ListRecipesRequest {
        maxResults = 15
    }
    PersonalizeClient { region = "us-east-1" }.use { personalizeClient ->
        val response = personalizeClient.listRecipes(request)
        response.recipes?.forEach { recipe ->
            println("The recipe ARN is ${recipe.recipeArn}")
            println("The recipe name is ${recipe.name}")
        }
    }
}
// snippet-end:[personalize.kotlin.list_recipes.main]
