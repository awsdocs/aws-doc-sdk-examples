/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.kotlin

import org.springframework.stereotype.Component
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.sns.model.*
import aws.sdk.kotlin.services.translate.model.TranslateTextRequest
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.system.exitProcess

@Component
class SnsService {

    var topicArnVal = "arn:aws:sns:us-west-2:814548047983:MyMailTopic"

    private fun getClient(): SnsClient {

        val snsClient = SnsClient{ region = "us-west-2" }
        return snsClient
    }

    // Create a Subscription.
    suspend fun subEmail(email: String?): String? {

        val snsClient: SnsClient = getClient()
        try {

            val request = SubscribeRequest {
                protocol = "email"
                endpoint = email
                returnSubscriptionArn = true
                topicArn = topicArnVal
            }

            val result = snsClient.subscribe(request)
            return result.subscriptionArn

        } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
            exitProcess(0)
        }
    }

    suspend fun pubTopic(messageVal: String, lang:String):String {

        val snsClient: SnsClient = getClient()
        var body: String

        val translateClient = TranslateClient {
            region = "us-west-2"
        }

        try {

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

            val result = snsClient.publish(request)
            return "{$result.messageId.toString()}  message sent successfully in $lang."

        } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
            exitProcess(0)
        }
    }

    suspend fun unSubEmail(emailEndpoint: String) {
        val snsClient: SnsClient = getClient()
        try {
            val subscriptionArnVal = getTopicArnValue(emailEndpoint)


            val request = UnsubscribeRequest {
                subscriptionArn = subscriptionArnVal
            }

            snsClient.unsubscribe(request)

        } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
            exitProcess(0)
        }
    }


    // Returns the Sub ARN based on the given endpoint used for unSub.
    suspend fun getTopicArnValue(endpoint: String): String? {
        val snsClient: SnsClient = getClient()
        try {
            var subArn: String

            val request = ListSubscriptionsByTopicRequest {
                topicArn = topicArnVal
            }

            val response = snsClient.listSubscriptionsByTopic(request)
            response.subscriptions?.forEach { sub ->

                    if (sub.endpoint?.compareTo(endpoint) ==0 ) {
                        subArn = sub.subscriptionArn.toString()
                        return subArn
                    }
            }

            return ""
        } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
            exitProcess(0)
        }
    }

    suspend fun getAllSubscriptions(): String? {
        val subList = mutableListOf<String>()
        val snsClient: SnsClient = getClient()

        try {

            val request = ListSubscriptionsByTopicRequest {
                topicArn = topicArnVal
            }
            val response = snsClient.listSubscriptionsByTopic(request)
            response.subscriptions?.forEach { sub ->
                              subList.add(sub.endpoint.toString())
            }

            return convertToString(toXml(subList))
        } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
            exitProcess(0)
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

                // Set email
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
