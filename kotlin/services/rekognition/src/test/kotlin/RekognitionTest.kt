// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.rekognition.model.NotificationChannel
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.rekognition.createMyCollection
import com.kotlin.rekognition.describeColl
import com.kotlin.rekognition.getCelebrityInfo
import com.kotlin.rekognition.getFaceResults
import com.kotlin.rekognition.getModResults
import com.kotlin.rekognition.listAllCollections
import com.kotlin.rekognition.startFaceDetection
import com.kotlin.rekognition.startModerationDetection
import kotlinx.coroutines.runBlocking
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RekognitionTest {
    private val logger: Logger = LoggerFactory.getLogger(RekognitionTest::class.java)
    private var channel: NotificationChannel? = null
    private var facesImage = ""
    private var celebritiesImage = ""
    private var faceImage2 = ""
    private var celId = ""
    private var moutainImage = ""
    private var collectionName = ""
    private var ppeImage = ""
    private var bucketName = ""
    private var textImage = ""
    private var modImage = ""
    private var faceVid = ""
    private var topicArn = ""
    private var roleArn = ""
    private var modVid = ""
    private var textVid = ""
    private var celVid = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            facesImage = values.facesImage.toString()
            celebritiesImage = values.celebritiesImage.toString()
            faceImage2 = values.faceImage2.toString()
            celId = values.celId.toString()
            moutainImage = values.moutainImage.toString()
            collectionName = values.collectionName + UUID.randomUUID()
            ppeImage = values.ppeImage.toString()
            bucketName = values.bucketName.toString()
            textImage = values.textImage.toString()
            modImage = values.modImage.toString()
            faceVid = values.faceVid.toString()
            topicArn = values.topicArn.toString()
            roleArn = values.roleArn.toString()
            modVid = values.modVid.toString()
            textVid = values.textVid.toString()
            celVid = values.celVid.toString()
        }

    @Test
    @Order(1)
    fun celebrityInfoTest() =
        runBlocking {
            getCelebrityInfo(celId)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createCollectionTest() =
        runBlocking {
            createMyCollection(collectionName)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listFacesCollectionTest() =
        runBlocking {
            listAllCollections()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listCollectionsTest() =
        runBlocking {
            listAllCollections()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun describeCollectionTest() =
        runBlocking {
            describeColl(collectionName)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun videoDetectFacesTest() =
        runBlocking {
            startFaceDetection(channel, bucketName, celVid)
            getFaceResults()
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun videoDetectInappropriateTest() =
        runBlocking {
            startModerationDetection(channel, bucketName, modVid)
            getModResults()
            logger.info("Test 7 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/rekognition"
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
    @DisplayName("A class used to get test values from test/rekognition (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val facesImage: String? = null
        val faceImage2: String? = null
        val celebritiesImage: String? = null
        val celId: String? = null
        val moutainImage: String? = null
        val collectionName: String? = null
        val ppeImage: String? = null
        val textImage: String? = null
        val modImage: String? = null
        val bucketName: String? = null
        val faceVid: String? = null
        val modVid: String? = null
        val textVid: String? = null
        val celVid: String? = null
        val topicArn: String? = null
        val roleArn: String? = null
    }
}
