// snippet-sourcedescription:[DetectPPE.kt demonstrates how to detect Personal Protective Equipment (PPE) worn by people detected in an image.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.detect_ppe.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.DetectProtectiveEquipmentRequest
import aws.sdk.kotlin.services.rekognition.model.EquipmentDetection
import aws.sdk.kotlin.services.rekognition.model.Image
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentBodyPart
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentSummarizationAttributes
import aws.sdk.kotlin.services.rekognition.model.ProtectiveEquipmentType
import java.io.File
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.detect_ppe.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <sourceImage> 

        Where:
            "sourceImage - The name of the image that contains PPE information (for example, people.png).
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val sourceImage = args[0]
    displayGear(sourceImage)
}

// snippet-start:[rekognition.kotlin.detect_ppe.main]
suspend fun displayGear(sourceImage: String) {

    val summarizationAttributesOb = ProtectiveEquipmentSummarizationAttributes {
        minConfidence = 80f
        this.requiredEquipmentTypes = listOf(ProtectiveEquipmentType.fromValue("FACE_COVER"), ProtectiveEquipmentType.fromValue("HEAD_COVER"))
    }

    val souImage = Image {
        bytes = (File(sourceImage).readBytes())
    }

    val request = DetectProtectiveEquipmentRequest {
        image = souImage
        summarizationAttributes = summarizationAttributesOb
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val response = rekClient.detectProtectiveEquipment(request)
        response.persons?.forEach { person ->
            println("ID: " + person.id)
            val bodyParts = person.bodyParts

            if (bodyParts != null) {
                if (bodyParts.isEmpty()) {
                    println("\tNo body parts detected")
                } else for (bodyPart: ProtectiveEquipmentBodyPart in bodyParts) {
                    println("${bodyPart.name}  -  Confidence: ${bodyPart.confidence}")

                    val equipmentDetections = bodyPart.equipmentDetections
                    if (equipmentDetections != null) {
                        if (equipmentDetections.isEmpty()) {
                            println("No PPE Detected on ${bodyPart.name}")
                        } else {
                            for (item: EquipmentDetection in equipmentDetections) {
                                println("Item ${item.type}  - confidence: ${item.confidence}")
                                println("Covers body part:  ${item.coversBodyPart?.value}  - confidence is  ${item.coversBodyPart?.confidence}")
                                println("\t\tBounding Box")
                                val box = item.boundingBox
                                if (box != null) {
                                    println("Left: ${box.left}")
                                    println("Top: ${box.top}")
                                    println("Width: ${box.width}")
                                    println("Height: ${box.height}")
                                    println("Confidence: ${item.confidence}")
                                    println()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// snippet-end:[rekognition.kotlin.detect_ppe.main]
