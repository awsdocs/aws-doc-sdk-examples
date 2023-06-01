package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.client.LogMode
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.decodeToString
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test


@TestMethodOrder(OrderAnnotation::class)
class PresignTests {
    val logger = LoggerFactory.getLogger("com.kotlin.s3.PresignTests")

    private val s3 = S3Client {
        region = "us-east-1"
        logMode = LogMode.LogRequestWithBody + LogMode.LogResponseWithBody
    }
    private val bucketName = UUID.randomUUID().toString()
    private val keyName = "bar"

    @BeforeAll
    fun setUp() = runBlocking {
        setUp(s3, bucketName)
    }

    @AfterAll
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
        logger.info("getObjectPresigned returned the same content" )
    }

    @Test
    @Order(2)
    fun putObjectPresignTest() = runBlocking {
        val contents = "Hello World"
        logger.info("start putObjectPresignTest")
        putObjectPresigned(s3, bucketName, keyName, contents)

        val returnedContent = s3.getObject(GetObjectRequest {
            bucket = bucketName
            key = keyName
        }) { resp ->
            val respString = resp.body?.decodeToString()
            respString
        }

        deleteObject(s3, bucketName, keyName)
        Assertions.assertEquals(contents, returnedContent)
        logger.info("putObjectPresigned returned the same content" )
    }
}