// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import com.example.photo.PhotoApplicationResources
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class S3Service {
    // Place the upload image into an Amazon S3 bucket
    suspend fun putObject(data: ByteArray, objectKey: String) {
        val request = PutObjectRequest {
            bucket = PhotoApplicationResources.STORAGE_BUCKET
            key = objectKey
            body = ByteStream.fromBytes(data)
        }

        S3Client { region = "us-east-1" }.use { s3Client ->
            s3Client.putObject(request)
        }
    }

    // Place the upload image into an Amazon S3 bucket.
    suspend fun putZIP(data: ByteArray, objectKey: String) {
        val request = PutObjectRequest {
            bucket = PhotoApplicationResources.WORKING_BUCKET
            key = objectKey
            body = ByteStream.fromBytes(data)
        }

        S3Client { region = "us-east-1" }.use { s3Client ->
            s3Client.putObject(request)
        }
    }

    suspend fun getObjectBytes(bucketName: String?, keyName: String?): ByteArray? {
        var myBytes: ByteArray? = null
        val objectRequest = GetObjectRequest {
            key = keyName
            bucket = bucketName
        }

        S3Client { region = "us-east-1" }.use { s3Client ->
            s3Client.getObject(objectRequest) { resp ->
                myBytes = resp.body?.toByteArray()
            }
            return myBytes
        }
    }

    // Pass a map and get back a byte[] that represents a ZIP of all images.
    fun createZipFile(files: HashMap<String, ByteArray>): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
        for ((fileName, fileData) in files) {
            if (fileName == null) {
                continue
            }
            val entry = ZipEntry(fileName)
            zipOutputStream.putNextEntry(entry)
            zipOutputStream.write(fileData)
            zipOutputStream.closeEntry()
        }
        zipOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }

    suspend fun signObjectToDownload(keyName: String?): String? {
        S3Client { region = "us-east-1" }.use { s3Client ->
            val unsignedRequest = GetObjectRequest {
                bucket = PhotoApplicationResources.WORKING_BUCKET
                key = keyName
            }

            // Presign the GetObject request.
            val presignedRequest = s3Client.presignGetObject(unsignedRequest, 1.hours)
            val presignedUrl = URL(presignedRequest.url.toString()).readText()
            println(presignedUrl)
            return presignedUrl
        }
    }

    suspend fun signObjectToUpload(keyName: String?): String {
        S3Client { region = "us-east-1" }.use { s3Client ->
            val presignedUrl = PutObjectRequest {
                bucket = PhotoApplicationResources.WORKING_BUCKET
                key = keyName
                contentType = "image/jpeg"
            }

            val presignedRequest = s3Client.presignPutObject(presignedUrl, 5L.minutes)
            println(presignedRequest.url.toString())
            return presignedRequest.url.toString()
        }
    }
}
