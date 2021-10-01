// snippet-sourcedescription:[DetectPPE.kt demonstrates how to detect Personal Protective Equipment (PPE) worn by people detected in an image.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06-08-2021]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.detect_ppe.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentSummarizationAttributes
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentType
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.DetectProtectiveEquipmentRequest
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentPerson
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentBodyPart
import aws.sdk.kotlin.services.rekognition.model.EquipmentDetection
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.detect_ppe.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
        Usage: 
            <sourceImage> 

        Where:
            "sourceImage - the name of the image in an Amazon S3 bucket (for example, people.png).
    """

     if (args.size != 1) {
         println(usage)
         exitProcess(0)
     }

    val sourceImage = args[0]
    val rekClient = RekognitionClient{ region = "us-east-1"}
    displayGear(rekClient, sourceImage)
    rekClient.close()
}

// snippet-start:[rekognition.kotlin.detect_ppe.main]
suspend fun displayGear(rekClient: RekognitionClient, sourceImage: String?) {

    try {
        val summarizationAttributesOb =  ProtectiveEquipmentSummarizationAttributes {
            minConfidence = 80f
            this.requiredEquipmentTypes = listOf(ProtectiveEquipmentType.fromValue("FACE_COVER"), ProtectiveEquipmentType.fromValue("HEAD_COVER"))
        }

        val souImage  = Image {
            bytes = (File(sourceImage).readBytes())
        }

        val request = DetectProtectiveEquipmentRequest {
            image = souImage
            summarizationAttributes  = summarizationAttributesOb
        }

        val result = rekClient.detectProtectiveEquipment(request)
        val persons = result.persons
        if (persons != null) {
            for (person: ProtectiveEquipmentPerson in persons) {
                println("ID: " + person.id)

                val bodyParts = person.bodyParts

                if (bodyParts != null) {
                    if (bodyParts.isEmpty()) {
                        println("\tNo body parts detected")
                    } else for (bodyPart: ProtectiveEquipmentBodyPart in bodyParts) {
                        println("${bodyPart.name.toString()}  -  Confidence: ${bodyPart.confidence.toString()}")

                        val equipmentDetections = bodyPart.equipmentDetections
                        if (equipmentDetections != null) {
                            if (equipmentDetections.isEmpty()) {
                                println("No PPE Detected on ${bodyPart.name}")
                            } else {
                                for (item: EquipmentDetection in equipmentDetections) {
                                    println("Item ${item.type.toString()}  - confidence: ${item.confidence.toString()}" )
                                    println("Covers body part:  ${item.coversBodyPart?.value.toString()}  - confidence is  ${item.coversBodyPart?.confidence.toString()}")
                                    println("\t\tBounding Box")
                                    val box = item.boundingBox
                                    if (box != null) {
                                        println("Left: ${box.left.toString()}")
                                    }
                                    if (box != null) {
                                        println("Top: ${box.top.toString()}")
                                    }
                                    if (box != null) {
                                        println("Width: ${box.width.toString()}")
                                    }
                                    if (box != null) {
                                        println("Height: ${box.height.toString()}")
                                    }
                                   println("Confidence: ${item.confidence.toString()}")
                                    println()
                                }
                            }
                        }
                    }
                }
            }
        }

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    }
}
// snippet-end:[rekognition.kotlin.detect_ppe.main]