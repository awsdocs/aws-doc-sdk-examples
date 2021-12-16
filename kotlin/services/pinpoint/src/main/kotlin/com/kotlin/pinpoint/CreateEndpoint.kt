//snippet-sourcedescription:[CreateEndpoint.kt demonstrates how to create an endpoint for an application in Amazon Pinpoint.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pinpoint

//snippet-start:[pinpoint.kotlin.createendpoint.import]
import aws.sdk.kotlin.services.pinpoint.PinpointClient
import aws.sdk.kotlin.services.pinpoint.model.UpdateEndpointRequest
import aws.sdk.kotlin.services.pinpoint.model.GetEndpointRequest
import aws.sdk.kotlin.services.pinpoint.model.EndpointRequest
import aws.sdk.kotlin.services.pinpoint.model.EndpointDemographic
import aws.sdk.kotlin.services.pinpoint.model.EndpointLocation
import aws.sdk.kotlin.services.pinpoint.model.ChannelType
import aws.sdk.kotlin.services.pinpoint.model.EndpointUser
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.Date
import kotlin.system.exitProcess
//snippet-end:[pinpoint.kotlin.createendpoint.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage: 
        <appId> 

    Where:
         appId - the Id value of the application to create an endpoint for.
      """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val appId =  args[0]
    val endId = createPinpointEndpoint(appId)
    if (endId != null)
        println("The Endpoint id is: ${endId}")
     }

//snippet-start:[pinpoint.kotlin.createendpoint.main]
suspend fun createPinpointEndpoint(applicationIdVal: String?): String? {

        val endpointIdVal = UUID.randomUUID().toString()
        println("Endpoint ID: $endpointIdVal")

       val endpointRequestOb = createEndpointRequestData()
       val updateEndpointRequest = UpdateEndpointRequest {
           applicationId = applicationIdVal
           endpointId = endpointIdVal
           endpointRequest = endpointRequestOb
       }

       PinpointClient { region = "us-west-2" }.use { pinpoint ->

         val updateEndpointResponse = pinpoint.updateEndpoint(updateEndpointRequest)
         println("Update Endpoint Response ${updateEndpointResponse.messageBody}")

         val getEndpointRequest = GetEndpointRequest {
            applicationId = applicationIdVal
            endpointId = endpointIdVal
         }

         val endpointResponse = pinpoint.getEndpoint(getEndpointRequest)
         println(endpointResponse.endpointResponse?.address)
         println(endpointResponse.endpointResponse?.channelType)
         println(endpointResponse.endpointResponse?.applicationId)
         println(endpointResponse.endpointResponse?.endpointStatus)
         println(endpointResponse.endpointResponse?.requestId)
         println(endpointResponse.endpointResponse?.user)

         // Return the endpoint Id value.
         return endpointResponse.endpointResponse?.id
        }
     }

    private fun createEndpointRequestData(): EndpointRequest? {

            val favoriteTeams = mutableListOf<String>()
            favoriteTeams.add("Lakers")
            favoriteTeams.add("Warriors")

            val customAttributes = mutableMapOf<String, List<String>>()
            customAttributes["team"] = favoriteTeams

            val demographicOb =  EndpointDemographic {
                appVersion = "1.0"
                make = "apple"
                model = "iPhone"
                modelVersion = "7"
                platform ="ios"
                platformVersion = "10.1.1"
                timezone  = "America/Los_Angeles"
            }

            val locationOb = EndpointLocation {
                city ="Los Angeles"
                country = "US"
                latitude = 34.0
                longitude = -118.2
                postalCode = "90068"
                region ="CA"
            }

            val metricsMap = mutableMapOf<String, Double>()
            metricsMap["health"] = 100.00
            metricsMap["luck"] = 75.00

            val userOb = EndpointUser {
                userId = UUID.randomUUID().toString()
            }

            val df: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'") // Quoted "Z" to indicate UTC, no timezone offset
            val nowAsISO = df.format(Date())

            return EndpointRequest {
                address = UUID.randomUUID().toString()
                attributes = customAttributes
                channelType = ChannelType.Apns
                demographic = demographicOb
                effectiveDate = nowAsISO
                location = locationOb
                metrics = metricsMap
                optOut ="NONE"
                requestId = UUID.randomUUID().toString()
                user = userOb
            }
    }
//snippet-end:[pinpoint.kotlin.createendpoint.main]