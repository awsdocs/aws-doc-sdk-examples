//snippet-sourcedescription:[ListRecipes.kt demonstrates how to list Amazon Personalize recipes.]
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

//snippet-start:[personalize.kotlin.list_recipes.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListRecipesRequest
//snippet-end:[personalize.kotlin.list_recipes.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){
    listAllRecipes()
    }

//snippet-start:[personalize.kotlin.list_recipes.main]
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
//snippet-end:[personalize.kotlin.list_recipes.main]