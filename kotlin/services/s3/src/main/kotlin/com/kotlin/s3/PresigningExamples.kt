package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketNotExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectNotExists
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.decodeToString
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.hours

val logger = LoggerFactory.getLogger("PresigningExamples")

fun main(): Unit = runBlocking {

    val s3 = S3Client.fromEnvironment()
    val bucketName = UUID.randomUUID().toString()
    val keyName = "bar"

    try {
        setUp(s3, bucketName)
        getObjectPresigned(s3, bucketName, keyName)
        putPresigned(s3, bucketName, keyName)
    } finally {
        cleanup(s3, bucketName, keyName)
    }
    exitProcess(1)
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

    deleteObject(s3, bucketName, keyName)
}

suspend fun putPresigned(s3:S3Client, bucketName:String, keyName: String) {


    // Create a PutObjectRequest.
    val unsignedRequest = PutObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the request.
    val presignedRequest = s3.presignPutObject(unsignedRequest, 24.hours)
    logger.info(presignedRequest.url.toString())
    presignedRequest.headers.forEach {key, values ->
        logger.info(key)
        logger.info(values.joinToString(", "))
    }

    // Use the URL from the presigned HttpRequest in a subsequent to retrieve the object.
    val putRequest = Request
        .Builder()
        .url(presignedRequest.url.toString())
        .apply {
            presignedRequest.headers.forEach { key, values ->
                header(key, values.joinToString(", "))
            }
        }
        .put("Hello world".toRequestBody())
        .build()

    val response = OkHttpClient().newCall(putRequest).execute()

        val theResp = s3.getObject(GetObjectRequest {
            bucket = bucketName
            key = keyName
        }) { resp ->
            val respString = resp.body?.decodeToString()
            logger.info(respString)
            respString
        }
    logger.info(theResp)
    deleteObject(s3, bucketName, keyName)
}

suspend fun cleanup( s3: S3Client, bucketName: String, keyName: String
) {
//    deleteObject(s3, bucketName, keyName)
    deleteBucket(s3, bucketName)
}

suspend fun deleteBucket(s3: S3Client, bucketName: String) {
    s3.deleteBucket(DeleteBucketRequest {
        bucket = bucketName
    })

    s3.waitUntilBucketNotExists { bucket = bucketName }
}

suspend fun deleteObject( s3: S3Client, bucketName: String, keyName: String
) {
    s3.deleteObject(DeleteObjectRequest {
        bucket = bucketName
        key = keyName
    })

    s3.waitUntilObjectNotExists {
        bucket = bucketName
        key = keyName
    }
}

suspend fun setUp(s3: S3Client, bucketName: String) {
    s3.createBucket(CreateBucketRequest {
        bucket = bucketName
    })
    s3.waitUntilBucketExists { bucket = bucketName }
}

