// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.controltower;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.controlcatalog.ControlCatalogClient;
import software.amazon.awssdk.services.controlcatalog.model.ControlSummary;
import software.amazon.awssdk.services.organizations.OrganizationsClient;
import software.amazon.awssdk.services.organizations.model.*;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import static java.lang.System.*;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class ControlTowerScenario {




    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    static Scanner scanner = new Scanner(in);

    private OrganizationsClient orgClient;
    private ControlCatalogClient catClient;
    private String ouArn;
    private String stack = null;
    private String ouId = null;
    private String accountId = null;
    private String landingZoneArn = null;
    private boolean useLandingZone = false;

    static {
        // Disable AWS CRT logging completely
        System.setProperty("aws.crt.log.level", "OFF");
    }

    public static void main(String[] args) {



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
        System.out.println("""
                               Some demo operations require the use of a landing zone.
                               You can use an existing landing zone or opt out of these operations in the demo.
                               For instructions on how to set up a landing zone,
                               see https://docs.aws.amazon.com/controltower/latest/userguide/getting-started-from-console.html
                               """);

            out.println("Step 1: Listing landing zones...");
            waitForInputToContinue(scanner);

            List<LandingZoneSummary> landingZones = actions.listLandingZonesAsync().join();
            if (!landingZones.isEmpty()) {
                System.out.println("\nAvailable Landing Zones:");
                for (int i = 0; i < landingZones.size(); i++) {
                    LandingZoneSummary lz = landingZones.get(i);
                    System.out.printf("%d) %s%n", i + 1, lz.arn());
                }
            } else {
                System.out.println("No landing zones found.");
            }

        /*
            if (askYesNo(
                        "Do you want to use the first landing zone in the list (" +
                                landingZones.get(0).arn() + ")? (y/n): ")) {

                    useLandingZone = true;
                    landingZoneArn = landingZones.get(0).arn();

                    System.out.println("Using landing zone ID: " + landingZoneArn);

                    // CALL: setupOrganization()
                    String sandboxOuId = setupOrganization();
                    ouId = sandboxOuId;

                } else if (askYesNo(
                        "Do you want to use a different existing Landing Zone for this demo? (y/n): ")) {

                    useLandingZone = true;

                    System.out.print("Enter landing zone id: ");
                    landingZoneArn = scanner.nextLine().trim();

                    // CALL: setupOrganization()
                    String sandboxOuId = setupOrganization();
                    ouId = sandboxOuId;
                }
            }
            */
            waitForInputToContinue(scanner);

            // ----------------------------------------------------------
            // CALL: ControlTowerActions.listBaselines()
            // ----------------------------------------------------------
            out.println(DASHES);
            out.println("Step 2: Listing available baselines...");

            List<BaselineSummary> baselines = actions.listBaselinesAsync().join();
            baselines.forEach(b -> {
                out.println("Baseline: " + b.name());
                out.println("  ARN: " + b.arn());
            });
            waitForInputToContinue(scanner);

            /*
            // ----------------------------------------------------------
            // CALL: ControlTowerActions.listControls()
            // ----------------------------------------------------------
            out.println(DASHES);
            out.println("Managing Controls:");

            List<ControlSummary> controls =
                    ControlTowerActions.listControls(catClient);

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
                List<EnabledControlSummary> enabledControls =
                        ControlTowerActions.listEnabledControls(controlTowerClient, targetOu);

                out.println("\nListing enabled controls:");

                for (int i = 0; i < enabledControls.size(); i++) {
                    EnabledControlSummary ec = enabledControls.get(i);
                    out.println(String.format("%d. %s",
                            (i + 1), ec.controlIdentifier()));
                }

                // Determine first non-enabled control
                Set<String> enabledControlArns = enabledControls.stream()
                        .map(EnabledControlSummary::arn)
                        .collect(Collectors.toSet());

                String controlArnToEnable = controls.stream()
                        .map(ControlSummary::arn)
                        .filter(arn -> !enabledControlArns.contains(arn))
                        .findFirst()
                        .orElse(null);

                waitForInputToContinue(scanner);

                // ----------------------------------------------------------
                // CALL: ControlTowerActions.enableControl()
                // ----------------------------------------------------------
                if (controlArnToEnable != null &&
                        askYesNo("Do you want to enable the control "
                                + controlArnToEnable + "? (y/n): ")) {

                    out.println("\nEnabling control: " + controlArnToEnable);

                    String operationId =
                            ControlTowerActions.enableControl(
                                    controlTowerClient, controlArnToEnable, targetOu);

                    if (operationId != null) {
                        out.println("Enabled control with operation id " + operationId);
                    }
                }
                waitForInputToContinue(scanner);

                // ----------------------------------------------------------
                // CALL: ControlTowerActions.disableControl()
                // ----------------------------------------------------------
                if (controlArnToEnable != null &&
                        askYesNo("Do you want to disable the control? (y/n): ")) {

                    out.println("\nDisabling control...");

                    String operationId =
                            ControlTowerActions.disableControl(
                                    controlTowerClient, controlArnToEnable, targetOu);

                    out.println("Disable operation ID: " + operationId);
                }



            // Final pause
            waitForInputToContinue(scanner);

            out.println("\nThis concludes the example scenario.");
            out.println("Thanks for watching!");
            out.println(DASHES);
            out.println(DASHES);
            out.println("Scenario completed Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }

         */
    }

    public String setupOrganization() {
        System.out.println("\nChecking organization status...");
        String orgId;

        // -------------------------------
        // 1. Describe or create organization
        // -------------------------------
        try {
            DescribeOrganizationResponse desc = orgClient.describeOrganization();
            orgId = desc.organization().id();
            System.out.println("Account is part of organization: " + orgId);

        } catch (AwsServiceException e) {

            if ("AWSOrganizationsNotInUseException".equals(e.awsErrorDetails().errorCode())) {

                System.out.println("No organization found. Creating one...");

                CreateOrganizationResponse create =
                        orgClient.createOrganization(
                                CreateOrganizationRequest.builder()
                                        .featureSet("ALL")
                                        .build()
                        );

                orgId = create.organization().id();
                System.out.println("Created organization: " + orgId);

                // ✅ NO WAITERS — we simply proceed
                // Organizations may take time to stabilize,
                // but this method returns immediately.

            } else {
                throw e;
            }
        }

        // -------------------------------
        // 2. Locate or create Sandbox OU
        // -------------------------------
        String sandboxOuId = null;

        ListRootsResponse roots = orgClient.listRoots();
        String rootId = roots.roots().get(0).id();

        System.out.println("Checking Sandbox OU...");

        for (ListOrganizationalUnitsForParentResponse page :
                orgClient.listOrganizationalUnitsForParentPaginator(
                        ListOrganizationalUnitsForParentRequest.builder()
                                .parentId(rootId)
                                .build()
                )) {

            for (OrganizationalUnit ou : page.organizationalUnits()) {
                if ("Sandbox".equals(ou.name())) {
                    sandboxOuId = ou.id();
                    this.ouArn = ou.arn();
                    System.out.println("Found Sandbox OU: " + sandboxOuId);
                    break;
                }
            }

            if (sandboxOuId != null) {
                break;
            }
        }

        // -------------------------------
        // Create OU if missing
        // -------------------------------
        if (sandboxOuId == null) {
            System.out.println("Creating Sandbox OU...");

            CreateOrganizationalUnitResponse created =
                    orgClient.createOrganizationalUnit(
                            CreateOrganizationalUnitRequest.builder()
                                    .parentId(rootId)
                                    .name("Sandbox")
                                    .build()
                    );

            sandboxOuId = created.organizationalUnit().id();
            this.ouArn = created.organizationalUnit().arn();

            System.out.println("Created Sandbox OU: " + sandboxOuId);

            // ✅ NO WAITER — return immediately
        }

        return sandboxOuId;
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
            String in = sc.nextLine();
            if ("c".equalsIgnoreCase(in.trim())) {
                out.println("Continuing...");
                break;
            }
        }
    }
}
