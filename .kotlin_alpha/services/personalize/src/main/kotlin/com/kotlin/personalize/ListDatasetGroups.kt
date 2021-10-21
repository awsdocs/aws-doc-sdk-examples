//snippet-sourcedescription:[ListDatasetGroups.kt demonstrates how to list Amazon Personalize data set groups.]
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

//snippet-start:[personalize.kotlin.list_dsgroups.import]
import aws.sdk.kotlin.services.personalize.PersonalizeClient
import aws.sdk.kotlin.services.personalize.model.ListDatasetGroupsRequest
import aws.sdk.kotlin.services.personalize.model.PersonalizeException
import kotlin.system.exitProcess
//snippet-end:[personalize.kotlin.list_dsgroups.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){

    val personalizeClient = PersonalizeClient{ region = "us-east-1" }
    listDSGroups(personalizeClient)
    personalizeClient.close()

}

//snippet-start:[personalize.kotlin.list_dsgroups.main]
suspend fun listDSGroups(personalizeClient: PersonalizeClient) {
        try {
            val groupsRequest = ListDatasetGroupsRequest {
                maxResults = 15
            }

            val groupsResponse = personalizeClient.listDatasetGroups(groupsRequest)
            val groups = groupsResponse.datasetGroups
            if (groups != null) {
                for (group in groups) {
                    println("The DataSet name is ${group.name}")
                    println("The DataSet ARN is ${group.datasetGroupArn}")
                }
            }

        } catch (ex: PersonalizeException) {
            println(ex.message)
            personalizeClient.close()
            exitProcess(0)
        }
 }
//snippet-end:[personalize.kotlin.list_dsgroups.main]