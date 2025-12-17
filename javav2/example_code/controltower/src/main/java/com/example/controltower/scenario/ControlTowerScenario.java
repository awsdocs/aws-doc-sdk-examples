// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.controltower.scenario;

import software.amazon.awssdk.services.controlcatalog.ControlCatalogClient;
import software.amazon.awssdk.services.controlcatalog.model.ControlSummary;
import software.amazon.awssdk.services.controltower.model.*;
import software.amazon.awssdk.services.organizations.OrganizationsClient;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;


/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * Use the AWS SDK for Java (v2) to create an AWS Control Tower client
 * and list all available baselines.
 * This example uses the default settings specified in your shared credentials
 * and config files.
 */

// snippet-start:[controltower.java2.controltower_scenario.main]
public class ControlTowerScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final Scanner scanner = new Scanner(in);

    private static OrganizationsClient orgClient;
    private static ControlCatalogClient catClient;

    private static String ouId = null;
    private static String ouArn = null;
    private static String landingZoneArn = null;
    private static boolean useLandingZone = false;

    private String stack = null;
    private String accountId = null;

    public static void main(String[] args) {

        out.println(DASHES);
        out.println("Welcome to the AWS Control Tower basics scenario!");
        out.println(DASHES);

        try {
            runScenarioAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -----------------------------
    // Utilities
    // -----------------------------
    private static boolean askYesNo(String msg) {
        out.print(msg);
        return scanner.nextLine().trim().toLowerCase().startsWith("y");
    }

    private static void runScenarioAsync() {
        ControlTowerActions actions = new ControlTowerActions();

        // -----------------------------
        // Step 1: Landing Zones
        // -----------------------------
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

        if (landingZones.isEmpty()) {
            out.println("No landing zones found. Landing-zone-dependent steps will be skipped.");
            useLandingZone = false;
            waitForInputToContinue(scanner);
        } else {
            out.println("\nAvailable Landing Zones:");
            for (int i = 0; i < landingZones.size(); i++) {
                out.printf("%d) %s%n", i + 1, landingZones.get(i).arn());
            }

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

        // -----------------------------
        // Setup Organization + Sandbox OU
        // -----------------------------
        if (useLandingZone) {
            out.println("Using landing zone ARN: " + landingZoneArn);

            ControlTowerActions.OrgSetupResult result =
                    actions.setupOrganizationAsync().join();

            ouArn = result.sandboxOuArn();
            ouId  = result.sandboxOuArn();

            out.println("Organization ID: " + result.orgId());
            out.println("Using Sandbox OU ARN: " + ouArn);
        }

        // -----------------------------
        // Step 2: Baselines
        // -----------------------------
        out.println(DASHES);
        out.println("Step 2: Listing available baselines...");
        waitForInputToContinue(scanner);

        List<BaselineSummary> baselines =
                actions.listBaselinesAsync().join();

        BaselineSummary controlTowerBaseline = null;
        for (BaselineSummary b : baselines) {
            out.println("Baseline: " + b.name());
            out.println("  ARN: " + b.arn());
            if ("AWSControlTowerBaseline".equals(b.name())) {
                controlTowerBaseline = b;
            }
        }

        waitForInputToContinue(scanner);

        if (useLandingZone && controlTowerBaseline != null) {

            out.println("\nListing enabled baselines:");
            List<EnabledBaselineSummary> enabledBaselines =
                    actions.listEnabledBaselinesAsync().join();

            EnabledBaselineSummary identityCenterBaseline = null;
            for (EnabledBaselineSummary eb : enabledBaselines) {
                out.println(eb.baselineIdentifier());
                if (eb.baselineIdentifier().contains("baseline/LN25R72TTG6IGPTQ")) {
                    identityCenterBaseline = eb;
                }
            }

            if (askYesNo("Do you want to enable the Control Tower Baseline? (y/n): ")) {
                out.println("\nEnabling Control Tower Baseline...");

                String enabledBaselineId =
                        actions.enableBaselineAsync(
                                ouArn,
                                controlTowerBaseline.arn(),
                                "4.0"
                        ).join();

                out.println("Enabled baseline operation ID: " + enabledBaselineId);

                if (askYesNo("Do you want to reset the Control Tower Baseline? (y/n): ")) {
                    String operationId =
                            actions.resetEnabledBaselineAsync(enabledBaselineId).join();
                    out.println("Reset baseline operation ID: " + operationId);
                }

                if (askYesNo("Do you want to disable the Control Tower Baseline? (y/n): ")) {
                    String operationId =
                            actions.disableBaselineAsync(enabledBaselineId).join();
                    out.println("Disabled baseline operation ID: " + operationId);

                    // Re-enable for next steps
                    actions.enableBaselineAsync(
                            ouArn,
                            controlTowerBaseline.arn(),
                            "4.0"
                    ).join();
                }
            }
        }

        // -----------------------------
        // Step 3: Controls
        // -----------------------------
        out.println(DASHES);
        out.println("Step 3: Managing Controls:");
        waitForInputToContinue(scanner);

        List<ControlSummary> controls =
                actions.listControlsAsync().join();

        out.println("\nListing first 5 available Controls:");
        for (int i = 0; i < Math.min(5, controls.size()); i++) {
            ControlSummary c = controls.get(i);
            System.out.format("%d. %s - %s".formatted(i + 1, c.name(), c.arn()));
        }

        if (useLandingZone) {
            waitForInputToContinue(scanner);

            List<EnabledControlSummary> enabledControls =
                    actions.listEnabledControlsAsync(ouArn).join();

            out.println("\nListing enabled controls:");
            for (int i = 0; i < enabledControls.size(); i++) {
                out.println("%d. %s".formatted(i + 1, enabledControls.get(i).controlIdentifier()));
            }

            String controlArnToEnable = null;
            for (ControlSummary control : controls) {
                boolean enabled = enabledControls.stream()
                        .anyMatch(ec -> ec.controlIdentifier().equals(control.arn()));
                if (!enabled) {
                    controlArnToEnable = control.arn();
                    break;
                }
            }

            waitForInputToContinue(scanner);

            if (controlArnToEnable != null &&
                    askYesNo("Do you want to enable the control " + controlArnToEnable + "? (y/n): ")) {

                String operationId =
                        actions.enableControlAsync(controlArnToEnable, ouArn).join();

                out.println("Enabled control with operation ID: " + operationId);
            }

            waitForInputToContinue(scanner);

            if (controlArnToEnable != null &&
                    askYesNo("Do you want to disable the control? (y/n): ")) {

                String operationId =
                        actions.disableControlAsync(controlArnToEnable, ouArn).join();

                out.println("Disable operation ID: " + operationId);
            }
        }

        out.println("\nThis concludes the example scenario.");
        out.println("Thanks for watching!");
        out.println(DASHES);
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
// snippet-end:[controltower.java2.controltower_scenario.main]