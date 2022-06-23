/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.kotlin

import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.*
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.TranslateTextRequest
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@Component
class SnsService {

    var topicArnVal = "arn:aws:sns:us-west-2:814548047983:MyMailTopic"

    // Create a Subscription.
    suspend fun subEmail(email: String?): String? {

        val request = SubscribeRequest {
            protocol = "email"
            endpoint = email
            returnSubscriptionArn = true
            topicArn = topicArnVal
        }

        SnsClient { region = "us-west-2" }.use { snsClient ->
            val result = snsClient.subscribe(request)
            return result.subscriptionArn
        }
    }

    suspend fun pubTopic(messageVal: String, lang:String):String {

       val translateClient =  TranslateClient { region = "us-east-1" }
        val body: String

        if (lang.compareTo("English") == 0) {
             body = messageVal
        } else if (lang.compareTo("French") == 0) {
             val textRequest = TranslateTextRequest {
                 sourceLanguageCode = "en"
                  targetLanguageCode = "fr"
                  text = messageVal
             }

             val textResponse = translateClient.translateText(textRequest)
             body = textResponse.translatedText.toString()

        } else {
            val textRequest = TranslateTextRequest {
                sourceLanguageCode = "en"
                targetLanguageCode = "es"
                text = messageVal
            }

            val textResponse = translateClient.translateText(textRequest)
            body = textResponse.translatedText.toString()
        }

        val request = PublishRequest {
             message = body
             topicArn = topicArnVal
        }

        SnsClient { region = "us-west-2" }.use { snsClient ->
            val result = snsClient.publish(request)
            return "{$result.messageId.toString()}  message sent successfully in $lang."
        }
    }

    suspend fun unSubEmail(emailEndpoint: String) {

       val subscriptionArnVal = getTopicArnValue(emailEndpoint)
       val request = UnsubscribeRequest {
           subscriptionArn = subscriptionArnVal
       }

        SnsClient { region = "us-west-2" }.use { snsClient ->
            snsClient.unsubscribe(request)
        }
    }

    // Returns the Sub Amazon Resource Name (ARN) based on the given endpoint used for unSub.
    suspend fun getTopicArnValue(endpoint: String): String? {

       var subArn: String
       val request = ListSubscriptionsByTopicRequest {
           topicArn = topicArnVal
       }

        SnsClient { region = "us-west-2" }.use { snsClient ->
            val response = snsClient.listSubscriptionsByTopic(request)
            response.subscriptions?.forEach { sub ->
                 if (sub.endpoint?.compareTo(endpoint) ==0 ) {
                     subArn = sub.subscriptionArn.toString()
                     return subArn
                }
            }
            return ""
        }
    }

    suspend fun getAllSubscriptions(): String? {
        val subList = mutableListOf<String>()
        val request = ListSubscriptionsByTopicRequest {
             topicArn = topicArnVal
         }

        SnsClient { region = "us-west-2" }.use { snsClient ->
            val response = snsClient.listSubscriptionsByTopic(request)
            response.subscriptions?.forEach { sub ->
                              subList.add(sub.endpoint.toString())
            }
            return convertToString(toXml(subList))
        }
    }

    // Convert the list to XML to pass back to the view.
    private fun toXml(subsList: List<String>): Document? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()

            // Start building the XML.
            val root = doc.createElement("Subs")
            doc.appendChild(root)

            // Iterate through the collection.
            for (sub in subsList) {
                val item = doc.createElement("Sub")
                root.appendChild(item)

                // Set email.
                val email = doc.createElement("email")
                email.appendChild(doc.createTextNode(sub))
                item.appendChild(email)
            }
            return doc
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return null
    }

    private fun convertToString(xml: Document?): String? {
        try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            val result = StreamResult(StringWriter())
            val source = DOMSource(xml)
            transformer.transform(source, result)
            return result.writer.toString()
        } catch (ex: TransformerException) {
            ex.printStackTrace()
        }
        return null
    }
}