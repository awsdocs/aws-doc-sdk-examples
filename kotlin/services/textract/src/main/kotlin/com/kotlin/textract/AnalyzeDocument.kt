// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.textract

// snippet-start:[textract.kotlin._analyze_doc.import]
import aws.sdk.kotlin.services.textract.TextractClient
import aws.sdk.kotlin.services.textract.model.AnalyzeDocumentRequest
import aws.sdk.kotlin.services.textract.model.Document
import aws.sdk.kotlin.services.textract.model.FeatureType
import java.io.File
import java.io.FileInputStream
import kotlin.system.exitProcess
// snippet-end:[textract.kotlin._analyze_doc.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <sourceDoc> 

    Where:
        sourceDoc - The path where the document is located (must be an image, for example, C:/AWS/book.png). 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val sourceDoc = args[0]
    analyzeDoc(sourceDoc)
}

// snippet-start:[textract.kotlin._analyze_doc.main]
suspend fun analyzeDoc(sourceDoc: String?) {
    val sourceStream = FileInputStream(File(sourceDoc))
    val sourceBytes = sourceStream.readBytes()

    // Get the input Document object as bytes.
    val myDoc =
        Document {
            bytes = sourceBytes
        }

    val featureTypesOb = mutableListOf<FeatureType>()
    featureTypesOb.add(FeatureType.Forms)
    featureTypesOb.add(FeatureType.Tables)

    val analyzeDocumentRequest =
        AnalyzeDocumentRequest {
            featureTypes = featureTypesOb
            document = myDoc
        }

    TextractClient { region = "us-east-1" }.use { textractClient ->
        val response = textractClient.analyzeDocument(analyzeDocumentRequest)
        response.blocks?.forEach { block ->
            println("The block type is ${block.blockType}")
        }
    }
}
// snippet-end:[textract.kotlin._analyze_doc.main]
