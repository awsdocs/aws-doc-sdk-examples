// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.controltower;

import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.*;
import software.amazon.awssdk.services.controltower.paginators.ListBaselinesIterable;
import software.amazon.awssdk.services.controltower.paginators.ListEnabledBaselinesIterable;
import software.amazon.awssdk.services.controltower.paginators.ListEnabledControlsIterable;
import software.amazon.awssdk.services.controltower.paginators.ListLandingZonesIterable;
import software.amazon.awssdk.services.controlcatalog.ControlCatalogClient;
import software.amazon.awssdk.services.controlcatalog.paginators.ListControlsIterable;
import software.amazon.awssdk.core.exception.SdkException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java code example shows how to perform AWS Control Tower operations.
 */

// snippet-start:[controltower.java2.controltower_actions.main]
public class ControlTowerActions {

    private static final Logger logger = LoggerFactory.getLogger(ControlTowerActions.class);

    // snippet-start:[controltower.java2.list_landing_zones.main]
    /**
     * Lists all landing zones using pagination to retrieve complete results.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @return a list of all landing zones
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static List<LandingZoneSummary> listLandingZones(ControlTowerClient controlTowerClient) {
        try {
            List<LandingZoneSummary> landingZones = new ArrayList<>();
            
            String nextToken = null;
            
            do {
                ListLandingZonesRequest.Builder requestBuilder = ListLandingZonesRequest.builder();
                if (nextToken != null) {
                    requestBuilder.nextToken(nextToken);
                }
                
                ListLandingZonesResponse response = controlTowerClient.listLandingZones(requestBuilder.build());
                
                if (response.landingZones() != null) {
                    landingZones.addAll(response.landingZones());
                }
                
                nextToken = response.nextToken();
            } while (nextToken != null);
            
            logger.info("Retrieved {} landing zones", landingZones.size());
            return landingZones;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "AccessDeniedException":
                    logger.error("Access denied when listing landing zones: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error listing landing zones: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error listing landing zones: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.list_landing_zones.main]

    // snippet-start:[controltower.java2.list_baselines.main]
    /**
     * Lists all available baselines using pagination to retrieve complete results.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @return a list of all baselines
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static List<BaselineSummary> listBaselines(ControlTowerClient controlTowerClient) {
        try {
            List<BaselineSummary> baselines = new ArrayList<>();
            
            String nextToken = null;
            
            do {
                ListBaselinesRequest.Builder requestBuilder = ListBaselinesRequest.builder();
                if (nextToken != null) {
                    requestBuilder.nextToken(nextToken);
                }
                
                ListBaselinesResponse response = controlTowerClient.listBaselines(requestBuilder.build());
                
                if (response.baselines() != null) {
                    baselines.addAll(response.baselines());
                }
                
                nextToken = response.nextToken();
            } while (nextToken != null);
            
            logger.info("Retrieved {} baselines", baselines.size());
            return baselines;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "AccessDeniedException":
                    logger.error("Access denied when listing baselines: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error listing baselines: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error listing baselines: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.list_baselines.main]

    // snippet-start:[controltower.java2.list_enabled_baselines.main]
    /**
     * Lists all enabled baselines using pagination to retrieve complete results.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @return a list of all enabled baselines
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static List<EnabledBaselineSummary> listEnabledBaselines(ControlTowerClient controlTowerClient) {
        try {
            List<EnabledBaselineSummary> enabledBaselines = new ArrayList<>();
            
            String nextToken = null;
            
            do {
                ListEnabledBaselinesRequest.Builder requestBuilder = ListEnabledBaselinesRequest.builder();
                if (nextToken != null) {
                    requestBuilder.nextToken(nextToken);
                }
                
                ListEnabledBaselinesResponse response = controlTowerClient.listEnabledBaselines(requestBuilder.build());
                
                if (response.enabledBaselines() != null) {
                    enabledBaselines.addAll(response.enabledBaselines());
                }
                
                nextToken = response.nextToken();
            } while (nextToken != null);
            
            logger.info("Retrieved {} enabled baselines", enabledBaselines.size());
            return enabledBaselines;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ResourceNotFoundException":
                    logger.error("Target not found when listing enabled baselines: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error listing enabled baselines: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error listing enabled baselines: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.list_enabled_baselines.main]

    // snippet-start:[controltower.java2.enable_baseline.main]
    /**
     * Enables a baseline for a specified target.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param baselineIdentifier the identifier of the baseline to enable
     * @param baselineVersion the version of the baseline to enable
     * @param targetIdentifier the identifier of the target (e.g., OU ARN)
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static String enableBaseline(ControlTowerClient controlTowerClient, 
                                       String baselineIdentifier, 
                                       String baselineVersion,
                                       String targetIdentifier) {
        try {
            EnableBaselineRequest request = EnableBaselineRequest.builder()
                    .baselineIdentifier(baselineIdentifier)
                    .baselineVersion(baselineVersion)
                    .targetIdentifier(targetIdentifier)
                    .build();

            EnableBaselineResponse response = controlTowerClient.enableBaseline(request);
            String operationId = response.operationIdentifier();
            
            logger.info("Enabled baseline with operation ID: {}", operationId);
            return operationId;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ValidationException":
                    if (e.getMessage().contains("already enabled")) {
                        logger.info("Baseline is already enabled for this target");
                        return null;
                    }
                    logger.error("Validation error enabling baseline: {}", e.getMessage());
                    break;
                case "ConflictException":
                    logger.error("Conflict enabling baseline: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error enabling baseline: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error enabling baseline: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.enable_baseline.main]

    // snippet-start:[controltower.java2.disable_baseline.main]
    /**
     * Disables a baseline for a specified target.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param enabledBaselineIdentifier the identifier of the enabled baseline to disable
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static String disableBaseline(ControlTowerClient controlTowerClient, 
                                        String enabledBaselineIdentifier) {
        try {
            DisableBaselineRequest request = DisableBaselineRequest.builder()
                    .enabledBaselineIdentifier(enabledBaselineIdentifier)
                    .build();

            DisableBaselineResponse response = controlTowerClient.disableBaseline(request);
            String operationId = response.operationIdentifier();
            
            logger.info("Disabled baseline with operation ID: {}", operationId);
            return operationId;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ConflictException":
                    logger.error("Conflict disabling baseline: {}", e.getMessage());
                    break;
                case "ResourceNotFoundException":
                    logger.error("Baseline not found for disabling: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error disabling baseline: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error disabling baseline: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.disable_baseline.main]

    // snippet-start:[controltower.java2.get_baseline_operation.main]
    /**
     * Gets the status of a baseline operation.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param operationIdentifier the identifier of the operation
     * @return the operation status
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static BaselineOperationStatus getBaselineOperation(ControlTowerClient controlTowerClient, 
                                                              String operationIdentifier) {
        try {
            GetBaselineOperationRequest request = GetBaselineOperationRequest.builder()
                    .operationIdentifier(operationIdentifier)
                    .build();

            GetBaselineOperationResponse response = controlTowerClient.getBaselineOperation(request);
            BaselineOperationStatus status = response.baselineOperation().status();
            
            logger.info("Baseline operation status: {}", status);
            return status;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ResourceNotFoundException":
                    logger.error("Baseline operation not found: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error getting baseline operation status: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error getting baseline operation status: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.get_baseline_operation.main]

    // snippet-start:[controltower.java2.list_enabled_controls.main]
    /**
     * Lists all enabled controls for a specific target using pagination.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param targetIdentifier the identifier of the target (e.g., OU ARN)
     * @return a list of enabled controls
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static List<EnabledControlSummary> listEnabledControls(ControlTowerClient controlTowerClient, 
                                                                 String targetIdentifier) {
        try {
            List<EnabledControlSummary> enabledControls = new ArrayList<>();
            
            // Use paginator to retrieve all results
            ListEnabledControlsIterable listIterable = controlTowerClient.listEnabledControlsPaginator(
                ListEnabledControlsRequest.builder()
                    .targetIdentifier(targetIdentifier)
                    .build()
            );
            
            listIterable.stream()
                    .flatMap(response -> response.enabledControls().stream())
                    .forEach(enabledControls::add);
            
            logger.info("Retrieved {} enabled controls for target {}", enabledControls.size(), targetIdentifier);
            return enabledControls;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "AccessDeniedException":
                    logger.error("Access denied when listing enabled controls: {}", e.getMessage());
                    break;
                case "ResourceNotFoundException":
                    if (e.getMessage().contains("not registered with AWS Control Tower")) {
                        logger.error("Control Tower must be enabled to work with controls");
                    } else {
                        logger.error("Target not found when listing enabled controls: {}", e.getMessage());
                    }
                    break;
                default:
                    logger.error("Error listing enabled controls: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error listing enabled controls: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.list_enabled_controls.main]

    // snippet-start:[controltower.java2.enable_control.main]
    /**
     * Enables a control for a specified target.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param controlIdentifier the identifier of the control to enable
     * @param targetIdentifier the identifier of the target (e.g., OU ARN)
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static String enableControl(ControlTowerClient controlTowerClient, 
                                      String controlIdentifier, 
                                      String targetIdentifier) {
        try {
            EnableControlRequest request = EnableControlRequest.builder()
                    .controlIdentifier(controlIdentifier)
                    .targetIdentifier(targetIdentifier)
                    .build();

            EnableControlResponse response = controlTowerClient.enableControl(request);
            String operationId = response.operationIdentifier();
            
            logger.info("Enabled control with operation ID: {}", operationId);
            return operationId;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ValidationException":
                    if (e.getMessage().contains("already enabled")) {
                        logger.info("Control is already enabled for this target");
                        return null;
                    }
                    logger.error("Validation error enabling control: {}", e.getMessage());
                    break;
                case "ResourceNotFoundException":
                    if (e.getMessage().contains("not registered with AWS Control Tower")) {
                        logger.error("Control Tower must be enabled to work with controls");
                    } else {
                        logger.error("Control not found: {}", e.getMessage());
                    }
                    break;
                default:
                    logger.error("Error enabling control: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error enabling control: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.enable_control.main]

    // snippet-start:[controltower.java2.disable_control.main]
    /**
     * Disables a control for a specified target.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param controlIdentifier the identifier of the control to disable
     * @param targetIdentifier the identifier of the target (e.g., OU ARN)
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static String disableControl(ControlTowerClient controlTowerClient, 
                                       String controlIdentifier, 
                                       String targetIdentifier) {
        try {
            DisableControlRequest request = DisableControlRequest.builder()
                    .controlIdentifier(controlIdentifier)
                    .targetIdentifier(targetIdentifier)
                    .build();

            DisableControlResponse response = controlTowerClient.disableControl(request);
            String operationId = response.operationIdentifier();
            
            logger.info("Disabled control with operation ID: {}", operationId);
            return operationId;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ResourceNotFoundException":
                    logger.error("Control not found for disabling: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error disabling control: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error disabling control: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.disable_control.main]

    // snippet-start:[controltower.java2.get_control_operation.main]
    /**
     * Gets the status of a control operation.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param operationIdentifier the identifier of the operation
     * @return the operation status
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static ControlOperationStatus getControlOperation(ControlTowerClient controlTowerClient, 
                                                            String operationIdentifier) {
        try {
            GetControlOperationRequest request = GetControlOperationRequest.builder()
                    .operationIdentifier(operationIdentifier)
                    .build();

            GetControlOperationResponse response = controlTowerClient.getControlOperation(request);
            ControlOperationStatus status = response.controlOperation().status();
            
            logger.info("Control operation status: {}", status);
            return status;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ResourceNotFoundException":
                    logger.error("Control operation not found: {}", e.getMessage());
                    break;
                default:
                    logger.error("Error getting control operation status: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error getting control operation status: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.get_control_operation.main]

    // snippet-start:[controltower.java2.list_controls.main]
    /**
     * Lists all controls in the Control Tower control catalog.
     *
     * @param controlCatalogClient the Control Catalog client to use for the operation
     * @return a list of controls
     * @throws SdkException if a service-specific error occurs
     */
    public static List<software.amazon.awssdk.services.controlcatalog.model.ControlSummary> listControls(
            ControlCatalogClient controlCatalogClient) {
        try {
            List<software.amazon.awssdk.services.controlcatalog.model.ControlSummary> controls = new ArrayList<>();
            
            ListControlsIterable paginator = controlCatalogClient.listControlsPaginator(
                    software.amazon.awssdk.services.controlcatalog.model.ListControlsRequest.builder().build());
            
            paginator.stream()
                    .flatMap(response -> response.controls().stream())
                    .forEach(controls::add);
            
            logger.info("Retrieved {} controls", controls.size());
            return controls;

        } catch (SdkException e) {
            if (e.getMessage().contains("AccessDeniedException")) {
                logger.error("Access denied. Please ensure you have the necessary permissions.");
            } else {
                logger.error("Couldn't list controls. Here's why: {}", e.getMessage());
            }
            throw e;
        }
    }
    // snippet-end:[controltower.java2.list_controls.main]

    // snippet-start:[controltower.java2.reset_enabled_baseline.main]
    /**
     * Resets an enabled baseline for a specific target.
     *
     * @param controlTowerClient the Control Tower client to use for the operation
     * @param enabledBaselineIdentifier the identifier of the enabled baseline to reset
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException if an SDK error occurs
     */
    public static String resetEnabledBaseline(ControlTowerClient controlTowerClient, 
                                             String enabledBaselineIdentifier) {
        try {
            ResetEnabledBaselineRequest request = ResetEnabledBaselineRequest.builder()
                    .enabledBaselineIdentifier(enabledBaselineIdentifier)
                    .build();

            ResetEnabledBaselineResponse response = controlTowerClient.resetEnabledBaseline(request);
            String operationId = response.operationIdentifier();
            
            logger.info("Reset enabled baseline with operation ID: {}", operationId);
            return operationId;

        } catch (ControlTowerException e) {
            String errorCode = e.awsErrorDetails().errorCode();
            switch (errorCode) {
                case "ResourceNotFoundException":
                    logger.error("Target not found: {}", e.getMessage());
                    break;
                default:
                    logger.error("Couldn't reset enabled baseline. Here's why: {}", e.getMessage());
            }
            throw e;
        } catch (SdkException e) {
            logger.error("SDK error resetting enabled baseline: {}", e.getMessage());
            throw e;
        }
    }
    // snippet-end:[controltower.java2.reset_enabled_baseline.main]
}
// snippet-end:[controltower.java2.controltower_actions.main]