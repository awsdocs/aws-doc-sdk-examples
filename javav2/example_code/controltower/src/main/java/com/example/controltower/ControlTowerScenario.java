// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.controltower;

import software.amazon.awssdk.services.controlcatalog.ControlCatalogClient;
import software.amazon.awssdk.services.controlcatalog.model.ControlSummary;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.*;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.*;

import java.util.List;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import static java.lang.System.*;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ControlTowerScenario {

    //private static final Logger logger = LoggerFactory.getLogger(ControlTowerScenario.class);
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final Scanner scanner = new Scanner(in);

    private OrganizationsClient orgClient;
    private ControlCatalogClient catClient;

    private static String ouArn;
    private static String ouId = null;
    private static String landingZoneArn = null;
    private static boolean useLandingZone = false;

    private String stack = null;
    private String accountId = null;

    static {
        // Disable AWS CRT logging completely
        System.setProperty("aws.crt.log.level", "OFF");
    }

    public static void main(String[] args) {


        // -----------------------------
        // Your program logic here
        // -----------------------------
        System.out.println("Hello! No AWS SDK logging should appear now.");


        out.println(DASHES);
        out.println("Welcome to the AWS Control Tower basics scenario!");
        out.println(DASHES);

        try {
            runScenario();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------
    // Main scenario flow
    // ----------------------------------------------------------
    private static void runScenario() {

        ControlTowerActions actions = new ControlTowerActions();

        out.println(DASHES);
        out.println("""
    Some demo operations require the use of a landing zone.
    You can use an existing landing zone or opt out of these operations in the demo.
    For instructions on how to set up a landing zone,
    see https://docs.aws.amazon.com/controltower/latest/userguide/getting-started-from-console.html
    """);

        out.println("Step 1: Listing landing zones...");
        waitForInputToContinue(scanner);

        List<LandingZoneSummary> landingZones =
                actions.listLandingZonesAsync().join();

        /*
         * IMPORTANT:
         * If no landing zones exist, skip all landing-zone logic
         * and continue directly to Step 2.
         */
        if (landingZones.isEmpty()) {
            out.println("No landing zones found. Landing-zone-dependent steps will be skipped.");
            useLandingZone = false;
            waitForInputToContinue(scanner);
        } else {

            // Display landing zones
            out.println("\nAvailable Landing Zones:");
            for (int i = 0; i < landingZones.size(); i++) {
                out.printf("%d) %s%n", i + 1, landingZones.get(i).arn());
            }

            // Ask whether to use the first landing zone
            if (askYesNo("Do you want to use the first landing zone in the list (" +
                    landingZones.get(0).arn() + ")? (y/n): ")) {

                useLandingZone = true;
                landingZoneArn = landingZones.get(0).arn();

            } else if (askYesNo("Do you want to use a different existing Landing Zone for this demo? (y/n): ")) {

                useLandingZone = true;
                out.print("Enter landing zone ARN: ");
                landingZoneArn = scanner.nextLine().trim();

            } else {
                out.println("Proceeding without a landing zone.");
                useLandingZone = false;
                waitForInputToContinue(scanner);
            }
        }


        /*
        // Setup organization only if a landing zone is used
        out.println("Using landing zone ARN: " + landingZoneArn);

        ControlTowerActions.OrgSetupResult result =
                actions.setupOrganizationAsync().join();

        ouId = result.sandboxOuArn();
        logger.info("Organization ID: {}", result.orgId());
        logger.info("Using Sandbox OU ARN: {}", ouId);


         */
     //

        // ----------------------------------------------------------
        // CALL: ControlTowerActions.listBaselines()
        // ----------------------------------------------------------
        out.println(DASHES);
        out.println("Step 2: Listing available baselines...");
        waitForInputToContinue(scanner);


        List<BaselineSummary> baselines = actions.listBaselinesAsync().join();
        baselines.forEach(b -> {
            out.println("Baseline: " + b.name());
            out.println("  ARN: " + b.arn());
        });

        waitForInputToContinue(scanner);

        // ----------------------------------------------------------
        // CALL: ControlTowerActions.listControls()
        // ----------------------------------------------------------
        out.println(DASHES);
        out.println("Step 3: Managing Controls:");

        List<ControlSummary> controls = actions.listControlsAsync().join();
        out.println("\nListing first 5 available Controls:");

        for (int i = 0; i < Math.min(5, controls.size()); i++) {
            ControlSummary c = controls.get(i);
            out.println(String.format("%d. %s - %s",
                    (i + 1), c.name(), c.arn()));
        }

        if (useLandingZone) {

            String targetOu = ouArn;
            waitForInputToContinue(scanner);

            // ----------------------------------------------------------
            // CALL: ControlTowerActions.listEnabledControls()
            // ----------------------------------------------------------
            // List<EnabledControlSummary> enabledControls =
            //     ControlTowerActions.listEnabledControls(controlTowerClient, targetOu);

            out.println("Listing enabled controls:");

            // for (int i = 0; i < enabledControls.size(); i++) {
            //     EnabledControlSummary ec = enabledControls.get(i);
            //     out.println(String.format("%d. %s",
            //             (i + 1), ec.controlIdentifier()));
            // }

            // Determine first non-enabled control
            // Set<String> enabledControlArns = enabledControls.stream()
            //         .map(EnabledControlSummary::arn)
            //         .collect(Collectors.toSet());

            // String controlArnToEnable = controls.stream()
            //         .map(ControlSummary::arn)
            //         .filter(arn -> !enabledControlArns.contains(arn))
            //         .findFirst()
            //         .orElse(null);

            waitForInputToContinue(scanner);

            // ----------------------------------------------------------
            // CALL: ControlTowerActions.enableControl()
            // ----------------------------------------------------------
            // if (controlArnToEnable != null &&
            //         askYesNo("Do you want to enable the control "
            //                 + controlArnToEnable + "? (y/n): ")) {

            // out.println("\nEnabling control: " + controlArnToEnable);

            // String operationId =
            //     ControlTowerActions.enableControl(controlTowerClient,
            //         controlArnToEnable, targetOu);

            // if (operationId != null) {
            //     out.println("Enabled control with operation id " + operationId);
            // }
        }

        waitForInputToContinue(scanner);

        // ----------------------------------------------------------
        // CALL: ControlTowerActions.disableControl()
        // ----------------------------------------------------------
        // if (controlArnToEnable != null &&
        //         askYesNo("Do you want to disable the control? (y/n): ")) {

        //     out.println("\nDisabling control...");

        //     String operationId =
        //         ControlTowerActions.disableControl(controlTowerClient,
        //             controlArnToEnable, targetOu);

        //     out.println("Disable operation ID: " + operationId);
        // }
    }

    // ----------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------
    private static boolean askYesNo(String msg) {
        out.print(msg);
        return scanner.nextLine().trim().toLowerCase().startsWith("y");
    }

    private static void waitForInputToContinue(Scanner sc) {
        out.println("\nEnter 'c' then <ENTER> to continue:");
        while (true) {
            String input = sc.nextLine();
            if ("c".equalsIgnoreCase(input.trim())) {
                out.println("Continuing...");
                break;
            }
        }
    }
}
