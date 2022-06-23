//snippet-sourcedescription:[AddQueueTags.kt demonstrates how to add tags to an Amazon Simple Queue Service (Amazon SQS) queue.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sqs

// snippet-start:[sqs.kotlin.add_tags.import]
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.GetQueueUrlRequest
import aws.sdk.kotlin.services.sqs.model.TagQueueRequest
import kotlin.system.exitProcess
// snippet-end:[sqs.kotlin.add_tags.import]

suspend fun main(args:Array<String>) {

    val usage = """
        Usage: 
            <queueName>
        Where:
            queueName - the name of the queue.

        """

      if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val queueName = args[0]
    addTags(queueName)

}

// snippet-start:[sqs.kotlin.add_tags.main]
suspend fun addTags(queueNameVal: String) {

        val urlRequest = GetQueueUrlRequest {
            queueName = queueNameVal
        }

        SqsClient { region = "us-east-1" }.use { sqsClient ->
          val getQueueUrlResponse = sqsClient.getQueueUrl(urlRequest)
          val queueUrlVal = getQueueUrlResponse.queueUrl

          val addedTags = mutableMapOf<String, String>()
          addedTags["Team"] = "Development"
          addedTags["Priority"] = "Beta"
          addedTags["Accounting ID"] = "456def"

          val tagQueueRequest = TagQueueRequest {
            queueUrl = queueUrlVal
            tags = addedTags
          }

          sqsClient.tagQueue(tagQueueRequest)
          println("Tags have been applied to $queueNameVal")
        }
}
// snippet-end:[sqs.kotlin.add_tags.main]