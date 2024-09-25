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
import software.amazon.awssdk.services.iotsitewise.model.ResourceNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

// snippet-start:[iotsitewise.java2.scenario.main]
public class SitewiseScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final Logger logger = LoggerFactory.getLogger(SitewiseScenario.class);
    static Scanner scanner = new Scanner(System.in);

    private static final String ROLES_STACK = "RoleSitewise";

    static SitewiseActions sitewiseActions = new SitewiseActions();

    public static void main(String[] args) throws Throwable {
        final String usage = """
            Usage:
               <assetModelName> <assetName> <portalName> <contactEmail> <gatewayName> <myThing>

            Where:
                assetModelName - The name of the asset model used in the IoT SiteWise program.
                assetName -  The name of the asset created in the IoT SiteWise program. 
                portalName - The name of the IoT SiteWise portal where the asset and other resources are created.
                contactEmail - The email address of the contact person associated with the IoT SiteWise program.
                gatewayName - The name of the IoT SiteWise gateway used to collect and send data to the IoT SiteWise service.
                myThing - The name of the IoT thing or device that is connected to the IoT SiteWise gateway.
            """;

        //if (args.length != 6) {
        //    logger.info(usage);
        //    return;
       // }

        Scanner scanner = new Scanner(System.in);
        String assetModelName = "MyAssetModel";
        String assetName = "MyAsset";
        String portalName = "MyPortal";
        String contactEmail = "scmacdon@amazon.com";
        String gatewayName = "myGateway11";
        String myThing = "myThing78";

        logger.info("""
            AWS IoT SiteWise is a fully managed software-as-a-service (SaaS) that 
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
            runScenario(assetModelName, assetName, portalName, contactEmail, gatewayName, myThing);
        } catch (RuntimeException e) {
           logger.info(e.getMessage());
        }
    }

    public static void runScenario(String assetModelName, String assetName,  String portalName, String contactEmail, String gatewayName, String myThing) throws Throwable {
        logger.info("Use AWS CloudFormation to create an IAM role that is required for this scenario.");
        CloudFormationHelper.deployCloudFormationStack(ROLES_STACK);
        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(ROLES_STACK);
        String iamRole = stackOutputs.get("SitewiseRoleArn");
        logger.info("The ARN of the IAM role is {}",iamRole);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("1. Create an AWS SiteWise Asset Model");
        logger.info("""
             An AWS IoT SiteWise Asset Model is a way to represent the physical assets, such as equipment,
             processes, and systems, that exist in an industrial environment. This model provides a structured and
             hierarchical representation of these assets, allowing users to define the relationships and properties
             of each asset.
             
             This scenario creates two asset model properties: temperature and humidity.
            """);
        waitForInputToContinue(scanner);
        String assetModelId;
        try {
            CompletableFuture<CreateAssetModelResponse> future = sitewiseActions.createAssetModelAsync(assetModelName);
            CreateAssetModelResponse response = future.join();
            assetModelId = response.assetModelId();
            logger.info("Asset Model successfully created. Asset Model ID: {}. ", assetModelId);

        } catch (RuntimeException rt) {
            Throwable cause = rt;
            while (cause.getCause() != null && !(cause instanceof ResourceAlreadyExistsException)) {
                cause = cause.getCause();
            }
            if (cause instanceof ResourceAlreadyExistsException) {
                assetModelId = sitewiseActions.getAssetModelIdAsync(assetModelName).join();
                logger.info("The Asset Model {} already exists. The id of the existing model is {}. Moving on...", assetModelName, assetModelId);
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage(), rt);
                throw cause;
            }
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("2. Create an AWS IoT SiteWise Asset");
        logger.info("""
             The IoT SiteWise model that we just created defines the structure and metadata for your physical assets. Now we create
             an asset from the asset model.
                    
            """);
        waitForInputToContinue(scanner);
        String assetId;
        try {
            CompletableFuture<CreateAssetResponse> future = sitewiseActions.createAssetAsync(assetName, assetModelId);
            CreateAssetResponse response = future.join();
            assetId = response.assetId();
            logger.info("Asset created with ID: {}", assetId);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            while (cause != null) {
                if (cause instanceof software.amazon.awssdk.services.iotsitewise.model.ResourceNotFoundException) {
                    logger.info("The AWS resource was not found: {}", cause.getMessage());  // QUESTION: What resource? the asset model? Clarify if that is the case?
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
             To send data to an asset, we need to get the property ID values. In this scenario, we access the
             temperature and humidity property ID values. 
            """);
        waitForInputToContinue(scanner);

        Map<String, String> propertyIds = null;
        try {
            propertyIds = sitewiseActions.getPropertyIds(assetModelId).join();
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof IoTSiteWiseException) {
                logger.error("IoTSiteWiseException occurred: {}", cause.getMessage(), e);
            } else {
                logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            }
            return;
        }
        String humPropId =  propertyIds.get("Humidity");
        logger.info("The Humidity property Id is {}", humPropId);
        String tempPropId = propertyIds.get("Temperature");
        logger.info("The Temperature property Id is {}", tempPropId);

        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Send data to an AWS IoT SiteWise Asset");
        logger.info("""
            By sending data to an IoT SiteWise Asset, you can aggregate data from 
            multiple sources, normalize the data into a standard format, and store it in a 
            centralized location. This makes it easier to analyze and gain insights from the data.
                        
            In this example, we generate sample temperature and humidity data and send it to the AWS IoT SiteWise asset.
                        
            """);
        waitForInputToContinue(scanner);
        try {
            sitewiseActions.sendDataToSiteWiseAsync(assetId, tempPropId, humPropId).join();
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null && cause instanceof ResourceNotFoundException) {
                logger.error("The AWS resource was not found: {}", cause.getMessage(), rt);
            } else {
                logger.error("An unexpected error occurred: {}", rt.getMessage(), rt);
            }
            return;
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
        sitewiseActions.getAssetPropValueAsync("Temperature property", tempPropId, assetId); // FIXME <------------------
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
        sitewiseActions.getAssetPropValueAsync("Humidity property", humPropId, assetId); // FIXME <------------------
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
            portalId = sitewiseActions.createPortalAsync(portalName, iamRole, contactEmail).join();
            logger.info("Portal created successfully. Portal ID {}", portalId);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null && cause instanceof IoTSiteWiseException siteWiseEx) {
                logger.error("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
            } else {
                logger.error("An unexpected error occurred: {}", rt.getMessage());
            }
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Describe the Portal");
        logger.info("""
             In this step, we will describe the step and provide the portal URL.
            """);
        waitForInputToContinue(scanner);
        try {
            String portalUrl = sitewiseActions.describePortalAsync(portalId).join();
            logger.info("Portal URL: {}", portalUrl);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null && cause instanceof ResourceNotFoundException notFoundException) {
                logger.error("A ResourceNotFoundException occurred: Error message: {}, Error code {}", notFoundException.getMessage(), notFoundException.awsErrorDetails().errorCode());
            } else {
                logger.error("An unexpected error occurred: {}", rt.getMessage());
            }
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. Create an IoT SiteWise Gateway");
        logger.info(
            """
                IoT SiteWise Gateway serves as the bridge between industrial equipment, sensors, and the 
                cloud-based IoT SiteWise service. It is responsible for securely collecting, processing, and 
                transmitting data from various industrial assets to the IoT SiteWise platform, 
                enabling real-time monitoring, analysis, and optimization of industrial operations.
                     
                """);
        waitForInputToContinue(scanner);
        String gatewayId = "";
        try {
            gatewayId = sitewiseActions.createGatewayAsync(gatewayName, myThing).join();
            logger.info("Gateway creation completed successfully. id is {}", gatewayId );
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null && cause instanceof IoTSiteWiseException siteWiseEx) {
                logger.error("IoT SiteWise error occurred: Error message: {}, Error code {}", siteWiseEx.getMessage(), siteWiseEx.awsErrorDetails().errorCode());
            } else {
                logger.error("An unexpected error occurred: {}", rt.getMessage());
            }
            return;
        }
        logger.info(DASHES);
        logger.info(DASHES);

        logger.info("9. Describe the IoT SiteWise Gateway");
         waitForInputToContinue(scanner);
        try {
            sitewiseActions.describeGatewayAsync(gatewayId).join();
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null && cause instanceof ResourceNotFoundException notFoundException) {
                logger.error("A ResourceNotFoundException occurred: Error message: {}, Error code {}", notFoundException.getMessage(), notFoundException.awsErrorDetails().errorCode());
            } else {
                logger.error("An unexpected error occurred: {}", rt.getMessage());
            }
            return;
        }
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("10. Delete the AWS IoT SiteWise Assets");
        logger.info(
            """
            Before you can delete the Asset Model, you must delete the assets.  
     
            """);
        logger.info("Would you like to delete the IoT SiteWise Assets? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            logger.info("You selected to delete the SiteWise assets.");
            waitForInputToContinue(scanner);
            try {
                sitewiseActions.deletePortalAsync(portalId).join();
                logger.info("Portal {} was deleted successfully.", portalId);

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause != null && cause instanceof ResourceNotFoundException notFoundException) {
                    logger.error("A ResourceNotFoundException occurred: Error message: {}, Error code {}", notFoundException.getMessage(), notFoundException.awsErrorDetails().errorCode());
                } else {
                    logger.error("An unexpected error occurred: {}", rt.getMessage());
                }
            }

            // Delete the Gateway.
            try {
                sitewiseActions.deleteGatewayAsync(gatewayId).join();
            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause != null && cause instanceof ResourceNotFoundException notFoundException) {
                    logger.error("A ResourceNotFoundException occurred: Error message: {}, Error code {}", notFoundException.getMessage(), notFoundException.awsErrorDetails().errorCode());
                } else {
                    logger.error("An unexpected error occurred: {}", rt.getMessage());
                }
            }

            try {
                sitewiseActions.deleteAssetAsync(assetId).join();
            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause != null && cause instanceof ResourceNotFoundException notFoundException) {
                    logger.error("A ResourceNotFoundException occurred: Error message: {}, Error code {}", notFoundException.getMessage(), notFoundException.awsErrorDetails().errorCode());
                } else {
                    logger.error("An unexpected error occurred: {}", rt.getMessage());
                }
            }
            logger.info("Let's wait 1 minute for the asset to be deleted.");
            countdown(1);
            waitForInputToContinue(scanner);
            logger.info("Delete the AWS IoT SiteWise Asset Model");
            try {
                sitewiseActions.deleteAssetModelAsync(assetModelId).join();
                logger.info("Asset model deleted successfully.");

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause != null && cause instanceof ResourceNotFoundException notFoundException) {
                    logger.error("A ResourceNotFoundException occurred: Error message: {}, Error code {}", notFoundException.getMessage(), notFoundException.awsErrorDetails().errorCode());
                } else {
                    logger.error("An unexpected error occurred: {}", rt.getMessage());
                }
            }
            waitForInputToContinue(scanner);

        } else {
            logger.info("The resources will not be deleted.");
        }
        logger.info(DASHES);

        logger.info(DASHES);
        CloudFormationHelper.destroyCloudFormationStack(ROLES_STACK);
        logger.info("This concludes the AWS IoT SiteWise Scenario");
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
// snippet-end:[iotsitewise.java2.scenario.main]