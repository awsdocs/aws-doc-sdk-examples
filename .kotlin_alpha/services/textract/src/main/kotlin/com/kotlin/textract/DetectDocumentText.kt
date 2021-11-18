// snippet-sourcedescription:[DetectDocumentText.kt demonstrates how to detect text in the input document.]
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

// snippet-start:[textract.kotlin._detect_doc_text.import]
import aws.sdk.kotlin.services.textract.TextractClient
import aws.sdk.kotlin.services.textract.model.Document
import aws.sdk.kotlin.services.textract.model.TextractException
import aws.sdk.kotlin.services.textract.model.DetectDocumentTextRequest
import aws.sdk.kotlin.services.textract.model.Block
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import kotlin.system.exitProcess
// snippet-end:[textract.kotlin._detect_doc_text.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <sourceDoc> 

    Where:
        sourceDoc - the path where the document is located (must be an image, for example, C:/AWS/book.png). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
     }

    val sourceDoc =  args[0]
    val textractClient = TextractClient{ region = "us-west-2"}
    detectDocText(textractClient, sourceDoc)
    textractClient.close()
}

// snippet-start:[textract.kotlin._detect_doc_text.main]
suspend fun detectDocText(textractClient: TextractClient, sourceDoc: String) {
    try {
        val sourceStream = FileInputStream(File(sourceDoc))
        val sourceBytes = sourceStream.readBytes()

        // Get the input Document object as bytes
        val myDoc = Document {
            bytes = sourceBytes
        }

        val detectDocumentTextRequest = DetectDocumentTextRequest {
            document = myDoc
        }

        val response = textractClient.detectDocumentText(detectDocumentTextRequest)
        response.blocks?.forEach { block ->
            println("The block type is ${block.blockType.toString()}")
        }

        val documentMetadata = response.documentMetadata
        if (documentMetadata != null) {
            println("The number of pages in the document is ${documentMetadata.pages}")
        }

    } catch (ex: TextractException) {
        println(ex.message)
        textractClient.close()
        exitProcess(0)
    } catch (e: FileNotFoundException) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[textract.kotlin._detect_doc_text.main]