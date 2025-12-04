// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.controltower;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.*;

import java.util.List;
import java.util.Scanner;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java code example performs the following tasks:
 *
 * 1. Check Control Tower setup and list landing zones
 * 2. List available baselines for governance
 * 3. List currently enabled baselines
 * 4. Enable a baseline for a target organizational unit
 * 5. List enabled controls for the target
 * 6. Enable a control for a target organizational unit
 * 7. Monitor operation status
 * 8. Clean up by disabling controls and baselines
 */

// snippet-start:[controltower.java2.controltower_scenario.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.ControlTowerException;
import software.amazon.awssdk.services.controltower.model.LandingZoneSummary;
import software.amazon.awssdk.services.controltower.model.BaselineSummary;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class ControlTowerScenario {
    private static final Logger logger = LoggerFactory.getLogger(ControlTowerScenario.class);
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Suppress AWS SDK internal debug logs
        System.setProperty("software.amazon.awssdk.logging.level", "WARN");

        logger.info(DASHES);
        logger.info("Welcome to the AWS Control Tower basics scenario!");
        logger.info(DASHES);
        logger.info("""
                AWS Control Tower is a service that enables you to set up and govern 
                a secure, multi-account AWS environment based on best practices. 
                Control Tower orchestrates several other AWS services to build a landing zone.
                
                This scenario will guide you through the basic operations of 
                Control Tower, including managing baselines and controls for 
                governance and compliance.
                """);

      //  waitForInputToContinue(scanner);

        try {

            ControlTowerClient controlTowerClient = ControlTowerClient.builder()
                .region(Region.US_EAST_1)
                 .credentialsProvider(DefaultCredentialsProvider.create())
                .httpClientBuilder(UrlConnectionHttpClient.builder()) // avoids Apache HTTP client
                .build() ;

            runScenario(controlTowerClient);

        } catch (ControlTowerException e) {
            logger.error("Control Tower service error: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
        }
    }

    private static void runScenario(ControlTowerClient controlTowerClient) {
        try {
            // Step 1: Check Control Tower setup
            logger.info(DASHES);
            logger.info("Step 1: Checking Control Tower setup...");

           /*
            List<LandingZoneSummary> landingZones = ControlTowerActions.listLandingZones(controlTowerClient);
            if (landingZones.isEmpty()) {
                logger.warn("⚠️ No landing zones found. Control Tower may not be set up.");
                logger.warn("Please set up Control Tower in the AWS Console first.");
                return;
            }

            logger.info("Found {} landing zone(s):", landingZones.size());
            for (LandingZoneSummary landingZone : landingZones) {
                logger.info("  - ARN: {}", landingZone.arn());
            }

            waitForInputToContinue(scanner);

            */

            // Step 2: List available baselines
            logger.info(DASHES);
            logger.info("Step 2: Listing available baselines...");

            List<BaselineSummary> baselines = ControlTowerActions.listBaselines(controlTowerClient);
            if (baselines.isEmpty()) {
                logger.warn("No baselines available.");
            } else {
                logger.info("Available baselines:");
                for (BaselineSummary baseline : baselines) {
                    logger.info("  - Name: {}", baseline.name());
                    logger.info("    ARN: {}", baseline.arn());
                    logger.info("    Description: {}", baseline.description());
                }
            }

            waitForInputToContinue(scanner);
            /*

            logger.info(DASHES);
            logger.info("🎉 Control Tower Basics scenario completed successfully!");


             */
        } catch (ControlTowerException e) {
            logger.error("Control Tower service error: {}", e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                logger.info("Continuing with the program...\n");
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[controltower.java2.controltower_scenario.main]