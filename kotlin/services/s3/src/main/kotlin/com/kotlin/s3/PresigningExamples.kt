// snippet-sourcedescription:[PresignExamples.kt demonstrates how to presign requests for an Amazon Simple Storage Service (Amazon S3) operations.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.s3

// snippet-start:[s3.kotlin.presign.import]
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteBucketRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketNotExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectNotExists
import aws.smithy.kotlin.runtime.auth.awssigning.AwsSignatureType
import aws.smithy.kotlin.runtime.auth.awssigning.AwsSigningAlgorithm
import aws.smithy.kotlin.runtime.auth.awssigning.crt.CrtAwsSigner
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.http.request.HttpRequest
import aws.smithy.kotlin.runtime.time.Instant
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.util.UUID
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.hours
// snippet-end:[s3.kotlin.presign.import]

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

        getObjectPresignedMoreOptions(s3, bucketName, keyName)

        putObjectPresigned(s3, bucketName, keyName, "Hello World")
        deleteObject(s3, bucketName, keyName)
    } finally {
        cleanup(s3, bucketName)
    }
    exitProcess(1)
}

// snippet-start:[s3.kotlin.presign_getObject.main]
suspend fun getObjectPresigned(s3: S3Client, bucketName: String, keyName: String): String {
    // Create a GetObjectRequest.
    val unsignedRequest = GetObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the GetObject request.
    val presignedRequest = s3.presignGetObject(unsignedRequest, 24.hours)

    // Use the URL from the presigned HttpRequest in a subsequent HTTP GET request to retrieve the object.
    val objectContents = URL(presignedRequest.url.toString()).readText()

    return objectContents
}
// snippet-end:[s3.kotlin.presign_getObject.main]

// snippet-start:[s3.kotlin.presign_putObject.main]
suspend fun putObjectPresigned(s3: S3Client, bucketName: String, keyName: String, content: String) {
    // Create a PutObjectRequest.
    val unsignedRequest = PutObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the request.
    val presignedRequest = s3.presignPutObject(unsignedRequest, 24.hours)

    // Use the URL and any headers from the presigned HttpRequest in a subsequent HTTP PUT request to retrieve the object.
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
    assert(response.isSuccessful)
}
// snippet-end:[s3.kotlin.presign_putObject.main]

// snippet-start:[s3.kotlin.presign_getObjectMoreOptions.main]
suspend fun getObjectPresignedMoreOptions(s3: S3Client, bucketName: String, keyName: String): HttpRequest {
    // Create a GetObjectRequest.
    val unsignedRequest = GetObjectRequest {
        bucket = bucketName
        key = keyName
    }

    // Presign the GetObject request.
    val presignedRequest = s3.presignGetObject(unsignedRequest, signer = CrtAwsSigner) {
        signingDate = Instant.now() + 12.hours // Presigned request can be used 12 hours from now.
        algorithm = AwsSigningAlgorithm.SIGV4_ASYMMETRIC
        signatureType = AwsSignatureType.HTTP_REQUEST_VIA_QUERY_PARAMS
        expiresAfter = 8.hours // Presigned request expires 8 hours later.
    }
    return presignedRequest
}
// snippet-end:[s3.kotlin.presign_getObjectMoreOptions.main]

suspend fun cleanup(s3: S3Client, bucketName: String) {
    deleteBucket(s3, bucketName)
}

suspend fun deleteBucket(s3: S3Client, bucketName: String) {
    s3.deleteBucket(
        DeleteBucketRequest {
            bucket = bucketName
        }
    )

    s3.waitUntilBucketNotExists { bucket = bucketName }
}

suspend fun deleteObject(s3: S3Client, bucketName: String, keyName: String) {
    s3.deleteObject(
        DeleteObjectRequest {
            bucket = bucketName
            key = keyName
        }
    )

    s3.waitUntilObjectNotExists {
        bucket = bucketName
        key = keyName
    }
}

suspend fun setUp(s3: S3Client, bucketName: String) {
    s3.createBucket(
        CreateBucketRequest {
            bucket = bucketName
        }
    )
    s3.waitUntilBucketExists { bucket = bucketName }
}

suspend fun putObject(s3: S3Client, bucketName: String, keyName: String, contents: String) {
    s3.putObject(
        PutObjectRequest {
            bucket = bucketName
            key = keyName
            body = ByteStream.fromString(contents)
        }
    )
    s3.waitUntilObjectExists {
        bucket = bucketName
        key = keyName
    }
}
