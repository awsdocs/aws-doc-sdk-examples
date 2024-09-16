// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iotsitewise.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.iotsitewise.model.BatchPutAssetPropertyValueResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetModelResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateGatewayResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeleteAssetModelResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeleteAssetResponse;
import software.amazon.awssdk.services.iotsitewise.model.DeletePortalResponse;
import software.amazon.awssdk.services.iotsitewise.model.DescribeGatewayResponse;
import software.amazon.awssdk.services.iotsitewise.model.IoTSiteWiseException;
import software.amazon.awssdk.services.iotsitewise.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.ssm.model.ResourceNotFoundException;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class SitewiseScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final Logger logger = LoggerFactory.getLogger(SitewiseScenario.class);
    static Scanner scanner = new Scanner(System.in);

    static SitewiseActions sitewiseActions = new SitewiseActions();

    public static void main(String[] args) throws Throwable {
        Scanner scanner = new Scanner(System.in);
        String assetModelName = "MyAssetModel2";
        String assetName = "MyAsset";
        String iamRole = "arn:aws:iam::814548047983:role/service-role/AWSIoTSiteWiseMonitorServiceRole_8jLqg43nJ";
        String portalName = "MyPortal";
        String contactEmail = "scmacdon@amazon.com";
        String gatewayName = "myGateway11";
        String myThing = "myThing78";

        logger.info("""
            AWS IoT SiteWise is a fully managed industrial software-as-a-service (SaaS) that 
            makes it easy to collect, store, organize, and monitor data from industrial equipment and processes. 
            It is designed to help industrial and manufacturing organizations collect data from their equipment and 
            processes, and use that data to make informed decisions about their operations.
                                            
            One of the key features of AWS IoT SiteWise is its ability to connect to a wide range of industrial 
            equipment and systems, including programmable logic controllers (PLCs), sensors, and other 
            industrial devices. It can collect data from these devices and organize it into a unified data model, 
            making it easier to analyze and gain insights from the data. AWS IoT SiteWise also provides tools for 
            visualizing the data, setting up alarms and alerts, and generating reports.
                                
            Another key feature of AWS IoT SiteWise is its ability to scale to handle large volumes of data. 
            It can collect and store data from thousands of devices and process millions of data points per second, 
            making it suitable for large-scale industrial operations. Additionally, AWS IoT SiteWise is designed 
            to be secure and compliant, with features like role-based access controls, data encryption, 
            and integration with other AWS services for additional security and compliance features.
                        
            Let's get started...
            """);

        waitForInputToContinue(scanner);
        logger.info(DASHES);

        try {
            runScenario(assetModelName, assetName, iamRole, portalName, contactEmail, gatewayName, myThing);
        } catch (RuntimeException e) {
           logger.info(e.getMessage());
        }
    }

    public static void runScenario(String assetModelName, String assetName,  String iamRole, String portalName, String contactEmail, String gatewayName, String myThing) throws Throwable {
        logger.info(DASHES);
        logger.info("1. Create an AWS SiteWise Asset Model");
        logger.info("""
             An AWS IoT SiteWise Asset Model is a way to represent the physical assets, such as equipment,
             processes, and systems, that exist in an industrial environment. This model provides a structured and
             hierarchical representation of these assets, allowing users to define the relationships and properties
             of each asset.
            """);
        waitForInputToContinue(scanner);
        String assetModelId = "";
        try {
            CompletableFuture<CreateAssetModelResponse> future = sitewiseActions.createAssetModelAsync(assetModelName);
            CreateAssetModelResponse response = future.join();
            assetModelId = response.assetModelId();
            logger.info("Asset Model successfully created. Asset Model ID: {}. ", assetModelId);

        } catch (RuntimeException rt) {
            Throwable cause = rt;

            // Unwrap nested exceptions to find the root cause.
            while (cause.getCause() != null && !(cause instanceof ResourceAlreadyExistsException)) {
                cause = cause.getCause();
            }
            if (cause instanceof ResourceAlreadyExistsException) {
                assetModelId = sitewiseActions.getAssetModelIdAsync(assetModelName).join();
                logger.info("The Asset Model {} already exists. The id of the existing model is {}. Moving on...", assetModelName, assetModelId);
            } else {
                // Log unexpected errors
                logger.info("An unexpected error occurred: " + rt.getMessage(), rt);
                throw cause;
            }
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("2. Create an AWS IoT SiteWise Asset");
        logger.info("""
             The IoT SiteWise model defines the structure and metadata for your physical assets. Now we
             can use the asset model to create the asset.
                    
            """);
        waitForInputToContinue(scanner);
        String assetId = "";
        try {
            CompletableFuture<CreateAssetResponse> future = sitewiseActions.createAssetAsync(assetName, assetModelId);
            CreateAssetResponse response = future.join();
            assetId = response.assetId();
            logger.info("Asset created with ID: " + assetId);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            // Unwrap nested exceptions to find the root cause.
            while (cause != null) {
                if (cause instanceof software.amazon.awssdk.services.iotsitewise.model.ResourceNotFoundException) {
                    logger.info("The AWS resource was not found: {}", cause.getMessage());
                    break;
                }
                cause = cause.getCause();
            }

            if (cause == null) {
                logger.info("An unexpected error occurred: {}", rt.getMessage());
            }

            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("3. Retrieve the property ID values");
        logger.info("""
             To send data to an asset, we need to get the property ID values for the
             Temperature and Humidity properties. 
            """);
        waitForInputToContinue(scanner);

        String humPropId = "";
        try {
            CompletableFuture<String> future = sitewiseActions.findPropertyIdByNameAsync("Humidity");
            humPropId = future.join();
            logger.info("The Humidity property Id is {}", humPropId);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof IoTSiteWiseException siteWiseEx) {
                logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }

        String tempPropId;
        try {
            CompletableFuture<String> future = sitewiseActions.findPropertyIdByNameAsync("Temperature");
            tempPropId = future.join();
            logger.info("The Temperature property Id is {}", tempPropId);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof IoTSiteWiseException siteWiseEx) {
                logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }

        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Send data to an AWS IoT SiteWise Asset");
        logger.info("""
            By sending data to an IoT SiteWise Asset, you can aggregate data from 
            multiple sources, normalize the data into a standard format, and store it in a 
            centralized location. This makes it easier to analyze and gain insights from the data.
                        
            This example demonstrate how to generate sample data and ingest it into the AWS IoT SiteWise asset.
                        
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<BatchPutAssetPropertyValueResponse> future = sitewiseActions.sendDataToSiteWiseAsync(assetId, tempPropId, humPropId);
            future.join();
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            while (cause != null) {
                if (cause instanceof software.amazon.awssdk.services.iotsitewise.model.ResourceNotFoundException) {
                    logger.info("The AWS resource was not found: {}", cause.getMessage());
                    break;
                }
                cause = cause.getCause();
            }

            if (cause == null) {
                logger.info("An unexpected error occurred: {}", rt.getMessage());
            }

            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("5. Retrieve the value of the IoT SiteWise Asset property");
        logger.info("""
            IoT SiteWise is an AWS service that allows you to collect, process, and analyze industrial data 
            from connected equipment and sensors. One of the key benefits of reading an IoT SiteWise property 
            is the ability to gain valuable insights from your industrial data.
                       
            """);
        waitForInputToContinue(scanner);
        try {
        sitewiseActions.getAssetPropValueAsync("Temperature property", tempPropId, assetId);
        waitForInputToContinue(scanner);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            while (cause != null) {
                if (cause instanceof software.amazon.awssdk.services.iotsitewise.model.ResourceNotFoundException) {
                    logger.info("The AWS resource was not found: {}", cause.getMessage());
                    break;
                }
                cause = cause.getCause();
            }

            if (cause == null) {
                logger.info("An unexpected error occurred: {}", rt.getMessage());
            }

            throw cause;
        }
        sitewiseActions.getAssetPropValueAsync("Humidity property", humPropId, assetId);
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("6. Create an IoT SiteWise Portal");
        logger.info("""
             An IoT SiteWise Portal allows you to aggregate data from multiple industrial sources, 
             such as sensors, equipment, and control systems, into a centralized platform.
            """);
        waitForInputToContinue(scanner);
        String portalId = "";
        try {
            CompletableFuture<String> future = sitewiseActions.createPortalAsync(portalName, iamRole, contactEmail);
            portalId = future.join();
            logger.info("Portal created successfully. Portal ID {}", portalId);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof IoTSiteWiseException siteWiseEx) {
                logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        logger.info("The portal Id is " + portalId);
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Describe the Portal");
        logger.info("""
             In this step, we will describe the step and provide the portal URL.
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = sitewiseActions.describePortalAsync(portalId);
            String portalUrl = future.join();
            logger.info("Portal URL: {}", portalUrl);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof IoTSiteWiseException siteWiseEx) {
                logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. Create an IoTSitewise Gateway");
        logger.info(
            """
                IoTSitewise Gateway serves as the bridge between industrial equipment, sensors, and the 
                cloud-based IoTSitewise service. It is responsible for securely collecting, processing, and 
                transmitting data from various industrial assets to the IoTSitewise platform, 
                enabling real-time monitoring, analysis, and optimization of industrial operations.
                     
                """);
        waitForInputToContinue(scanner);
        String gatewayId = "";
        try {
            CompletableFuture<CreateGatewayResponse> future = sitewiseActions.createGatewayAsync(gatewayName, myThing);
            CreateGatewayResponse response = future.join();
            gatewayId = response.gatewayId();
            logger.info("Gateway creation completed successfully. id is {}",gatewayId );
        } catch (RuntimeException e) {
            System.err.println("Failed to create gateway: " + e.getMessage());
        }
        logger.info(DASHES);


        logger.info(DASHES);
        logger.info("9. Describe the IoTSitewise Gateway");
         waitForInputToContinue(scanner);
        try {
            CompletableFuture<DescribeGatewayResponse> future = sitewiseActions.describeGatewayAsync(gatewayId) ;
            future.join();

        } catch (RuntimeException e) {
            System.err.println("Failed to create gateway: " + e.getMessage());
        }
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("10. Delete the AWS IoT SiteWise Assets");
        logger.info(
            """
            Before you can delete the Asset Model, you must delete the assets.  
     
            """);
        logger.info("Would you like to delete the IoT Sitewise Assets? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            logger.info("You selected to delete the Sitewise assets.");
            waitForInputToContinue(scanner);
            try {
                CompletableFuture<DeletePortalResponse> future = sitewiseActions.deletePortalAsync(portalId);
                future.join();
                logger.info("Portal {} was deleted successfully.", portalId);

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof IoTSiteWiseException siteWiseEx) {
                    logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }

            // Delete the Gateway.
            try {
                CompletableFuture<DeleteAssetResponse> future = sitewiseActions.deleteGatewayAsync(gatewayId);
                future.join();

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof IoTSiteWiseException siteWiseEx) {
                    logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }

            try {
                CompletableFuture<DeleteAssetResponse> future = sitewiseActions.deleteAssetAsync(assetId);
                future.join();
                logger.info("Asset deleted successfully.");

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof IoTSiteWiseException siteWiseEx) {
                    logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }
            logger.info("Lets wait 1 min for the asset to be deleted");
            countdown(1);
            waitForInputToContinue(scanner);
            logger.info("Delete the AWS IoT SiteWise Asset Model");
            try {
                CompletableFuture<DeleteAssetModelResponse> future = sitewiseActions.deleteAssetModelAsync(assetModelId);
                future.join();
                logger.info("Asset model deleted successfully.");

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof IoTSiteWiseException siteWiseEx) {
                    logger.info("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }
            waitForInputToContinue(scanner);

        } else {
            logger.info("The resources will not be deleted.");
        }
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("This concludes the AWS SiteWise Scenario");
        logger.info(DASHES);
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
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    public static void countdown(int minutes) throws InterruptedException {
        int seconds = 0;
        for (int i = minutes * 60 + seconds; i >= 0; i--) {
            int displayMinutes = i / 60;
            int displaySeconds = i % 60;
            System.out.print(String.format("\r%02d:%02d", displayMinutes, displaySeconds));
            Thread.sleep(1000); // Wait for 1 second
        }

        System.out.println("Countdown complete!");
    }
}
