//snippet-sourcedescription:[UploadArchive.kt demonstrates how to upload an archive to an Amazon Glacier vault.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glacier

// snippet-start:[glacier.kotlin.upload.import]
import aws.sdk.kotlin.services.glacier.GlacierClient
import aws.sdk.kotlin.services.glacier.model.GlacierException
import aws.sdk.kotlin.services.glacier.model.UploadArchiveRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import java.io.File
import kotlin.system.exitProcess
// snippet-start:[glacier.kotlin.upload.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: <strPath> <vaultName> 

        Where:
            strPath - the path to the archive to upload (for example, C:\AWS\test.pdf).
            vaultName - the name of the vault.
        """

     if (args.size != 2) {
         println(usage)
         exitProcess(1)
     }

    val strPath = args[0]
    val vaultName = args[1]
    val myFile = File(strPath)
    val glacierClient = GlacierClient { region = "us-east-1" }
    uploadContent(glacierClient, vaultName, myFile)
    glacierClient.close()
}

// snippet-start:[glacier.kotlin.upload.main]
suspend fun uploadContent(glacier: GlacierClient, vaultNameVal: String?, myFile: File): String? {

    // Get an SHA-256 tree hash value
    val checkSum = CreateCheckSum()
    val checkVal = checkSum.computeSHA256(myFile)

    try {

        val myBytes = myFile.readBytes()
        val uploadRequest = UploadArchiveRequest {
            vaultName = vaultNameVal
            checksum = checkVal
            body = ByteStream.fromBytes(myBytes)
        }

        val res = glacier.uploadArchive(uploadRequest)
        return res.archiveId

    } catch (e: GlacierException) {
        println(e.message)
        glacier.close()
        exitProcess(0)
    }
}
// snippet-end:[glacier.kotlin.upload.main]

