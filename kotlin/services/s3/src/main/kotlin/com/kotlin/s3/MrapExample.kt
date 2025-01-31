// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.deleteBucket
import aws.sdk.kotlin.services.s3.deleteObject
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.putObject
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilBucketNotExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectExists
import aws.sdk.kotlin.services.s3.waiters.waitUntilObjectNotExists
import aws.sdk.kotlin.services.s3.withConfig
import aws.sdk.kotlin.services.s3control.S3ControlClient
import aws.sdk.kotlin.services.s3control.createMultiRegionAccessPoint
import aws.sdk.kotlin.services.s3control.model.CreateMultiRegionAccessPointResponse
import aws.sdk.kotlin.services.s3control.model.DeleteMultiRegionAccessPointRequest
import aws.sdk.kotlin.services.s3control.model.DeleteMultiRegionAccessPointResponse
import aws.sdk.kotlin.services.s3control.model.DescribeMultiRegionAccessPointOperationRequest
import aws.sdk.kotlin.services.s3control.model.DescribeMultiRegionAccessPointOperationResponse
import aws.sdk.kotlin.services.s3control.model.GetMultiRegionAccessPointRequest
import aws.sdk.kotlin.services.s3control.model.Region
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.getCallerIdentity
import aws.sdk.kotlin.services.sts.model.GetCallerIdentityRequest
import aws.smithy.kotlin.runtime.auth.awssigning.crt.CrtAwsSigner
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.decodeToString
import aws.smithy.kotlin.runtime.http.auth.SigV4AsymmetricAuthScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun main(): Unit = runBlocking {
    val mrapExample = MrapExample()
    val s3: S3Client = MrapExample.createS3Client()
    val s3Control = MrapExample.createS3ControlClient()
    val bucketName1 = "mrap-us-east-1-" + UUID.randomUUID().toString()
    val bucketName2 = "mrap-us-west-1-" + UUID.randomUUID().toString()
    val accountId = MrapExample.getAccountId()
    val keyName = "my-key"
    val mrapName = "mrap-test"
    val stringToPut = "Hello World"
    var mrapArn = ""

    try {
        MrapExample.setUpTwoBuckets(s3, bucketName1, bucketName2)
        mrapArn = mrapExample.createMrap(s3Control, accountId, bucketName1, bucketName2, mrapName)
        mrapExample.putObjectUsingMrap(s3, mrapArn, keyName, stringToPut)
        val objectFromMrap = mrapExample.getObjectFromMrap(s3, mrapArn, keyName)
        assert(objectFromMrap == stringToPut)
    } catch (e: Exception) {
        println(e)
    } finally {
        mrapExample.deleteObjectUsingMrap(s3, mrapArn, keyName)
        mrapExample.deleteMrap(s3Control, accountId, mrapName)
        MrapExample.cleanupBuckets(s3, bucketName1, bucketName2)
    }
    s3.close()
    s3Control.close()
    exitProcess(1)
}

class MrapExample {
    // snippet-start:[s3.kotlin.mrap.create]
    suspend fun createMrap(
        s3Control: S3ControlClient,
        accountIdParam: String,
        bucketName1: String,
        bucketName2: String,
        mrapName: String,
    ): String {
        println("Creating MRAP ...")
        val createMrapResponse: CreateMultiRegionAccessPointResponse =
            s3Control.createMultiRegionAccessPoint {
                accountId = accountIdParam
                clientToken = UUID.randomUUID().toString()
                details {
                    name = mrapName
                    regions = listOf(
                        Region {
                            bucket = bucketName1
                        },
                        Region {
                            bucket = bucketName2
                        },
                    )
                }
            }
        val requestToken: String? = createMrapResponse.requestTokenArn

        // Use the request token to check for the status of the CreateMultiRegionAccessPoint operation.
        if (requestToken != null) {
            waitForSucceededStatus(s3Control, requestToken, accountIdParam)
            println("MRAP created")
        }

        val getMrapResponse =
            s3Control.getMultiRegionAccessPoint(
                input = GetMultiRegionAccessPointRequest {
                    accountId = accountIdParam
                    name = mrapName
                },
            )
        val mrapAlias = getMrapResponse.accessPoint?.alias
        return "arn:aws:s3::$accountIdParam:accesspoint/$mrapAlias"
    }
    // snippet-end:[s3.kotlin.mrap.create]

    // snippet-start:[s3.kotlin.mrap.delete]
    suspend fun deleteMrap(
        s3Control: S3ControlClient,
        accountIdParam: String,
        mrapName: String,
    ) {
        println("Deleting MRAP ...")
        val deleteMrapResponse: DeleteMultiRegionAccessPointResponse =
            s3Control.deleteMultiRegionAccessPoint(
                input = DeleteMultiRegionAccessPointRequest {
                    accountId = accountIdParam
                    details {
                        name = mrapName
                    }
                },
            )
        val requestToken: String? = deleteMrapResponse.requestTokenArn

        // Use the request token to check for the status of the DeleteMultiRegionAccessPoint operation.
        if (requestToken != null) {
            waitForSucceededStatus(s3Control, requestToken, accountIdParam, 10.seconds)
            println("MRAP deleted")
        }
    }
    // snippet-end:[s3.kotlin.mrap.delete]

    // snippet-start:[s3.kotlin.mrap.putobject]
    suspend fun putObjectUsingMrap(
        s3: S3Client,
        mrapArn: String,
        keyName: String,
        stringToPut: String,
    ) {
        s3.putObject {
            bucket = mrapArn
            key = keyName
            body = ByteStream.fromString(stringToPut)
        }
        s3.waitUntilObjectExists {
            bucket = mrapArn
            key = keyName
        }
        println("String object uploaded")
    }
    // snippet-end:[s3.kotlin.mrap.putobject]

    // snippet-start:[s3.kotlin.mrap.getobject]
    suspend fun getObjectFromMrap(
        s3: S3Client,
        mrapArn: String,
        keyName: String,
    ): String? {
        val request = GetObjectRequest {
            bucket = mrapArn // Use the ARN instead of the bucket name for object operations.
            key = keyName
        }

        var stringObj: String? = null
        s3.getObject(request) { resp ->
            stringObj = resp.body?.decodeToString()
            if (stringObj != null) {
                println("Successfully read $keyName from $mrapArn")
            }
        }
        return stringObj
    }
    // snippet-end:[s3.kotlin.mrap.getobject]

    // snippet-start:[s3.kotlin.mrap.deleteobject]
    suspend fun deleteObjectUsingMrap(
        s3: S3Client,
        mrapArn: String,
        keyName: String,
    ) {
        s3.deleteObject {
            bucket = mrapArn
            key = keyName
        }
        s3.waitUntilObjectNotExists {
            bucket = mrapArn
            key = keyName
        }
        println("String object deleted using MRAP ARN.")
    }
    // snippet-end:[s3.kotlin.mrap.deleteobject]

    companion object {
        // snippet-start:[s3.kotlin.mrap.create-s3client]
        suspend fun createS3Client(): S3Client {
            // Configure your S3Client to use the Asymmetric Sigv4 (Sigv4a) signing algorithm.
            val sigV4AScheme = SigV4AsymmetricAuthScheme(CrtAwsSigner)
            val s3 = S3Client.fromEnvironment {
                authSchemes = listOf(sigV4AScheme)
            }
            return s3
        }
        // snippet-end:[s3.kotlin.mrap.create-s3client]

        // snippet-start:[s3.kotlin.mrap.create-s3controlclient]
        suspend fun createS3ControlClient(): S3ControlClient {
            // Configure your S3ControlClient to send requests to US West (Oregon).
            val s3Control = S3ControlClient.fromEnvironment {
                region = "us-west-2"
            }
            return s3Control
        }
        // snippet-end:[s3.kotlin.mrap.create-s3controlclient]

        // snippet-start:[s3.kotlin.mrap.create-buckets]
        suspend fun setUpTwoBuckets(
            s3: S3Client,
            bucketName1: String,
            bucketName2: String,
        ) {
            println("Create two buckets in different regions.")
            // The shared aws config file configures the default Region to be us-east-1.
            s3.createBucket(
                CreateBucketRequest {
                    bucket = bucketName1
                },
            )
            s3.waitUntilBucketExists {
                bucket = bucketName1
            }
            println("  Bucket [$bucketName1] created.")

            // Override the S3Client to work with us-west-1 for the second bucket.
            s3.withConfig {
                region = "us-west-1"
            }.use { s3West ->
                s3West.createBucket(
                    CreateBucketRequest {
                        bucket = bucketName2
                        createBucketConfiguration = CreateBucketConfiguration {
                            locationConstraint = BucketLocationConstraint.UsWest1
                        }
                    },
                )
                s3West.waitUntilBucketExists {
                    bucket = bucketName2
                }
                println("  Bucket [$bucketName2] created.")
            }
        }
        // snippet-end:[s3.kotlin.mrap.create-buckets]

        suspend fun getAccountId(): String {
            StsClient.fromEnvironment().use { sts ->
                val callerIdentity = sts.getCallerIdentity { GetCallerIdentityRequest {} }
                return callerIdentity.account!!
            }
        }

        // snippet-start:[s3.kotlin.mrap.check-operation-status]
        suspend fun waitForSucceededStatus(
            s3Control: S3ControlClient,
            requestToken: String,
            accountIdParam: String,
            timeBetweenChecks: Duration = 1.minutes,
        ) {
            var describeResponse: DescribeMultiRegionAccessPointOperationResponse
            describeResponse = s3Control.describeMultiRegionAccessPointOperation(
                input = DescribeMultiRegionAccessPointOperationRequest {
                    accountId = accountIdParam
                    requestTokenArn = requestToken
                },
            )

            var status: String? = describeResponse.asyncOperation?.requestStatus
            while (status != "SUCCEEDED") {
                delay(timeBetweenChecks)
                describeResponse = s3Control.describeMultiRegionAccessPointOperation(
                    input = DescribeMultiRegionAccessPointOperationRequest {
                        accountId = accountIdParam
                        requestTokenArn = requestToken
                    },
                )
                status = describeResponse.asyncOperation?.requestStatus
                println(status)
            }
        }
        // snippet-end:[s3.kotlin.mrap.check-operation-status]

        suspend fun cleanupBuckets(
            s3: S3Client,
            bucketName1: String,
            bucketName2: String,
        ) {
            s3.deleteBucket { bucket = bucketName1 }
            s3.waitUntilBucketNotExists { bucket = bucketName1 }
            println("Bucket $bucketName1 deleted.")
            s3.withConfig { region = "us-west-1" }.use { s3West ->
                s3West.deleteBucket { bucket = bucketName2 }
                s3West.waitUntilBucketNotExists { bucket = bucketName2 }
                println("Bucket $bucketName2 deleted.")
            }
        }
    }
}
