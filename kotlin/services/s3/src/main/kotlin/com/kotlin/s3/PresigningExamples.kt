package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketNotExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectNotExists
import aws.smithy.kotlin.runtime.content.ByteStream
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.hours

val logger = LoggerFactory.getLogger("LoggerName")

fun main(): Unit = runBlocking {

    val s3 = S3Client.fromEnvironment()
    val bucketName = UUID.randomUUID().toString()
    val keyName = "bar"

    try {
        setUp(s3, bucketName)
        getObjectPresigned(s3, bucketName, keyName)
    } finally {
        cleanup(s3, bucketName, keyName)
    }
    exitProcess(1)
}

suspend fun cleanup(
    s3: S3Client,
    bucketName: String,
    keyName: String
) {
    s3.deleteObject(DeleteObjectRequest {
        bucket = bucketName
        key = keyName
    })

    s3.deleteBucket(DeleteBucketRequest {
        bucket = bucketName
    })

    s3.waitUntilObjectNotExists {
        bucket = bucketName
        key = keyName
    }

    s3.waitUntilBucketNotExists { bucket = bucketName }
}

suspend fun setUp(s3: S3Client, bucketName: String) {
    s3.createBucket(CreateBucketRequest {
        bucket = bucketName
    })
    s3.waitUntilBucketExists { bucket = bucketName }
}

suspend fun getObjectPresigned(s3:S3Client, bucketName:String, keyName: String){

    // Put an object into the bucket.
    s3.putObject(PutObjectRequest {
        bucket = bucketName
        key = keyName
        body = ByteStream.fromString("body")
    })

    // Create a GetObjectRequest.
    val unsignedRequest = GetObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the request
    val presignedRequest = s3.presignGetObject(unsignedRequest, 24.hours)

    // Use the URL from the presigned HttpRequest in a subsequent to retrieve the object.
    val objectContents = URL(presignedRequest.url.toString()).readText()
    logger.info(objectContents)

}