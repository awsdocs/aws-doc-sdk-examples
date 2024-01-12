// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.services

import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.PublishRequest
import com.example.photo.PhotoApplicationResources

class SnsService {
    suspend fun pubTopic(messageVal: String?) {
        val request = PublishRequest {
            message = messageVal
            topicArn = PhotoApplicationResources.TOPIC_ARN
        }
        SnsClient { region = "us-east-1" }.use { snsClient ->
            snsClient.publish(request)
        }
    }
}
