// snippet-sourcedescription:[SetTopicAttributes.kt demonstrates how to set attributes for an Amazon Simple Notification Service (Amazon SNS) topic.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Simple Notification Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sns

// snippet-start:[sns.kotlin.SetTopicAttributes.import]
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.SetTopicAttributesRequest
import kotlin.system.exitProcess
// snippet-end:[sns.kotlin.SetTopicAttributes.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
      Usage: 
        <attribute> <topicArn> <value>

      Where:
        attribute - The attribute action to use. Valid parameters are: Policy | DisplayName | DeliveryPolicy .
        topicArn - The ARN of the topic. 
        value - The value for the attribute.
    """

    if (args.size < 3) {
        println(usage)
        exitProcess(0)
    }

    val attribute = args[0]
    val topicArn = args[1]
    val value = args[2]
    setTopAttr(attribute, topicArn, value)
}

// snippet-start:[sns.kotlin.SetTopicAttributes.main]
suspend fun setTopAttr(attribute: String?, topicArnVal: String?, value: String?) {

    val request = SetTopicAttributesRequest {
        attributeName = attribute
        attributeValue = value
        topicArn = topicArnVal
    }

    SnsClient { region = "us-east-1" }.use { snsClient ->
        snsClient.setTopicAttributes(request)
        println("Topic ${request.topicArn} was updated.")
    }
}
// snippet-end:[sns.kotlin.SetTopicAttributes.main]
