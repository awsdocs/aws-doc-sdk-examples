//snippet-sourcedescription:[ListRecipes.kt demonstrates how to list Amazon Personalize recipes.]
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

//snippet-start:[personalize.kotlin.list_recipes.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListRecipesRequest
import aws.sdk.kotlin.services.personalize.model.RecipeSummary
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.list_recipes.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){

    val personalizeClient = PersonalizeClient{ region = "us-east-1" }
    listAllRecipes(personalizeClient)
    personalizeClient.close()
}

//snippet-start:[personalize.kotlin.list_recipes.main]
suspend fun listAllRecipes(personalizeClient: PersonalizeClient) {

        try {

            val recipesRequest = ListRecipesRequest {
                maxResults = 15
            }

            val response = personalizeClient.listRecipes(recipesRequest)
            val recipes: List<RecipeSummary>? = response.recipes
            if (recipes != null) {
                for (recipe in recipes) {
                    println("The recipe ARN is ${recipe.recipeArn}")
                    println("The recipe name is ${recipe.name}")
                }
            }

        } catch (ex: PersonalizeException) {
            println(ex.message)
            personalizeClient.close()
            exitProcess(0)
        }
}
//snippet-end:[personalize.kotlin.list_recipes.main]