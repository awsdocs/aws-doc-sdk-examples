// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.personalize.createPersonalCompaign
import com.kotlin.personalize.createPersonalizeSolution
import com.kotlin.personalize.deleteGivenSolution
import com.kotlin.personalize.deleteSpecificCampaign
import com.kotlin.personalize.describeSpecificCampaign
import com.kotlin.personalize.describeSpecificSolution
import com.kotlin.personalize.getRecs
import com.kotlin.personalize.listAllCampaigns
import com.kotlin.personalize.listAllRecipes
import com.kotlin.personalize.listAllSolutions
import com.kotlin.personalize.listDSGroups
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class PersonalizeKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(PersonalizeKotlinTest::class.java)
    private var datasetGroupArn = ""
    private var solutionArn = ""
    private var solutionVersionArn = ""
    private var recipeArn = ""
    private var solutionName = ""
    private var campaignName = ""
    private var campaignArn = ""
    private var userId = ""
    private var newCampaignArn = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            datasetGroupArn = values.datasetGroupArn.toString()
            solutionVersionArn = values.solutionVersionArn.toString()
            recipeArn = values.recipeArn.toString()
            solutionName = values.solutionName.toString() + UUID.randomUUID()
            userId = values.userId.toString()
            campaignName = values.campaignName.toString() + UUID.randomUUID()
        }

    @Test
    @Order(1)
    fun createSolution() =
        runBlocking {
            solutionArn = createPersonalizeSolution(datasetGroupArn, solutionName, recipeArn).toString()
            assertTrue(!solutionArn.isEmpty())
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun listSolutions() =
        runBlocking {
            listAllSolutions(datasetGroupArn)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun describeSolution() =
        runBlocking {
            describeSpecificSolution(solutionArn)
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun createCampaign() =
        runBlocking {
            newCampaignArn = createPersonalCompaign(solutionVersionArn, campaignName).toString()
            assertTrue(!newCampaignArn.isEmpty())
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun describeCampaign() =
        runBlocking {
            println("Wait 20 mins for resource to become available.")
            TimeUnit.MINUTES.sleep(20)
            describeSpecificCampaign(newCampaignArn)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun listCampaigns() =
        runBlocking {
            listAllCampaigns(solutionArn)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun listRecipes() =
        runBlocking {
            listAllRecipes()
            logger.info("Test 7 passed")
        }

    @Test
    @Order(8)
    fun listDatasetGroups() =
        runBlocking {
            listDSGroups()
            logger.info("Test 8 passed")
        }

    @Test
    @Order(9)
    fun deleteSolution() =
        runBlocking {
            deleteGivenSolution(solutionArn)
            logger.info("Test 9 passed")
        }

    @Test
    @Order(10)
    fun getRecommendations() =
        runBlocking {
            getRecs(newCampaignArn, userId)
            logger.info("Test 10 passed")
        }

    @Test
    @Order(11)
    fun deleteCampaign() =
        runBlocking {
            deleteSpecificCampaign(newCampaignArn)
            logger.info("Test 11 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/personalize"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/personalize (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val solutionName: String? = null
        val campaignName: String? = null
        val existingSolutionArn: String? = null
        val solutionVersionArn: String? = null
        val existingCampaignName: String? = null
        val datasetGroupArn: String? = null
        val recipeArn: String? = null
        val userId: String? = null
    }
}
