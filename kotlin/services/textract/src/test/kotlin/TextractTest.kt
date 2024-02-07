// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.textract.analyzeDoc
import com.kotlin.textract.detectDocText
import com.kotlin.textract.detectDocTextS3
import com.kotlin.textract.startDocAnalysisS3
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
class TextractTest {
    private var sourceDoc = ""
    private var bucketName = ""
    private var docName = ""

    @BeforeAll
    fun setup() = runBlocking {
        // Get the values to run these tests from AWS Secrets Manager.
        val gson = Gson()
        val json: String = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        sourceDoc =  values.sourceDoc.toString()
        bucketName = values.bucketName.toString()
        docName = values.docName.toString()
        //docName =

        //C:\Users\scmacdon\source\repos\aws-doc-sdk-examples\resources\sample_files

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)

        // Populate the data members required for all tests
        sourceDoc = prop.getProperty("sourceDoc")
        bucketName = prop.getProperty("bucketName")
        docName = prop.getProperty("docName")
        */
    }

    @Test
    @Order(1)
    fun analyzeDocumentTest() = runBlocking {
        analyzeDoc(sourceDoc)
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun detectDocumentTextTest() = runBlocking {
        detectDocText(sourceDoc)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun detectDocumentTextS3Test() = runBlocking {
        detectDocTextS3(bucketName, docName)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun startDocumentAnalysisTest() = runBlocking {
        startDocAnalysisS3(bucketName, docName)
        println("Test 4 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/textract"
        val valueRequest= GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/textract (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val sourceDoc: String? = null
        val bucketName: String? = null
        val docName: String? = null
    }
}
