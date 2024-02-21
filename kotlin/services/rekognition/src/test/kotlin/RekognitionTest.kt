// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.rekognition.model.NotificationChannel
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.rekognition.addToCollection
import com.kotlin.rekognition.compareTwoFaces
import com.kotlin.rekognition.createMyCollection
import com.kotlin.rekognition.describeColl
import com.kotlin.rekognition.detectFacesinImage
import com.kotlin.rekognition.detectImageLabels
import com.kotlin.rekognition.detectModLabels
import com.kotlin.rekognition.detectTextLabels
import com.kotlin.rekognition.displayGear
import com.kotlin.rekognition.getCelebrityInfo
import com.kotlin.rekognition.getFaceResults
import com.kotlin.rekognition.getModResults
import com.kotlin.rekognition.listAllCollections
import com.kotlin.rekognition.recognizeAllCelebrities
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
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class RekognitionTest {
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
    fun setup() = runBlocking {
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

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)

        // Populate the data members required for all tests.
        facesImage = prop.getProperty("facesImage")
        celebritiesImage = prop.getProperty("celebritiesImage")
        faceImage2 = prop.getProperty("faceImage2")
        celId = prop.getProperty("celId")
        moutainImage = prop.getProperty("moutainImage")
        collectionName = prop.getProperty("collectionName")
        ppeImage = prop.getProperty("ppeImage")
        bucketName = prop.getProperty("bucketName")
        textImage = prop.getProperty("textImage")
        modImage = prop.getProperty("modImage")
        faceVid = prop.getProperty("faceVid")
        topicArn = prop.getProperty("topicArn")
        roleArn = prop.getProperty("roleArn")
        modVid = prop.getProperty("modVid")
        textVid = prop.getProperty("textVid")
        celVid = prop.getProperty("celVid")
        */
    }

    @Test
    @Order(1)
    fun detectFacesTest() = runBlocking {
        detectFacesinImage(facesImage)
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun recognizeCelebritiesTest() = runBlocking {
        recognizeAllCelebrities(celebritiesImage)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun compareFacesTest() = runBlocking {
        compareTwoFaces(70f, facesImage, faceImage2)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun celebrityInfoTest() = runBlocking {
        getCelebrityInfo(celId)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun detectLabelsTest() = runBlocking {
        detectImageLabels(moutainImage)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun createCollectionTest() = runBlocking {
        createMyCollection(collectionName)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun addFacesToCollectionTest() = runBlocking {
        addToCollection(collectionName, facesImage)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun listFacesCollectionTest() = runBlocking {
        listAllCollections()
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun listCollectionsTest() = runBlocking {
        listAllCollections()
        println("Test 9 passed")
    }

    @Test
    @Order(10)
    fun describeCollectionTest() = runBlocking {
        describeColl(collectionName)
        println("Test 10 passed")
    }

    @Test
    @Order(11)
    fun detectPPETest() = runBlocking {
        displayGear(ppeImage)
        println("Test 11 passed")
    }

    @Test
    @Order(12)
    fun detectTextTest() = runBlocking {
        detectTextLabels(textImage)
        println("Test 12 passed")
    }

    @Test
    @Order(13)
    fun DetectModerationLabelsTest() = runBlocking {
        detectModLabels(modImage)
        println("Test 13 passed")
    }

    @Test
    @Order(14)
    fun VideoDetectFacesTest() = runBlocking {
        startFaceDetection(channel, bucketName, celVid)
        getFaceResults()
        println("Test 14 passed")
    }

    @Test
    @Order(15)
    fun VideoDetectInappropriateTest() = runBlocking {
        startModerationDetection(channel, bucketName, modVid)
        getModResults()
        println("Test 15 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/rekognition"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
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
