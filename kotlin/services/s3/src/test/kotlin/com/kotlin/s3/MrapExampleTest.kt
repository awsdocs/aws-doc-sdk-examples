// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3control.S3ControlClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.UUID

class MrapExampleTest {
    @Test
    fun testGetObjectUsingMrap(): Unit =
        runBlocking {
            val objectFromMrap = mrapExample.getObjectFromMrap(s3, mrapArn, keyName)
            Assertions.assertEquals(stringToPut, objectFromMrap)
        }

    companion object {
        val mrapExample = MrapExample()
        lateinit var s3: S3Client
        lateinit var s3Control: S3ControlClient
        lateinit var accountId: String
        val bucketName1 = "mrap-us-east-1-" + UUID.randomUUID().toString()
        val bucketName2 = "mrap-us-west-1-" + UUID.randomUUID().toString()
        val keyName = "my-key"
        val mrapName = "mrap-test"
        val stringToPut = "Hello World"
        lateinit var mrapArn: String

        @BeforeAll
        @JvmStatic
        internal fun beforeAll(): Unit =
            runBlocking {
                s3 = MrapExample.createS3Client()
                s3Control = MrapExample.createS3ControlClient()
                accountId = MrapExample.getAccountId()
                MrapExample.setUpTwoBuckets(s3, bucketName1, bucketName2)
                mrapArn = mrapExample.createMrap(s3Control, accountId, bucketName1, bucketName2, mrapName)
                mrapExample.putObjectUsingMrap(s3, mrapArn, keyName, stringToPut)
            }

        @AfterAll
        @JvmStatic
        internal fun afterAll(): Unit =
            runBlocking {
                mrapExample.deleteObjectUsingMrap(s3, mrapArn, keyName)
                mrapExample.deleteMrap(s3Control, accountId, mrapName)
                MrapExample.cleanupBuckets(s3, bucketName1, bucketName2)
            }
    }
}
