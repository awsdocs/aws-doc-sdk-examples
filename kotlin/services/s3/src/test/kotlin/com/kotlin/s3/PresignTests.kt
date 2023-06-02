package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.client.LogMode
import aws.smithy.kotlin.runtime.content.decodeToString
import aws.smithy.kotlin.runtime.time.Instant
import aws.smithy.kotlin.runtime.time.toJvmInstant
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.LoggerFactory
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours

@TestMethodOrder(OrderAnnotation::class)
class PresignTests {
    val logger = LoggerFactory.getLogger("com.kotlin.s3.PresignTests")

    private val s3 = S3Client {
        region = "us-east-1"
        logMode = LogMode.LogRequestWithBody + LogMode.LogResponseWithBody
    }
    private val bucketName = UUID.randomUUID().toString()
    private val keyName = "bar"

    @BeforeEach
    fun setUp() = runBlocking {
        setUp(s3, bucketName)
    }

    @AfterEach
    fun cleanUp() = runBlocking {
        cleanup(s3, bucketName)
    }

    @Test
    @Order(1)
    fun getObjectPresignTest() = runBlocking {
        val contents = "body"
        logger.info("start getObjectPresignTest")
        // Put an object into the bucket.
        putObject(s3, bucketName, keyName, contents)
        val returnedContent = getObjectPresigned(s3, bucketName, keyName)
        deleteObject(s3, bucketName, keyName)
        Assertions.assertEquals(contents, returnedContent)
        logger.info("getObjectPresigned returned the same content")
    }

    @Test
    @Order(2)
    fun putObjectPresignTest() = runBlocking {
        val contents = "Hello World"
        logger.info("start putObjectPresignTest")
        putObjectPresigned(s3, bucketName, keyName, contents)

        val returnedContent = s3.getObject(
            GetObjectRequest {
                bucket = bucketName
                key = keyName
            }
        ) { resp ->
            val respString = resp.body?.decodeToString()
            respString
        }

        deleteObject(s3, bucketName, keyName)
        Assertions.assertEquals(contents, returnedContent)
        logger.info("putObjectPresigned returned the same content")
    }

    @Test
    @Order(3)
    fun getObjectPresignMoreOptionsTest() = runBlocking {
        // The example under test sets a future signing date of 12 hours from now.
        // The signing date ends up as the 'X-Amz-Date' query parameter on the URL.
        val presignedRequest = getObjectPresignedMoreOptions(s3, bucketName, keyName)

        val stringDate: String? = presignedRequest.url.parameters.get("X-Amz-Date")

        if (!stringDate.isNullOrEmpty()) {
            val signingDate = Instant.fromIso8601(stringDate.toString())
            val aroundNow = signingDate.minus(12.hours)
            val aroundNowJvmInstant: java.time.Instant = aroundNow.toJvmInstant()
            val nowJvmInstant: java.time.Instant = Instant.now().toJvmInstant()
            val difference: Long = aroundNowJvmInstant.until(nowJvmInstant, ChronoUnit.MILLIS)
            logger.info(difference.toString())
            // The difference between the signing date minus 12 hours and now should be very small.
            // Asserting here that the difference is less than 5 seconds.
            Assertions.assertTrue(difference < 5000)
        }
    }
}
