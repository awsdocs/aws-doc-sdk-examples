// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise.scenario

import aws.sdk.kotlin.services.iot.IotClient
import aws.sdk.kotlin.services.iot.model.CreateThingRequest
import aws.sdk.kotlin.services.iotfleetwise.IotFleetWiseClient
import aws.sdk.kotlin.services.iotfleetwise.model.Branch
import aws.sdk.kotlin.services.iotfleetwise.model.CanInterface
import aws.sdk.kotlin.services.iotfleetwise.model.CanSignal
import aws.sdk.kotlin.services.iotfleetwise.model.CreateDecoderManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.CreateFleetRequest
import aws.sdk.kotlin.services.iotfleetwise.model.CreateModelManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.CreateSignalCatalogRequest
import aws.sdk.kotlin.services.iotfleetwise.model.CreateVehicleRequest
import aws.sdk.kotlin.services.iotfleetwise.model.DeleteDecoderManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.DeleteFleetRequest
import aws.sdk.kotlin.services.iotfleetwise.model.DeleteModelManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.DeleteSignalCatalogRequest
import aws.sdk.kotlin.services.iotfleetwise.model.DeleteVehicleRequest
import aws.sdk.kotlin.services.iotfleetwise.model.GetDecoderManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.GetModelManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.GetVehicleRequest
import aws.sdk.kotlin.services.iotfleetwise.model.ListSignalCatalogNodesRequest
import aws.sdk.kotlin.services.iotfleetwise.model.ManifestStatus
import aws.sdk.kotlin.services.iotfleetwise.model.NetworkInterface
import aws.sdk.kotlin.services.iotfleetwise.model.NetworkInterfaceType
import aws.sdk.kotlin.services.iotfleetwise.model.Node
import aws.sdk.kotlin.services.iotfleetwise.model.NodeDataType
import aws.sdk.kotlin.services.iotfleetwise.model.Sensor
import aws.sdk.kotlin.services.iotfleetwise.model.SignalDecoder
import aws.sdk.kotlin.services.iotfleetwise.model.SignalDecoderType
import aws.sdk.kotlin.services.iotfleetwise.model.UpdateDecoderManifestRequest
import aws.sdk.kotlin.services.iotfleetwise.model.UpdateModelManifestRequest
import kotlinx.coroutines.delay
import java.util.Scanner

// snippet-start:[iotfleetwise.kotlin.scenario.main]
/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
var scanner = Scanner(System.`in`)
val DASHES = String(CharArray(80)).replace("\u0000", "-")
suspend fun main(args: Array<String>) {
    val usage =
        """
        Usage:
            <signalCatalogName> <manifestName> <fleetId> <vecName> <decName>
                        
        Where:
            signalCatalogName     - The name of the Signal Catalog to create (eg, catalog30).
            manifestName          - The name of the Vehicle Model (Model Manifest) to create (eg, manifest30).
            fleetId               - The ID of the Fleet to create (eg, fleet30).
            vecName               - The name of the Vehicle to create (eg, vehicle30).
            decName               - The name of the Decoder Manifest to create (eg, decManifest30).
                        
        """.trimIndent()

    if (args.size != 5) {
        println(usage)
        return
    }

    val signalCatalogName = args[0]
    val manifestName = args[1]
    val fleetId = args[2]
    val vecName = args[3]
    val decName = args[4]

    println(
        """
        AWS IoT FleetWise is a managed service that simplifies the 
        process of collecting, organizing, and transmitting vehicle 
        data to the cloud in near real-time. Designed for automakers 
        and fleet operators, it allows you to define vehicle models, 
        specify the exact data you want to collect (such as engine 
        temperature, speed, or battery status), and send this data to 
        AWS for analysis. By using intelligent data collection 
        techniques, IoT FleetWise reduces the volume of data 
        transmitted by filtering and transforming it at the edge, 
        helping to minimize bandwidth usage and costs. 
                
        At its core, AWS IoT FleetWise helps organizations build 
        scalable systems for vehicle data management and analytics, 
        supporting a wide variety of vehicles and sensor configurations. 
        You can define signal catalogs and decoder manifests that describe 
        how raw CAN bus signals are translated into readable data, making 
        the platform highly flexible and extensible. This allows 
        manufacturers to optimize vehicle performance, improve safety, 
        and reduce maintenance costs by gaining real-time visibility 
        into fleet operations. 
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    println(DASHES)
    runScenario(signalCatalogName, fleetId, manifestName, decName, vecName)
}

suspend fun runScenario(signalCatalogName: String, fleetIdVal: String, manifestName: String, decName: String, vecName: String) {
    println(DASHES)
    println("1. Creates a collection of standardized signals that can be reused to create vehicle models")
    waitForInputToContinue(scanner)
    val signalCatalogArn = createbranchVehicle(signalCatalogName)
    println("The collection ARN is $signalCatalogArn")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("2. Create a fleet that represents a group of vehicles")
    println(
        """
        Creating an IoT FleetWise fleet allows you to efficiently collect, 
        organize, and transfer vehicle data to the cloud, enabling real-time 
        insights into vehicle performance and health. 
                
        It helps reduce data costs by allowing you to filter and prioritize 
        only the most relevant vehicle signals, supporting advanced analytics 
        and predictive maintenance use cases.
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    val fleetid = createFleet(signalCatalogArn, fleetIdVal)
    println("The fleet Id is $fleetid")
    waitForInputToContinue(scanner)
    val nodeList = listSignalCatalogNode(signalCatalogName)
    println(DASHES)

    println(DASHES)
    println("3. Create a model manifest")
    println(
        """
        An AWS IoT FleetWise manifest defines the structure and 
        relationships of vehicle data. The model manifest specifies 
        which signals to collect and how they relate to vehicle systems, 
        while the decoder manifest defines how to decode raw vehicle data 
        into meaningful signals. 
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    val nodes = listSignalCatalogNode(signalCatalogName)
    val manifestArn = nodes?.let { createModelManifest(manifestName, signalCatalogArn, it) }
    println("The manifest ARN is $manifestArn")
    println(DASHES)

    println(DASHES)
    println("4. Create a decoder manifest")
    println(
        """
        A decoder manifest in AWS IoT FleetWise defines how raw vehicle 
        data (such as CAN signals) should be interpreted and decoded 
        into meaningful signals. It acts as a translation layer 
        that maps vehicle-specific protocols to standardized data formats
        using decoding rules. This is crucial for extracting usable
        data from different vehicle models, even when their data 
        formats vary.
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    val decArn = createDecoderManifest(decName, manifestArn)
    println("The decoder manifest ARN is $decArn")
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("5. Check the status of the model manifest")
    println(
        """
        The model manifest must be in an ACTIVE state before it can be used 
        to create or update a vehicle.
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    updateModelManifest(manifestName)
    waitForModelManifestActive(manifestName)
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("6. Check the status of the decoder")
    println(
        """
        The decoder manifest must be in an ACTIVE state before it can be used 
        to create or update a vehicle.
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    updateDecoderManifest(decName)
    waitForDecoderManifestActive(decName)
    waitForInputToContinue(scanner)
    println(DASHES)

    println(DASHES)
    println("7. Create an IoT Thing")
    println(
        """
        AWS IoT FleetWise expects an existing AWS IoT Thing with the same 
        name as the vehicle name you are passing to createVehicle method. 
        Before calling createVehicle(), you must create an AWS IoT Thing 
        with the same name using the AWS IoT Core service.
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    createThingIfNotExist(vecName)
    println(DASHES)

    println(DASHES)
    println("8. Create a vehicle")
    println(
        """
        Creating a vehicle in AWS IoT FleetWise allows you to digitally 
        represent and manage a physical vehicle within the AWS ecosystem. 
        This enables efficient ingestion, transformation, and transmission 
        of vehicle telemetry data to the cloud for analysis.
        """.trimIndent(),
    )
    waitForInputToContinue(scanner)
    createVehicle(vecName, manifestArn, decArn)
    println(DASHES)

    println(DASHES)
    println("9. Display vehicle details")
    waitForInputToContinue(scanner)
    getVehicleDetails(vecName)
    waitForInputToContinue(scanner)
    println(DASHES)
    println(DASHES)
    println("10. Delete the AWS IoT Fleetwise Assets")
    println("Would you like to delete the IoT Fleetwise Assets? (y/n)")
    val delAns = scanner.nextLine().trim()
    if (delAns.equals("y", ignoreCase = true)) {
        deleteVehicle(vecName)
        deleteDecoderManifest(decName)
        deleteModelManifest(manifestName)
        deleteFleet(fleetid)
        deleteSignalCatalog(signalCatalogName)
    }

    println(DASHES)
    println(
        """
        Thank you for checking out the AWS IoT Fleetwise Service Use demo. We hope you
        learned something new, or got some inspiration for your own apps today.
        For more AWS code examples, have a look at:
        https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html
        """.trimIndent(),
    )
    println(DASHES)
}

// snippet-start:[iotfleetwise.kotlin.delete.vehicle.main]
suspend fun deleteVehicle(vecName: String) {
    val request = DeleteVehicleRequest {
        vehicleName = vecName
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.deleteVehicle(request)
        println("Vehicle $vecName was deleted successfully.")
    }
}
// snippet-end:[iotfleetwise.kotlin.delete.vehicle.main]

// snippet-start:[iotfleetwise.kotlin.get.vehicle.main]
suspend fun getVehicleDetails(vehicleNameVal: String) {
    val request = GetVehicleRequest {
        vehicleName = vehicleNameVal
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.getVehicle(request)
        val details = mapOf(
            "vehicleName" to response.vehicleName,
            "arn" to response.arn,
            "modelManifestArn" to response.modelManifestArn,
            "decoderManifestArn" to response.decoderManifestArn,
            "attributes" to response.attributes.toString(),
            "creationTime" to response.creationTime.toString(),
            "lastModificationTime" to response.lastModificationTime.toString(),
        )

        println("Vehicle Details:")
        for ((key, value) in details) {
            println("• %-20s : %s".format(key, value))
        }
    }
}
// snippet-end:[iotfleetwise.kotlin.get.vehicle.main]

// snippet-start:[iotfleetwise.kotlin.create.vehicle.main]
suspend fun createVehicle(vecName: String, manifestArn: String?, decArn: String) {
    val request = CreateVehicleRequest {
        vehicleName = vecName
        modelManifestArn = manifestArn
        decoderManifestArn = decArn
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.createVehicle(request)
        println("Vehicle $vecName was created successfully.")
    }
}
// snippet-end:[iotfleetwise.kotlin.create.vehicle.main]

/**
 * Creates an IoT Thing if it does not already exist.
 *
 * @param vecName the name of the IoT Thing to create
 */
suspend fun createThingIfNotExist(vecName: String) {
    val request = CreateThingRequest {
        thingName = vecName
    }

    IotClient { region = "us-east-1" }.use { client ->
        client.createThing(request)
        println("The $vecName IoT Thing was successfully created")
    }
}

// snippet-start:[iotfleetwise.kotlin.update.decoder.main]
suspend fun updateDecoderManifest(nameVal: String) {
    val request = UpdateDecoderManifestRequest {
        name = nameVal
        status = ManifestStatus.Active
    }
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.updateDecoderManifest(request)
        println("$nameVal was successfully updated")
    }
}
// snippet-end:[iotfleetwise.kotlin.update.decoder.main]

// snippet-start:[iotfleetwise.kotlin.decoder.active.main]
/**
 * Waits for the specified model manifest to become active.
 *
 * @param decNameVal the name of the model manifest to wait for
 */
suspend fun waitForDecoderManifestActive(decNameVal: String) {
    var elapsedSeconds = 0
    var lastStatus: ManifestStatus = ManifestStatus.Draft

    print("⏳ Elapsed: 0s | Status: DRAFT")
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        while (true) {
            delay(1000)
            elapsedSeconds++
            if (elapsedSeconds % 5 == 0) {
                val request = GetDecoderManifestRequest {
                    name = decNameVal
                }

                val response = fleetwiseClient.getDecoderManifest(request)
                lastStatus = response.status ?: ManifestStatus.Draft

                when (lastStatus) {
                    ManifestStatus.Active -> {
                        print("\rElapsed: ${elapsedSeconds}s | Status: ACTIVE ✅\n")
                        return
                    }

                    ManifestStatus.Invalid -> {
                        print("\rElapsed: ${elapsedSeconds}s | Status: INVALID ❌\n")
                        throw RuntimeException("Model manifest became INVALID. Cannot proceed.")
                    }

                    else -> {
                        print("\r Elapsed: ${elapsedSeconds}s | Status: $lastStatus")
                    }
                }
            } else {
                print("\r Elapsed: ${elapsedSeconds}s | Status: $lastStatus")
            }
        }
    }
}
// snippet-end:[iotfleetwise.kotlin.decoder.active.main]

// snippet-start:[iotfleetwise.kotlin.get.manifest.main]
/**
 * Waits for the specified model manifest to become active.
 *
 * @param manifestName the name of the model manifest to wait for
 */
suspend fun waitForModelManifestActive(manifestNameVal: String) {
    var elapsedSeconds = 0
    var lastStatus: ManifestStatus = ManifestStatus.Draft

    print("⏳ Elapsed: 0s | Status: DRAFT")
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        while (true) {
            delay(1000)
            elapsedSeconds++
            if (elapsedSeconds % 5 == 0) {
                val request = GetModelManifestRequest {
                    name = manifestNameVal
                }

                val response = fleetwiseClient.getModelManifest(request)
                lastStatus = response.status ?: ManifestStatus.Draft

                when (lastStatus) {
                    ManifestStatus.Active -> {
                        print("\r Elapsed: ${elapsedSeconds}s | Status: ACTIVE ✅\n")
                        return
                    }

                    ManifestStatus.Invalid -> {
                        print("\r Elapsed: ${elapsedSeconds}s | Status: INVALID ❌\n")
                        throw RuntimeException("Model manifest became INVALID. Cannot proceed.")
                    }

                    else -> {
                        print("\r Elapsed: ${elapsedSeconds}s | Status: $lastStatus")
                    }
                }
            } else {
                print("\r Elapsed: ${elapsedSeconds}s | Status: $lastStatus")
            }
        }
    }
}
// snippet-end:[iotfleetwise.kotlin.get.manifest.main]

// snippet-start:[iotfleetwise.kotlin.update.manifest.main]
/**
 * Updates the model manifest.
 *
 * @param nameVal the name of the model manifest to update
 */
suspend fun updateModelManifest(nameVal: String) {
    val request = UpdateModelManifestRequest {
        name = nameVal
        status = ManifestStatus.Active
    }
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.updateModelManifest(request)
        println("$nameVal was successfully updated")
    }
}
// snippet-end:[iotfleetwise.kotlin.update.manifest.main]

// snippet-start:[iotfleetwise.kotlin.delete.decoder.main]
suspend fun deleteDecoderManifest(nameVal: String) {
    val request = DeleteDecoderManifestRequest {
        name = nameVal
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.deleteDecoderManifest(request)
        println("$nameVal was successfully deleted")
    }
}
// snippet-end:[iotfleetwise.kotlin.delete.decoder.main]

// snippet-start:[iotfleetwise.kotlin.create.decoder.main]
/**
 * Creates a new decoder manifest.
 *
 * @param decName             the name of the decoder manifest
 * @param modelManifestArnVal the ARN of the model manifest
 * @return the ARN of the decoder manifest
 */
suspend fun createDecoderManifest(decName: String, modelManifestArnVal: String?): String {
    val interfaceIdVal = "can0"

    val canInter = CanInterface {
        name = "canInterface0"
        protocolName = "CAN"
        protocolVersion = "1.0"
    }

    val networkInterface = NetworkInterface {
        interfaceId = interfaceIdVal
        type = NetworkInterfaceType.CanInterface
        canInterface = canInter
    }

    val carRpmSig = CanSignal {
        messageId = 100
        isBigEndian = false
        isSigned = false
        startBit = 16
        length = 16
        factor = 1.0
        offset = 0.0
    }

    val carSpeedSig = CanSignal {
        messageId = 101
        isBigEndian = false
        isSigned = false
        startBit = 0
        length = 16
        factor = 1.0
        offset = 0.0
    }

    val engineRpmDecoder = SignalDecoder {
        fullyQualifiedName = "Vehicle.Powertrain.EngineRPM"
        interfaceId = interfaceIdVal
        type = SignalDecoderType.CanSignal
        canSignal = carRpmSig
    }

    val vehicleSpeedDecoder = SignalDecoder {
        fullyQualifiedName = "Vehicle.Powertrain.VehicleSpeed"
        interfaceId = interfaceIdVal
        type = SignalDecoderType.CanSignal
        canSignal = carSpeedSig
    }

    val request = CreateDecoderManifestRequest {
        name = decName
        modelManifestArn = modelManifestArnVal
        networkInterfaces = listOf(networkInterface)
        signalDecoders = listOf(engineRpmDecoder, vehicleSpeedDecoder)
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.createDecoderManifest(request)
        return response.arn
    }
}
// snippet-end:[iotfleetwise.kotlin.create.decoder.main]

// snippet-start:[iotfleetwise.kotlin.delete.catalog.main]
/**
 * Deletes a signal catalog.
 *
 * @param name the name of the signal catalog to delete
 */
suspend fun deleteSignalCatalog(catName: String) {
    val request = DeleteSignalCatalogRequest {
        name = catName
    }
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.deleteSignalCatalog(request)
        println(" $catName was successfully deleted")
    }
}
// snippet-end:[iotfleetwise.kotlin.delete.catalog.main]

// snippet-start:[iotfleetwise.kotlin.delete.fleet.main]
/**
 * Deletes a fleet based on the provided fleet ID.
 *
 * @param fleetId the ID of the fleet to be deleted
 */
suspend fun deleteFleet(fleetIdVal: String) {
    val request = DeleteFleetRequest {
        fleetId = fleetIdVal
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.deleteFleet(request)
        println(" $fleetIdVal was successfully deleted")
    }
}
// snippet-end:[iotfleetwise.kotlin.delete.fleet.main]

// snippet-start:[iotfleetwise.kotlin.delete.model.main]
/**
 * Deletes a model manifest.
 *
 * @param nameVal the name of the model manifest to delete
 */
suspend fun deleteModelManifest(nameVal: String) {
    val request = DeleteModelManifestRequest {
        name = nameVal
    }
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        fleetwiseClient.deleteModelManifest(request)
        println(" $nameVal was successfully deleted")
    }
}
// snippet-end:[iotfleetwise.kotlin.delete.model.main]

// snippet-start:[iotfleetwise.kotlin.create.model.main]
/**
 * Creates a model manifest.
 *
 * @param name              the name of the model manifest to create
 * @param signalCatalogArn  the Amazon Resource Name (ARN) of the signal catalog
 * @param nodes             a list of nodes to include in the model manifest
 * @return a {@link CompletableFuture} that completes with the ARN of the created model manifest
 */
suspend fun createModelManifest(nameVal: String, signalCatalogArnVal: String, nodesList: List<Node>): String {
    val fqnList: List<String> = nodesList.map { node ->
        when (node) {
            is Node.Sensor -> node.asSensor().fullyQualifiedName
            is Node.Branch -> node.asBranch().fullyQualifiedName
            else -> throw RuntimeException("Unsupported node type")
        }
    }

    val request = CreateModelManifestRequest {
        name = nameVal
        signalCatalogArn = signalCatalogArnVal
        nodes = fqnList
    }
    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.createModelManifest(request)
        return response.arn
    }
}
// snippet-end:[iotfleetwise.kotlin.create.model.main]

// snippet-start:[iotfleetwise.kotlin.list.catalogs.main]
/**
 * Lists the signal catalog nodes asynchronously.
 *
 * @param signalCatalogName the name of the signal catalog
 * @return a CompletableFuture that, when completed, contains a list of nodes in the specified signal catalog
 * @throws CompletionException if an exception occurs during the asynchronous operation
 */
suspend fun listSignalCatalogNode(signalCatalogName: String): List<Node>? {
    val request = ListSignalCatalogNodesRequest {
        name = signalCatalogName
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.listSignalCatalogNodes(request)
        return response.nodes
    }
}
// snippet-end:[iotfleetwise.kotlin.list.catalogs.main]

// snippet-start:[iotfleetwise.kotlin.create.fleet.main]
/**
 * Creates a new fleet.
 *
 * @param catARN the Amazon Resource Name (ARN) of the signal catalog to associate with the fleet
 * @param fleetId the unique identifier for the fleet
 * @return the ID of the created fleet
 */
suspend fun createFleet(catARN: String, fleetIdVal: String): String {
    val fleetRequest = CreateFleetRequest {
        fleetId = fleetIdVal
        signalCatalogArn = catARN
        description = "Built using the AWS For Kotlin"
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.createFleet(fleetRequest)
        return response.id
    }
}
// snippet-end:[iotfleetwise.kotlin.create.fleet.main]

// snippet-start:[iotfleetwise.kotlin.create.catalog.main]
/**
 * Creates a signal catalog.
 *
 * @param signalCatalogName the name of the signal catalog to create the branch vehicle in
 * @return the ARN (Amazon Resource Name) of the created signal catalog
 */
suspend fun createbranchVehicle(signalCatalogName: String): String {
    delay(2000) // Wait for 2 seconds
    val branchVehicle = Branch {
        fullyQualifiedName = "Vehicle"
        description = "Root branch"
    }

    val branchPowertrain = Branch {
        fullyQualifiedName = "Vehicle.Powertrain"
        description = "Powertrain branch"
    }

    val sensorRPM = Sensor {
        fullyQualifiedName = "Vehicle.Powertrain.EngineRPM"
        description = "Engine RPM"
        dataType = NodeDataType.Double
        unit = "rpm"
    }

    val sensorKM = Sensor {
        fullyQualifiedName = "Vehicle.Powertrain.VehicleSpeed"
        description = "Vehicle Speed"
        dataType = NodeDataType.Double
        unit = "km/h"
    }

    // Wrap each specific node type (Branch and Sensor) into the sealed Node class
    // so they can be included in the CreateSignalCatalogRequest.
    val myNodes = listOf(
        Node.Branch(branchVehicle),
        Node.Branch(branchPowertrain),
        Node.Sensor(sensorRPM),
        Node.Sensor(sensorKM),
    )

    val request = CreateSignalCatalogRequest {
        name = signalCatalogName
        nodes = myNodes
    }

    IotFleetWiseClient { region = "us-east-1" }.use { fleetwiseClient ->
        val response = fleetwiseClient.createSignalCatalog(request)
        return response.arn
    }
}
// snippet-end:[iotfleetwise.kotlin.create.catalog.main]

private fun waitForInputToContinue(scanner: Scanner) {
    while (true) {
        println("")
        println("Enter 'c' followed by <ENTER> to continue:")
        val input = scanner.nextLine()

        if (input.trim { it <= ' ' }.equals("c", ignoreCase = true)) {
            println("Continuing with the program...")
            println("")
            break
        } else {
            println("Invalid input. Please try again.")
        }
    }
}
// snippet-end:[iotfleetwise.kotlin.scenario.main]
