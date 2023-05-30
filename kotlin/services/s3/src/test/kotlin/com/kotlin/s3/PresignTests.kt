package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketNotExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectNotExists
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


class PresignTests {
    private val s3 = S3Client {
        region = "us-east-1"
    }
    private val bucketName = UUID.randomUUID().toString()
    private val keyName = "bar"

    @BeforeEach
    fun setUp() = runBlocking {
        setUp(s3, bucketName)
    }

    @AfterEach
    fun cleanUp() = runBlocking {
        cleanup(s3, bucketName, keyName)
    }

    @Test
    fun getObjectPresignTest() = runBlocking {
        getObjectPresigned(s3, bucketName, keyName)
    }
}