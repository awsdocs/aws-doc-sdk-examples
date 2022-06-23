// snippet-sourcedescription:[DetectDocumentTextS3.kt demonstrates how to detect text in an input document located in an Amazon S3 bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Textract]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[07/16/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.textract

// snippet-start:[textract.kotlin._detect_s3_text.import]
import aws.sdk.kotlin.services.textract.TextractClient
import aws.sdk.kotlin.services.textract.model.S3Object
import aws.sdk.kotlin.services.textract.model.DetectDocumentTextRequest
import aws.sdk.kotlin.services.textract.model.Document
import kotlin.system.exitProcess
// snippet-end:[textract.kotlin._detect_s3_text.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
       <bucketName> <docName> 

    Where:
        bucketName - The name of the Amazon S3 bucket that contains the document.
        docName - The document name (must be an image, i.e., book.png). 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val bucketName = args[0]
    val docName = args[1]
    detectDocTextS3(bucketName, docName)
}

// snippet-start:[textract.kotlin._detect_s3_text.main]
suspend fun detectDocTextS3(bucketName: String?, docName: String?) {

    val s3ObjectOb = S3Object {
        bucket = bucketName
        name = docName
    }

    val myDoc = Document {
        s3Object = s3ObjectOb
    }

    val detectDocumentTextRequest = DetectDocumentTextRequest {
        document = myDoc
    }

    TextractClient { region = "us-west-2" }.use { textractClient ->
      val response = textractClient.detectDocumentText(detectDocumentTextRequest)
        response.blocks?.forEach { block ->
            println("The block type is ${block.blockType.toString()}")
        }

        val documentMetadata = response.documentMetadata
        if (documentMetadata != null) {
            println("The number of pages in the document is ${documentMetadata.pages}")
        }
    }
}
// snippet-end:[textract.kotlin._detect_s3_text.main]