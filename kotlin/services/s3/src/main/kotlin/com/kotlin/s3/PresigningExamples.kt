package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketNotExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectNotExists
import aws.smithy.kotlin.runtime.auth.awssigning.crt.CrtAwsSigner
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.decodeToString
import aws.smithy.kotlin.runtime.time.Instant
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.hours

val logger = LoggerFactory.getLogger("com.kotlin.s3PresigningExamples")

/**
 * Be sure that you have an active portal session using 'aws sso login'.
 * See https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup-basic-onetime-setup.html#setup-login-sso.
 */
fun main(): Unit = runBlocking {

    val s3 = S3Client.fromEnvironment()
    val bucketName = UUID.randomUUID().toString()
    val keyName = "bar"

    try {
        setUp(s3, bucketName)

        putObject(s3, bucketName, keyName, "body")
        getObjectPresigned(s3, bucketName, keyName)
        deleteObject(s3, bucketName, keyName)

        putObject(s3, bucketName, keyName, "body")
        try {
            getObjectPresignedMoreOptions(s3, bucketName, keyName)
        } catch (e: Exception) {
            logger.error(e.message, e)
            deleteObject(s3, bucketName, keyName)
        }


        putObjectPresigned(s3, bucketName, keyName, "Hello World")
        deleteObject(s3, bucketName, keyName)
    } finally {
        cleanup(s3, bucketName)
    }
    exitProcess(1)
}

suspend fun getObjectPresigned(s3:S3Client, bucketName:String, keyName: String): String {
    // Create a GetObjectRequest.
    val unsignedRequest = GetObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the GetObject request.
    val presignedRequest = s3.presignGetObject(unsignedRequest, 24.hours)

    // Use the URL from the presigned HttpRequest in a subsequent call to retrieve the object.
    val objectContents = URL(presignedRequest.url.toString()).readText()


    return objectContents

}

suspend fun putObjectPresigned(s3:S3Client, bucketName:String, keyName: String, content: String){
    // Create a PutObjectRequest.
    val unsignedRequest = PutObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the request.
    val presignedRequest = s3.presignPutObject(unsignedRequest, 24.hours)

    // Use the URL and any headers from the presigned HttpRequest in a subsequent call to retrieve the object.
    // Create a PUT request using the OKHttpClient API.
    val putRequest = Request
        .Builder()
        .url(presignedRequest.url.toString())
        .apply {
            presignedRequest.headers.forEach { key, values ->
                header(key, values.joinToString(", "))
            }
        }
        .put(content.toRequestBody())
        .build()

    val response = OkHttpClient().newCall(putRequest).execute()
}

suspend fun getObjectPresignedMoreOptions(s3:S3Client, bucketName:String, keyName: String): String {
    // Create a GetObjectRequest.
    val unsignedRequest = GetObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the GetObject request.
    val presignedRequest = s3.presignGetObject(unsignedRequest, signer = CrtAwsSigner) {
        signingDate = Instant.now() + 24.hours
        expiresAfter = 8.hours
    }

    // Use the URL from the presigned HttpRequest in a subsequent call to retrieve the object.
    val putRequest = Request
        .Builder()
        .url(presignedRequest.url.toString())
        .apply {
            presignedRequest.headers.forEach { key, values ->
                header(key, values.joinToString(", "))
            }
        }
        .get()
        .build()

    val response = OkHttpClient().newCall(putRequest).execute()
    return response.body.toString()
/*
    val objectContents = URL(presignedRequest.url.toString()).readText()
    return objectContents
*/
}

suspend fun cleanup( s3: S3Client, bucketName: String) {
    deleteBucket(s3, bucketName)
}

suspend fun deleteBucket(s3: S3Client, bucketName: String) {
    s3.deleteBucket(DeleteBucketRequest {
        bucket = bucketName
    })

    s3.waitUntilBucketNotExists { bucket = bucketName }
}

suspend fun deleteObject( s3: S3Client, bucketName: String, keyName: String) {
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

suspend fun putObject(s3: S3Client, bucketName: String, keyName: String, contents: String) {
    s3.putObject(PutObjectRequest {
        bucket = bucketName
        key = keyName
        body = ByteStream.fromString(contents)
    })
    s3.waitUntilObjectExists {
        bucket = bucketName
        key = keyName
    }
}

