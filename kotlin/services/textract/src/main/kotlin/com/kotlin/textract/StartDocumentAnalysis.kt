// snippet-sourcedescription:[StartDocumentAnalysis.kt demonstrates how to start the asynchronous analysis of a document.]
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

// snippet-start:[textract.kotlin._start_doc_analysis.import]
import aws.sdk.kotlin.services.textract.TextractClient
import aws.sdk.kotlin.services.textract.model.GetDocumentAnalysisRequest
import aws.sdk.kotlin.services.textract.model.FeatureType
import aws.sdk.kotlin.services.textract.model.DocumentLocation
import aws.sdk.kotlin.services.textract.model.S3Object
import aws.sdk.kotlin.services.textract.model.StartDocumentAnalysisRequest
import kotlin.system.exitProcess
// snippet-end:[textract.kotlin._start_doc_analysis.import]

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
        exitProcess(1)
     }

    val bucketName = args[0]
    val docName =  args[1]
    startDocAnalysisS3(bucketName, docName)
}

// snippet-start:[textract.kotlin._start_doc_analysis.main]
suspend fun startDocAnalysisS3(bucketName: String?, docName: String?) {

    val myList = mutableListOf<FeatureType>()
    myList.add(FeatureType.Tables)
    myList.add(FeatureType.Forms)

    val s3ObjectOb = S3Object {
        bucket = bucketName
        name = docName
    }

    val location = DocumentLocation {
        s3Object = s3ObjectOb
    }

    val documentAnalysisRequest = StartDocumentAnalysisRequest {
        documentLocation = location
        featureTypes = myList
    }

    TextractClient { region = "us-west-2" }.use { textractClient ->
        val response = textractClient.startDocumentAnalysis(documentAnalysisRequest)

        // Get the job ID.
        val jobId = response.jobId
        val result = getJobResults(textractClient, jobId)
        println("The status of the job is: $result")
    }
}

private suspend fun getJobResults(textractClient: TextractClient, jobIdVal: String?): String {

    val analysisRequest = GetDocumentAnalysisRequest {
        jobId = jobIdVal
        maxResults = 1000
    }
    val response = textractClient.getDocumentAnalysis(analysisRequest)
    return response.jobStatus.toString()
}
// snippet-end:[textract.kotlin._start_doc_analysis.main]