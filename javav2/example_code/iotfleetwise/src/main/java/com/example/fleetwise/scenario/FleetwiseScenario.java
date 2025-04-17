// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iotfleetwise.model.Node;
import java.util.List;
import java.util.Scanner;

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
                    signalCatalogName     - The name of the Signal Catalog to create or use.
                    manifestName          - The name of the Vehicle Model (Model Manifest) to create or use.
                    fleetId               - The ID of the Fleet to create or use.
                    vecName               - The name of the Vehicle to create or register.
                    decName               - The name of the Decoder Manifest to create or use.
                """;

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String signalCatalogName = args[0];
        String manifestName = args[1];
        String fleetId = args[2];
        String vecName = args[3];
        String decName = args[4];

        System.out.println("""
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
        System.out.println(DASHES);
        runScenario(signalCatalogName, manifestName, fleetId, vecName, decName);

    }

    private static void runScenario(String signalCatalogName,
                                    String manifestName,
                                    String fleetId,
                                    String vecName,
                                    String decName) {
        System.out.println(DASHES);
        System.out.println("1. Creates a collection of standardized signals that can be reused to create vehicle models");
        String signalCatalogArn = actions.createSignalCatalogAsync(signalCatalogName).join();
        System.out.println("The collection ARN is " + signalCatalogArn);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create a fleet that represents a group of vehicles");
        System.out.println("""
                Creating an IoT FleetWise fleet allows you to efficiently collect, 
                organize, and transfer vehicle data to the cloud, enabling real-time 
                insights into vehicle performance and health. 
                
                It helps reduce data costs by allowing you to filter and prioritize 
                only the most relevant vehicle signals, supporting advanced analytics 
                and predictive maintenance use cases.
               """);

        waitForInputToContinue(scanner);
        String fleetid = actions.createFleetAsync(signalCatalogArn, fleetId).join();
        System.out.println("The fleet Id is " + fleetid);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create a model manifest");
        System.out.println("""
          An AWS IoT FleetWise manifest defines the structure and 
          relationships of vehicle data. The model manifest specifies 
          which signals to collect and how they relate to vehicle systems, 
          while the decoder manifest defines how to decode raw vehicle data 
          into meaningful signals. 
          """);
        waitForInputToContinue(scanner);
        List<Node> nodes = actions.listSignalCatalogNodeAsync(signalCatalogName).join();
        String manifestArn = actions.createModelManifestAsync(manifestName,signalCatalogArn,nodes).join();
        System.out.println("The manifest ARN is "+ manifestArn);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Create a decoder manifest");
        System.out.println("""
                A decoder manifest in AWS IoT FleetWise defines how raw vehicle 
                data (such as CAN signals) should be interpreted and decoded 
                into meaningful signals. It acts as a translation layer 
                that maps vehicle-specific protocols to standardized data formats
                using decoding rules. This is crucial for extracting usable
                data from different vehicle models, even when their data 
                 formats vary.
                
                """);
        waitForInputToContinue(scanner);
        String decArn = actions.createDecoderManifestAsync(decName ,manifestArn).join();
        System.out.println("The decoder manifest ARN is "+ decArn);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("5. Check the status of the model");
        System.out.println("""
                 The model manifest must be in an ACTIVE state before it can be used 
                  to create or update a vehicle.
                """);
        waitForInputToContinue(scanner);
        actions.updateModelManifestAsync(manifestName);
        actions.waitForModelManifestActiveAsync(manifestName).join();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println("6. Check the status of the decoder");
        System.out.println("""
                 The decoder manifest must be in an ACTIVE state before it can be used 
                  to create or update a vehicle.
                """);
        waitForInputToContinue(scanner);
        actions.updateDecoderManifestAsync(decName);
        actions.waitForDecoderManifestActiveAsync(decName).join() ;
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Create an IoT Thing");
        System.out.println("""
        AWS IoT FleetWise expects an existing AWS IoT Thing with the same 
        name as the vehicle name you are passing to createVehicle method. 
        Before calling createVehicle(), you must create an AWS IoT Thing 
        with the same name using the AWS IoT Core service.
          """);
        waitForInputToContinue(scanner);
        actions.createThingIfNotExistsAsync(vecName).join();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Create a vehicle");
        System.out.println("""
          Creating a vehicle in AWS IoT FleetWise allows you to digitally 
          represent and manage a physical vehicle within the AWS ecosystem. 
          This enables efficient ingestion, transformation, and transmission 
          of vehicle telemetry data to the cloud for analysis.
          """);
        waitForInputToContinue(scanner);
        actions.createVehicleAsync(vecName, manifestArn,decArn).join();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Display vehicle details");
        waitForInputToContinue(scanner);
        actions.getVehicleDetailsAsync(vecName).join();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. Delete the AWS IoT Fleetwise Assets");
        System.out.println("Would you like to delete the IoT Fleetwise Assets? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            actions.deleteVehicleAsync(vecName).join();
            actions.deleteDecoderManifestAsync(decName).join();
            actions.deleteModelManifestAsync(manifestName).join();
            actions.deleteFleetAsync(fleetid).join();
            actions.deleteSignalCatalogAsync(signalCatalogName).join();
        }

        System.out.println(DASHES);
        System.out.println("You have successfully completed the AWS IoT Fleetwise scenario.");
        System.out.println(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-start:[iotfleetwise.java2.scenario.main]