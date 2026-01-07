package com.example.controltower;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.*;

import java.util.Scanner;

public class EnableBaselineExample {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String CONTROL_TOWER_BASELINE_ARN =
            "arn:aws:controltower:us-east-1::baseline/17BSJV3IGJ2QSGA2";

    private static final String BASELINE_VERSION = "5.0";

    private static final String OU_ARN =
            "arn:aws:organizations::814548047983:ou/o-teewdr5qvn/ou-v6oa-v0gd6i4y";

    public static void main(String[] args) {

        ControlTowerClient client = ControlTowerClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String enabledBaselineArn = null;

        // ------------------------------------------------------------
        // 1. List enabled baselines
        // ------------------------------------------------------------
        try {
            enabledBaselineArn = findEnabledBaselineArn(client, CONTROL_TOWER_BASELINE_ARN, OU_ARN);
            if (enabledBaselineArn != null) {
                System.out.println("Baseline already enabled: " + enabledBaselineArn);
            } else {
                System.out.println("Baseline not enabled yet.");
            }
        } catch (ControlTowerException e) {
            printCtError("list enabled baselines", e);
        }

        waitForInput();

        // ------------------------------------------------------------
        // 2. Enable baseline if not enabled
        // ------------------------------------------------------------
        if (enabledBaselineArn == null) {
            try {
                EnableBaselineResponse enableResp = client.enableBaseline(
                        EnableBaselineRequest.builder()
                                .baselineIdentifier(CONTROL_TOWER_BASELINE_ARN)
                                .baselineVersion(BASELINE_VERSION)
                                .targetIdentifier(OU_ARN)
                                .build()
                );

                System.out.println("Enable operation started. Operation ID: " + enableResp.operationIdentifier());

                // Re-discover enabled baseline ARN dynamically
                try {
                    enabledBaselineArn = findEnabledBaselineArn(client, CONTROL_TOWER_BASELINE_ARN, OU_ARN);
                    System.out.println("Enabled baseline ARN discovered: " + enabledBaselineArn);
                } catch (ControlTowerException e) {
                    printCtError("re-list enabled baselines", e);
                }

            } catch (ControlTowerException e) {
                printCtError("enable baseline", e);
            }
        }

        waitForInput();

        // ------------------------------------------------------------
        // 3. Reset baseline
        // ------------------------------------------------------------
        if (enabledBaselineArn != null) {
            try {
                ResetEnabledBaselineResponse resetResp = client.resetEnabledBaseline(
                        ResetEnabledBaselineRequest.builder()
                                .enabledBaselineIdentifier(enabledBaselineArn)
                                .build()
                );
                System.out.println("Reset operation started. Operation ID: " + resetResp.operationIdentifier());
            } catch (ControlTowerException e) {
                printCtError("reset baseline", e);
            }
        } else {
            System.out.println("Skipping reset (baseline not enabled).");
        }

        waitForInput();

        // ------------------------------------------------------------
        // 4. Disable baseline
        // ------------------------------------------------------------
        if (enabledBaselineArn != null) {
            try {
                DisableBaselineResponse disableResp = client.disableBaseline(
                        DisableBaselineRequest.builder()
                                .enabledBaselineIdentifier(enabledBaselineArn)
                                .build()
                );
                System.out.println("Disable operation started. Operation ID: " + disableResp.operationIdentifier());
            } catch (ControlTowerException e) {
                printCtError("disable baseline", e);
            }
        } else {
            System.out.println("Skipping disable (baseline not enabled).");
        }

        waitForInput();

        // ------------------------------------------------------------
        // 5. Re-enable baseline
        // ------------------------------------------------------------
        try {
            EnableBaselineResponse reenableResp = client.enableBaseline(
                    EnableBaselineRequest.builder()
                            .baselineIdentifier(CONTROL_TOWER_BASELINE_ARN)
                            .baselineVersion(BASELINE_VERSION)
                            .targetIdentifier(OU_ARN)
                            .build()
            );
            System.out.println("Re-enable operation started. Operation ID: " + reenableResp.operationIdentifier());
        } catch (ControlTowerException e) {
            printCtError("re-enable baseline", e);
        }

        System.out.println("\nScenario complete.");
    }

    // ------------------------------------------------------------
    // Helper: Find enabled baseline ARN dynamically
    // ------------------------------------------------------------
    private static String findEnabledBaselineArn(
            ControlTowerClient client,
            String baselineDefinitionArn,
            String targetArn) {

        ListEnabledBaselinesResponse resp = client.listEnabledBaselines(
                ListEnabledBaselinesRequest.builder().build()
        );

        for (EnabledBaselineSummary b : resp.enabledBaselines()) {
            // baselineIdentifier() is the definition ARN
            // b.arn() is the enabled baseline ARN required for reset/disable
            if (baselineDefinitionArn.equals(b.baselineIdentifier())
                    && targetArn.equals(b.targetIdentifier())) {
                return b.arn();
            }
        }
        return null;
    }

    // ------------------------------------------------------------
    // Helper: Consistent string-based error handling
    // ------------------------------------------------------------
    private static void printCtError(String action, ControlTowerException e) {

        String errorCode = e.awsErrorDetails() != null
                ? e.awsErrorDetails().errorCode()
                : "UNKNOWN";

        int statusCode = e.statusCode();

        if (statusCode == 401 || statusCode == 403 ||
                "UnauthorizedException".equals(errorCode)) {
            System.out.println("Authorization failure during " + action + ": " + e.getMessage());

        } else if ("ValidationException".equals(errorCode) &&
                e.getMessage().contains("landing zone")) {
            System.out.println("Baseline is mandatory or locked by landing zone.");

        } else if ("ConflictException".equals(errorCode)) {
            System.out.println("Conflict during " + action + " (operation in progress or already applied).");

        } else {
            System.out.println("Control Tower error during " + action + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Wait for user input
    // ------------------------------------------------------------
    private static void waitForInput() {
        System.out.println("\nEnter 'c' then <ENTER> to continue:");
        while (!"c".equalsIgnoreCase(scanner.nextLine().trim())) {
        }
    }
}
