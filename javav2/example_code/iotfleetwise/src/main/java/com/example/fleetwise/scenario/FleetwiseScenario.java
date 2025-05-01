// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iot.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.iotfleetwise.model.Node;
import software.amazon.awssdk.services.iotfleetwise.model.ResourceNotFoundException;
import software.amazon.awssdk.services.iotfleetwise.model.ValidationException;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletionException;

// snippet-start:[iotfleetwise.java2.scenario.main]
public class FleetwiseScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    static FleetwiseActions actions = new FleetwiseActions();
    private static final Logger logger = LoggerFactory.getLogger(FleetwiseScenario.class);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        final String usage =
                """
                Usage:
                    <signalCatalogName> <manifestName> <fleetId> <vecName> <decName>
                
                Where:
                    signalCatalogName     - The name of the Signal Catalog to create (eg, catalog30).
                    manifestName          - The name of the Vehicle Model (Model Manifest) to create (eg, manifest30).
                    fleetId               - The ID of the Fleet to create (eg, fleet30).
                    vecName               - The name of the Vehicle to create (eg, vehicle30).
                    decName               - The name of the Decoder Manifest to create (eg, decManifest30).
                """;

        if (args.length != 5) {
            logger.info(usage);
            return;
        }

        String signalCatalogName = args[0];
        String manifestName = args[1];
        String fleetId = args[2];
        String vecName = args[3];
        String decName = args[4];

        logger.info(
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
                """);

        waitForInputToContinue(scanner);
        logger.info(DASHES);
        try {
            runScenario(signalCatalogName, manifestName, fleetId, vecName, decName);
        } catch (RuntimeException e) {
            logger.info(e.getMessage());
        }
    }

    private static void runScenario(String signalCatalogName,
                                    String manifestName,
                                    String fleetId,
                                    String vecName,
                                    String decName) {
        logger.info(DASHES);
        logger.info("1. Creates a collection of standardized signals that can be reused to create vehicle models");
        String signalCatalogArn;
        try {
            signalCatalogArn = actions.createSignalCatalogAsync(signalCatalogName).join();
            logger.info("The collection ARN is " + signalCatalogArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof ValidationException) {
                logger.error("The request failed due to a validation issue: {}", cause.getMessage());
            } else {
                logger.error("An unexpected error occurred.", cause);
            }
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("2. Create a fleet that represents a group of vehicles");
        logger.info(
                """
                Creating an IoT FleetWise fleet allows you to efficiently collect, 
                organize, and transfer vehicle data to the cloud, enabling real-time 
                insights into vehicle performance and health. 
                
                It helps reduce data costs by allowing you to filter and prioritize 
                only the most relevant vehicle signals, supporting advanced analytics 
                and predictive maintenance use cases.
                """);

        waitForInputToContinue(scanner);
        String fleetid;
        try {
            fleetid = actions.createFleetAsync(signalCatalogArn, fleetId).join();
            logger.info("The fleet Id is " + fleetid);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof ResourceNotFoundException) {
                logger.error("The resource was not found: {}", cause.getMessage());
            } else {
                logger.error("An unexpected error occurred.", cause);
            }
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("3. Create a model manifest");
        logger.info(
                """
                An AWS IoT FleetWise manifest defines the structure and 
                relationships of vehicle data. The model manifest specifies 
                which signals to collect and how they relate to vehicle systems, 
                while the decoder manifest defines how to decode raw vehicle data 
                into meaningful signals. 
                """);
        waitForInputToContinue(scanner);
        String manifestArn;
        try {
            List<Node> nodes = actions.listSignalCatalogNodeAsync(signalCatalogName).join();
            manifestArn = actions.createModelManifestAsync(manifestName, signalCatalogArn, nodes).join();
            logger.info("The manifest ARN is {}", manifestArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.error("An unexpected error occurred.", cause);
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Create a decoder manifest");
        logger.info(
                """
                A decoder manifest in AWS IoT FleetWise defines how raw vehicle 
                data (such as CAN signals) should be interpreted and decoded 
                into meaningful signals. It acts as a translation layer 
                that maps vehicle-specific protocols to standardized data formats
                using decoding rules. This is crucial for extracting usable
                data from different vehicle models, even when their data 
                formats vary.
                
                """);
        waitForInputToContinue(scanner);
        String decArn;
        try {
            decArn = actions.createDecoderManifestAsync(decName, manifestArn).join();
            logger.info("The decoder manifest ARN is {}", decArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.error("An unexpected error occurred.", cause);
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("5. Check the status of the model manifest");
        logger.info(
                """
                The model manifest must be in an ACTIVE state before it can be used 
                to create or update a vehicle.
                """);
        waitForInputToContinue(scanner);
        try {
            actions.updateModelManifestAsync(manifestName);
            actions.waitForModelManifestActiveAsync(manifestName).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.error("An unexpected error occurred while waiting for the model manifest status.", cause);
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("6. Check the status of the decoder");
        logger.info(
                """
                The decoder manifest must be in an ACTIVE state before it can be used 
                to create or update a vehicle.
                """);
        waitForInputToContinue(scanner);
        try {
            actions.updateDecoderManifestAsync(decName);
            actions.waitForDecoderManifestActiveAsync(decName).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.error("An unexpected error occurred while waiting for the decoder manifest status.", cause);
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Create an IoT Thing");
        logger.info(
                """
                AWS IoT FleetWise expects an existing AWS IoT Thing with the same 
                name as the vehicle name you are passing to createVehicle method. 
                Before calling createVehicle(), you must create an AWS IoT Thing 
                with the same name using the AWS IoT Core service.
                """);
        waitForInputToContinue(scanner);
        try {
            actions.createThingIfNotExistsAsync(vecName).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof ResourceAlreadyExistsException) {
                logger.error("The resource exists: {}", cause.getMessage());
            } else {
                logger.error("An unexpected error occurred.", cause);
                return;
            }
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. Create a vehicle");
        logger.info(
                """
                Creating a vehicle in AWS IoT FleetWise allows you to digitally 
                represent and manage a physical vehicle within the AWS ecosystem. 
                This enables efficient ingestion, transformation, and transmission 
                of vehicle telemetry data to the cloud for analysis.
                """);
        waitForInputToContinue(scanner);
        try {
            actions.createVehicleAsync(vecName, manifestArn, decArn).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();

            if (cause instanceof ResourceNotFoundException) {
                logger.error("The required resource was not found: {}", cause.getMessage());
            } else {
                logger.error("An unexpected error occurred while creating vehicle.", cause);
            }
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("9. Display vehicle details");
        waitForInputToContinue(scanner);
        try {
            actions.getVehicleDetailsAsync(vecName).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            if (cause instanceof ResourceNotFoundException) {
                logger.error("The resource was not found: {}", cause.getMessage());
            } else {
                logger.error("An unexpected error occurred.", cause);
            }
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("10. Delete the AWS IoT Fleetwise Assets");
        logger.info("Would you like to delete the IoT Fleetwise Assets? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            try {
                actions.deleteVehicleAsync(vecName).join();
                actions.deleteDecoderManifestAsync(decName).join();
                actions.deleteModelManifestAsync(manifestName).join();
                actions.deleteFleetAsync(fleetid).join();
                actions.deleteSignalCatalogAsync(signalCatalogName).join();
            } catch (CompletionException ce) {
                Throwable cause = ce.getCause();
                if (cause instanceof ResourceNotFoundException) {
                    // Handle the case where the resource is not found.
                    logger.error("The resource was not found: {}", cause.getMessage());
                } else if (cause instanceof RuntimeException) {
                    // Handle other runtime exceptions.
                    logger.error("An unexpected error occurred: {}", cause.getMessage());
                } else {
                    // Catch any other unexpected exceptions.
                    logger.error("An unknown error occurred.", cause);
                }
                return;
            }

            logger.info(DASHES);
            logger.info(
                    """
                    Thank you for checking out the AWS IoT Fleetwise Service Use demo. We hope you
                    learned something new, or got some inspiration for your own apps today.
                    For more AWS code examples, have a look at:
                    https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html
                    """);
            logger.info(DASHES);
        } else {
            logger.info("The AWS resources will not be deleted.");
        }
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            logger.info("");
            logger.info("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                logger.info("Continuing with the program...");
                logger.info("");
                break;
            } else {
                logger.info("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[iotfleetwise.java2.scenario.main]