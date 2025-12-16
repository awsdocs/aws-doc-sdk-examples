// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.controltower;

import io.netty.handler.logging.LogLevel;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controlcatalog.ControlCatalogAsyncClient;
import software.amazon.awssdk.services.controltower.ControlTowerAsyncClient;
import software.amazon.awssdk.services.controltower.model.*;
import software.amazon.awssdk.services.controltower.paginators.ListBaselinesIterable;
import software.amazon.awssdk.services.controltower.paginators.ListBaselinesPublisher;
import software.amazon.awssdk.services.controltower.paginators.ListEnabledBaselinesIterable;
import software.amazon.awssdk.services.controltower.paginators.ListEnabledBaselinesPublisher;
import software.amazon.awssdk.services.controltower.paginators.ListEnabledControlsIterable;
import software.amazon.awssdk.services.controltower.paginators.ListEnabledControlsPublisher;
import software.amazon.awssdk.services.controltower.paginators.ListLandingZonesIterable;
import software.amazon.awssdk.services.controlcatalog.ControlCatalogClient;
import software.amazon.awssdk.services.controlcatalog.paginators.ListControlsIterable;
import software.amazon.awssdk.core.exception.SdkException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.controlcatalog.ControlCatalogAsyncClient;
import software.amazon.awssdk.services.controlcatalog.model.ControlSummary;
import software.amazon.awssdk.services.controlcatalog.model.ListControlsRequest;
import software.amazon.awssdk.services.controlcatalog.paginators.ListControlsPublisher;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.controltower.paginators.ListLandingZonesPublisher;
import software.amazon.awssdk.services.organizations.OrganizationsAsyncClient;
import software.amazon.awssdk.services.organizations.model.CreateOrganizationRequest;
import software.amazon.awssdk.services.organizations.model.CreateOrganizationResponse;
import software.amazon.awssdk.services.organizations.model.DescribeOrganizationResponse;
import software.amazon.awssdk.services.organizations.model.ListOrganizationalUnitsForParentRequest;
import software.amazon.awssdk.services.organizations.model.Organization;
import software.amazon.awssdk.services.organizations.model.OrganizationFeatureSet;
import software.amazon.awssdk.services.organizations.model.OrganizationalUnit;
import software.amazon.awssdk.services.organizations.paginators.ListOrganizationalUnitsForParentPublisher;
import software.amazon.awssdk.utils.Pair;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 * <p>
 * This Java code example shows how to perform AWS Control Tower operations.
 */

// snippet-start:[controltower.java2.controltower_actions.main]
public class ControlTowerActions {


    private static ControlCatalogAsyncClient controlCatalogAsyncClient;
    private static ControlTowerAsyncClient controlTowerAsyncClient;
    private static OrganizationsAsyncClient orgAsyncClient;
    private static final Logger logger = LoggerFactory.getLogger(ControlTowerActions.class);


    private static OrganizationsAsyncClient getAsyncOrgClient() {
        if (orgAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                    .maxConcurrency(50)
                    .connectionTimeout(Duration.ofSeconds(60))
                    .readTimeout(Duration.ofSeconds(60))
                    .writeTimeout(Duration.ofSeconds(60))
                    .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                    .apiCallTimeout(Duration.ofMinutes(2))
                    .apiCallAttemptTimeout(Duration.ofSeconds(90))
                    .build();

            orgAsyncClient = OrganizationsAsyncClient.builder()
                    .httpClient(httpClient)
                    .overrideConfiguration(overrideConfig)
                    .build();
        }
        return orgAsyncClient;
    }

    private static ControlCatalogAsyncClient getAsyncCatClient() {
        if (controlCatalogAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                    .maxConcurrency(100)
                    .connectionTimeout(Duration.ofSeconds(60))
                    .readTimeout(Duration.ofSeconds(60))
                    .writeTimeout(Duration.ofSeconds(60))
                    .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                    .apiCallTimeout(Duration.ofMinutes(2))
                    .apiCallAttemptTimeout(Duration.ofSeconds(90))
                    .retryStrategy(RetryMode.STANDARD)
                    .build();

            controlCatalogAsyncClient = ControlCatalogAsyncClient.builder()
                    .httpClient(httpClient)
                    .overrideConfiguration(overrideConfig)
                    .build();
        }
        return controlCatalogAsyncClient;
    }

    private static ControlTowerAsyncClient getAsyncClient() {
        if (controlTowerAsyncClient == null) {

            SdkAsyncHttpClient httpClient =
                    AwsCrtAsyncHttpClient.builder()
                            .maxConcurrency(100)
                            .connectionTimeout(Duration.ofSeconds(60))
                            .build();

            ClientOverrideConfiguration overrideConfig =
                    ClientOverrideConfiguration.builder()
                            .apiCallTimeout(Duration.ofMinutes(2))
                            .apiCallAttemptTimeout(Duration.ofSeconds(90))
                            .retryStrategy(RetryMode.STANDARD)
                            .build();

            controlTowerAsyncClient =
                    ControlTowerAsyncClient.builder()
                            .httpClient(httpClient)
                            .credentialsProvider(ProfileCredentialsProvider.create("default"))
                            .overrideConfiguration(overrideConfig)
                            .build();
        }

        return controlTowerAsyncClient;
    }

    public record OrgSetupResult(String orgId, String sandboxOuArn) {}

    public CompletableFuture<OrgSetupResult> setupOrganizationAsync() {
        logger.info("Starting organization setup…");

        OrganizationsAsyncClient client = getAsyncOrgClient();

        // Step 1: Describe or create organization
        CompletableFuture<Organization> orgFuture = client.describeOrganization()
                .thenApply(desc -> {
                    logger.info("Organization exists: {}", desc.organization().id());
                    return desc.organization();
                })
                .exceptionallyCompose(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof AwsServiceException awsEx &&
                            "AWSOrganizationsNotInUseException".equals(awsEx.awsErrorDetails().errorCode())) {
                        logger.info("No organization found. Creating one…");
                        return client.createOrganization(CreateOrganizationRequest.builder()
                                        .featureSet(OrganizationFeatureSet.ALL)
                                        .build())
                                .thenApply(createResp -> {
                                    logger.info("Created organization: {}", createResp.organization().id());
                                    return createResp.organization();
                                });
                    }
                    return CompletableFuture.failedFuture(
                            new CompletionException("Failed to describe or create organization", cause)
                    );
                });

        // Step 2: Locate Sandbox OU
        return orgFuture.thenCompose(org -> {
            String orgId = org.id();
            logger.info("Organization ID: {}", orgId);

            return client.listRoots()
                    .thenCompose(rootsResp -> {
                        if (rootsResp.roots().isEmpty()) {
                            return CompletableFuture.failedFuture(
                                    new RuntimeException("No root found in organization")
                            );
                        }
                        String rootId = rootsResp.roots().get(0).id();

                        ListOrganizationalUnitsForParentRequest ouRequest =
                                ListOrganizationalUnitsForParentRequest.builder()
                                        .parentId(rootId)
                                        .build();

                        ListOrganizationalUnitsForParentPublisher paginator =
                                client.listOrganizationalUnitsForParentPaginator(ouRequest);

                        AtomicReference<String> sandboxOuArnRef = new AtomicReference<>();

                        return paginator.subscribe(page -> {
                                    for (OrganizationalUnit ou : page.organizationalUnits()) {
                                        if ("Sandbox".equals(ou.name())) {
                                            sandboxOuArnRef.set(ou.arn());
                                            logger.info("Found Sandbox OU: {}", ou.id());
                                            break;
                                        }
                                    }
                                })
                                .thenApply(v -> {
                                    String sandboxArn = sandboxOuArnRef.get();
                                    if (sandboxArn == null) {
                                        logger.warn("Sandbox OU not found.");
                                    }
                                    return new OrgSetupResult(orgId, sandboxArn);
                                });
                    });
        }).exceptionally(ex -> {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            logger.error("Failed to setup organization: {}", cause.getMessage());
            throw new CompletionException(cause);
        });
    }



    // snippet-start:[controltower.java2.list_landing_zones.main]
    /**
     * Lists all landing zones using pagination to retrieve complete results.
     *
     * @return a list of all landing zones
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<List<LandingZoneSummary>> listLandingZonesAsync() {
        logger.info("Starting list landing zones paginator…");

        ListLandingZonesRequest request = ListLandingZonesRequest.builder().build();
        ListLandingZonesPublisher paginator = getAsyncClient().listLandingZonesPaginator(request);
        List<LandingZoneSummary> landingZones = new ArrayList<>();

        return paginator.subscribe(response -> {
                    if (response.landingZones() != null && !response.landingZones().isEmpty()) {
                        response.landingZones().forEach(lz -> {
                            logger.info("Landing zone ARN: {}", lz.arn());
                            landingZones.add(lz);
                        });
                    } else {
                        logger.info("Page contained no landing zones.");
                    }
                })
                .thenRun(() -> logger.info("Successfully retrieved {} landing zones.", landingZones.size()))
                .thenApply(v -> landingZones)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof ControlTowerException e) {
                        String errorCode = e.awsErrorDetails().errorCode();
                        switch (errorCode) {
                            case "AccessDeniedException":
                                throw new CompletionException(
                                        "Access denied when listing landing zones: " + e.getMessage(), e);
                            default:
                                throw new CompletionException(
                                        "Error listing landing zones: " + e.getMessage(), e);
                        }
                    }

                    if (cause instanceof SdkException) {
                        throw new CompletionException(
                                "SDK error listing landing zones: " + cause.getMessage(), cause);
                    }

                    throw new CompletionException("Failed to list landing zones", cause);
                });
    }
    // snippet-end:[controltower.java2.list_landing_zones.main]

    // snippet-start:[controltower.java2.list_baselines.main]
    /**
     * Lists all available baselines using pagination to retrieve complete results.
     *
     * @return a list of all baselines
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<List<BaselineSummary>> listBaselinesAsync() {
        logger.info("Starting list baselines paginator…");
        ListBaselinesRequest request = ListBaselinesRequest.builder().build();
        ListBaselinesPublisher paginator =
                getAsyncClient().listBaselinesPaginator(request);

        List<BaselineSummary> baselines = new ArrayList<>();
        return paginator.subscribe(response -> {
                    if (response.baselines() != null && !response.baselines().isEmpty()) {
                        response.baselines().forEach(baseline -> {
                            logger.info("Baseline: {}", baseline.name());
                            baselines.add(baseline);
                        });
                    } else {
                        logger.info("Page contained no baselines.");
                    }
                })
                .thenRun(() ->
                        logger.info("Successfully listed baselines. Total: {}", baselines.size())
                )
                .thenApply(v -> baselines)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof ControlTowerException e) {
                        String errorCode = e.awsErrorDetails().errorCode();

                        if ("AccessDeniedException".equals(errorCode)) {
                            throw new CompletionException(
                                    "Access denied when listing baselines: %s".formatted(e.getMessage()),
                                    e
                            );
                        }

                        throw new CompletionException(
                                "Error listing baselines: %s".formatted(e.getMessage()),
                                e
                        );
                    }

                    if (cause instanceof SdkException) {
                        throw new CompletionException(
                                "SDK error listing baselines: %s".formatted(cause.getMessage()),
                                cause
                        );
                    }

                    throw new CompletionException("Failed to list baselines", cause);
                });
    }
    // snippet-end:[controltower.java2.list_baselines.main]

    // snippet-start:[controltower.java2.list_enabled_baselines.main]
    /**
     * Lists all enabled baselines using pagination to retrieve complete results.
     *
     * @return a list of all enabled baselines
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<List<EnabledBaselineSummary>> listEnabledBaselinesAsync() {
        logger.info("Starting list enabled baselines paginator…");

        ListEnabledBaselinesRequest request =
                ListEnabledBaselinesRequest.builder().build();

        ListEnabledBaselinesPublisher paginator =
                getAsyncClient().listEnabledBaselinesPaginator(request);

        List<EnabledBaselineSummary> enabledBaselines = new ArrayList<>();
        return paginator.subscribe(response -> {
                    if (response.enabledBaselines() != null
                            && !response.enabledBaselines().isEmpty()) {

                        response.enabledBaselines().forEach(baseline -> {
                            logger.info("Enabled baseline: {}", baseline.baselineIdentifier());
                            enabledBaselines.add(baseline);
                        });
                    } else {
                        logger.info("Page contained no enabled baselines.");
                    }
                })
                .thenRun(() ->
                        logger.info(
                                "Successfully listed enabled baselines. Total: {}",
                                enabledBaselines.size()
                        )
                )
                .thenApply(v -> enabledBaselines)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof ControlTowerException e) {
                        String errorCode = e.awsErrorDetails().errorCode();

                        if ("ResourceNotFoundException".equals(errorCode)) {
                            throw new CompletionException(
                                    "Target not found when listing enabled baselines: %s"
                                            .formatted(e.getMessage()),
                                    e
                            );
                        }

                        throw new CompletionException(
                                "Error listing enabled baselines: %s"
                                        .formatted(e.getMessage()),
                                e
                        );
                    }

                    if (cause instanceof SdkException) {
                        throw new CompletionException(
                                "SDK error listing enabled baselines: %s"
                                        .formatted(cause.getMessage()),
                                cause
                        );
                    }

                    throw new CompletionException(
                            "Failed to list enabled baselines",
                            cause
                    );
                });
    }
    // snippet-end:[controltower.java2.list_enabled_baselines.main]

    // snippet-start:[controltower.java2.enable_baseline.main]
    /**
     * Enables a baseline for a specified target.
     *
     * @param baselineIdentifier the identifier of the baseline to enable
     * @param baselineVersion    the version of the baseline to enable
     * @param targetIdentifier   the identifier of the target (e.g., OU ARN)
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public static CompletableFuture<String> enableBaselineAsync(
            ControlTowerAsyncClient controlTowerAsyncClient,
            String baselineIdentifier,
            String baselineVersion,
            String targetIdentifier) {

        EnableBaselineRequest request = EnableBaselineRequest.builder()
                .baselineIdentifier(baselineIdentifier)
                .baselineVersion(baselineVersion)
                .targetIdentifier(targetIdentifier)
                .build();

        return getAsyncClient().enableBaseline(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null
                                ? exception.getCause()
                                : exception;

                        if (cause instanceof ControlTowerException e) {
                            String errorCode = e.awsErrorDetails().errorCode();

                            switch (errorCode) {
                                case "ValidationException":
                                    if (e.getMessage() != null
                                            && e.getMessage().contains("already enabled")) {
                                        throw new CompletionException(
                                                "Baseline is already enabled for this target",
                                                e
                                        );
                                    }
                                    throw new CompletionException(
                                            "Validation error enabling baseline: " + e.getMessage(),
                                            e
                                    );

                                case "ConflictException":
                                    throw new CompletionException(
                                            "Conflict enabling baseline: " + e.getMessage(),
                                            e
                                    );

                                default:
                                    throw new CompletionException(
                                            "Error enabling baseline: " + e.getMessage(),
                                            e
                                    );
                            }
                        }

                        if (cause instanceof SdkException) {
                            throw new CompletionException(
                                    "SDK error enabling baseline: " + cause.getMessage(),
                                    cause
                            );
                        }

                        throw new CompletionException(
                                "Unexpected error enabling baseline: " + exception.getMessage(),
                                exception
                        );
                    }
                })
                .thenApply(response -> {
                    String operationId = response.operationIdentifier();
                    return "Enabled baseline with operation ID: " + operationId;
                });
    }
    // snippet-end:[controltower.java2.enable_baseline.main]

    // snippet-start:[controltower.java2.disable_baseline.main]
    /**
     * Disables a baseline for a specified target.
     *
     * @param enabledBaselineIdentifier the identifier of the enabled baseline to disable
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<String> disableBaselineAsync(
            ControlTowerAsyncClient controlTowerAsyncClient,
            String enabledBaselineIdentifier) {

        DisableBaselineRequest request = DisableBaselineRequest.builder()
                .enabledBaselineIdentifier(enabledBaselineIdentifier)
                .build();

        return controlTowerAsyncClient.disableBaseline(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null
                                ? exception.getCause()
                                : exception;

                        if (cause instanceof ControlTowerException e) {
                            String errorCode = e.awsErrorDetails().errorCode();

                            switch (errorCode) {
                                case "ConflictException":
                                    throw new CompletionException(
                                            "Conflict disabling baseline: %s"
                                                    .formatted(e.getMessage()),
                                            e
                                    );

                                case "ResourceNotFoundException":
                                    throw new CompletionException(
                                            "Baseline not found for disabling: %s"
                                                    .formatted(e.getMessage()),
                                            e
                                    );

                                default:
                                    throw new CompletionException(
                                            "Error disabling baseline: %s"
                                                    .formatted(e.getMessage()),
                                            e
                                    );
                            }
                        }

                        if (cause instanceof SdkException) {
                            throw new CompletionException(
                                    "SDK error disabling baseline: %s"
                                            .formatted(cause.getMessage()),
                                    cause
                            );
                        }

                        throw new CompletionException(
                                "Failed to disable baseline",
                                cause
                        );
                    }
                })
                .thenApply(response -> {
                    String operationId = response.operationIdentifier();
                    return "Disabled baseline with operation ID: " + operationId;
                });
    }
    // snippet-end:[controltower.java2.disable_baseline.main]

    // snippet-start:[controltower.java2.get_baseline_operation.main]
    /**
     * Gets the status of a baseline operation.
     *
     * @param operationIdentifier the identifier of the operation
     * @return the operation status
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<BaselineOperationStatus> getBaselineOperationAsync(
            ControlTowerAsyncClient controlTowerAsyncClient,
            String operationIdentifier) {

        GetBaselineOperationRequest request = GetBaselineOperationRequest.builder()
                .operationIdentifier(operationIdentifier)
                .build();

        return controlTowerAsyncClient.getBaselineOperation(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null
                                ? exception.getCause()
                                : exception;

                        if (cause instanceof ControlTowerException e) {
                            String errorCode = e.awsErrorDetails().errorCode();

                            if ("ResourceNotFoundException".equals(errorCode)) {
                                throw new CompletionException(
                                        "Baseline operation not found: %s"
                                                .formatted(e.getMessage()),
                                        e
                                );
                            }

                            throw new CompletionException(
                                    "Error getting baseline operation status: %s"
                                            .formatted(e.getMessage()),
                                    e
                            );
                        }

                        if (cause instanceof SdkException) {
                            throw new CompletionException(
                                    "SDK error getting baseline operation status: %s"
                                            .formatted(cause.getMessage()),
                                    cause
                            );
                        }

                        throw new CompletionException(
                                "Failed to get baseline operation status",
                                cause
                        );
                    }
                })
                .thenApply(response -> {
                    BaselineOperationStatus status =
                            response.baselineOperation().status();
                    return status;
                });
    }
    // snippet-end:[controltower.java2.get_baseline_operation.main]

    // snippet-start:[controltower.java2.list_enabled_controls.main]
    /**
     * Lists all enabled controls for a specific target using pagination.
     *
     * @param targetIdentifier the identifier of the target (e.g., OU ARN)
     * @return a list of enabled controls
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<List<EnabledControlSummary>> listEnabledControlsAsync(String targetIdentifier) {
        logger.info("Starting list enabled controls paginator for target {}…", targetIdentifier);

        ListEnabledControlsRequest request = ListEnabledControlsRequest.builder()
                .targetIdentifier(targetIdentifier)
                .build();

        ListEnabledControlsPublisher paginator = getAsyncClient().listEnabledControlsPaginator(request);
        List<EnabledControlSummary> enabledControls = new ArrayList<>();

        // Subscribe to the paginator asynchronously
        return paginator.subscribe(response -> {
                    if (response.enabledControls() != null && !response.enabledControls().isEmpty()) {
                        response.enabledControls().forEach(control -> {
                            logger.info("Enabled control: {}", control.controlIdentifier());
                            enabledControls.add(control);
                        });
                    } else {
                        logger.info("Page contained no enabled controls.");
                    }
                })
                .thenRun(() -> logger.info(
                        "Successfully retrieved {} enabled controls for target {}",
                        enabledControls.size(),
                        targetIdentifier
                ))
                .thenApply(v -> enabledControls)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof ControlTowerException e) {
                        String errorCode = e.awsErrorDetails().errorCode();

                        switch (errorCode) {
                            case "AccessDeniedException":
                                throw new CompletionException(
                                        "Access denied when listing enabled controls: %s".formatted(e.getMessage()), e);

                            case "ResourceNotFoundException":
                                if (e.getMessage() != null && e.getMessage().contains("not registered with AWS Control Tower")) {
                                    throw new CompletionException(
                                            "Control Tower must be enabled to work with controls", e);
                                }
                                throw new CompletionException(
                                        "Target not found when listing enabled controls: %s".formatted(e.getMessage()), e);

                            default:
                                throw new CompletionException(
                                        "Error listing enabled controls: %s".formatted(e.getMessage()), e);
                        }
                    }

                    if (cause instanceof SdkException) {
                        throw new CompletionException(
                                "SDK error listing enabled controls: %s".formatted(cause.getMessage()), cause);
                    }

                    throw new CompletionException("Failed to list enabled controls", cause);
                });
    }
    // snippet-end:[controltower.java2.list_enabled_controls.main]

    // snippet-start:[controltower.java2.enable_control.main]
    /**
     * Enables a control for a specified target.
     *
     * @param controlIdentifier the identifier of the control to enable
     * @param targetIdentifier  the identifier of the target (e.g., OU ARN)
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<String> enableControlAsync(
            ControlTowerAsyncClient controlTowerAsyncClient,
            String controlIdentifier,
            String targetIdentifier) {

        EnableControlRequest request = EnableControlRequest.builder()
                .controlIdentifier(controlIdentifier)
                .targetIdentifier(targetIdentifier)
                .build();

        return getAsyncClient().enableControl(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null
                                ? exception.getCause()
                                : exception;

                        if (cause instanceof ControlTowerException e) {
                            String errorCode = e.awsErrorDetails().errorCode();

                            switch (errorCode) {
                                case "ValidationException":
                                    if (e.getMessage() != null
                                            && e.getMessage().contains("already enabled")) {
                                        // Preserve sync behavior: treat as no-op
                                        return;
                                    }

                                    throw new CompletionException(
                                            "Validation error enabling control: %s"
                                                    .formatted(e.getMessage()),
                                            e
                                    );

                                case "ResourceNotFoundException":
                                    if (e.getMessage() != null
                                            && e.getMessage().contains(
                                            "not registered with AWS Control Tower")) {
                                        throw new CompletionException(
                                                "Control Tower must be enabled to work with controls",
                                                e
                                        );
                                    }

                                    throw new CompletionException(
                                            "Control not found: %s"
                                                    .formatted(e.getMessage()),
                                            e
                                    );

                                default:
                                    throw new CompletionException(
                                            "Error enabling control: %s"
                                                    .formatted(e.getMessage()),
                                            e
                                    );
                            }
                        }

                        if (cause instanceof SdkException) {
                            throw new CompletionException(
                                    "SDK error enabling control: %s"
                                            .formatted(cause.getMessage()),
                                    cause
                            );
                        }

                        throw new CompletionException(
                                "Failed to enable control",
                                cause
                        );
                    }
                })
                .thenApply(response ->
                        response != null ? response.operationIdentifier() : null
                );
    }

    // snippet-end:[controltower.java2.enable_control.main]

    // snippet-start:[controltower.java2.disable_control.main]

    /**
     * Disables a control for a specified target.
     *
     * @param controlIdentifier the identifier of the control to disable
     * @param targetIdentifier  the identifier of the target (e.g., OU ARN)
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<String> disableControlAsync(
            ControlTowerAsyncClient controlTowerAsyncClient,
            String controlIdentifier,
            String targetIdentifier) {

        DisableControlRequest request = DisableControlRequest.builder()
                .controlIdentifier(controlIdentifier)
                .targetIdentifier(targetIdentifier)
                .build();

        return controlTowerAsyncClient.disableControl(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null
                                ? exception.getCause()
                                : exception;

                        if (cause instanceof ControlTowerException e) {
                            String errorCode = e.awsErrorDetails().errorCode();

                            if ("ResourceNotFoundException".equals(errorCode)) {
                                throw new CompletionException(
                                        "Control not found for disabling: %s"
                                                .formatted(e.getMessage()),
                                        e
                                );
                            }

                            throw new CompletionException(
                                    "Error disabling control: %s"
                                            .formatted(e.getMessage()),
                                    e
                            );
                        }

                        if (cause instanceof SdkException) {
                            throw new CompletionException(
                                    "SDK error disabling control: %s"
                                            .formatted(cause.getMessage()),
                                    cause
                            );
                        }

                        throw new CompletionException(
                                "Failed to disable control",
                                cause
                        );
                    }
                })
                .thenApply(response -> response.operationIdentifier());
    }
    // snippet-end:[controltower.java2.disable_control.main]

    // snippet-start:[controltower.java2.get_control_operation.main]

    /**
     * Gets the status of a control operation.
     *
     * @param operationIdentifier the identifier of the operation
     * @return the operation status
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<ControlOperationStatus> getControlOperationAsync(
            String operationIdentifier) {

        GetControlOperationRequest request = GetControlOperationRequest.builder()
                .operationIdentifier(operationIdentifier)
                .build();

        return getAsyncClient().getControlOperation(request)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        Throwable cause = exception.getCause() != null ? exception.getCause() : exception;

                        if (cause instanceof ControlTowerException e) {
                            String errorCode = e.awsErrorDetails().errorCode();

                            if ("ResourceNotFoundException".equals(errorCode)) {
                                throw new CompletionException(
                                        "Control operation not found: %s".formatted(e.getMessage()),
                                        e
                                );
                            }

                            throw new CompletionException(
                                    "Error getting control operation status: %s".formatted(e.getMessage()),
                                    e
                            );
                        }

                        if (cause instanceof SdkException) {
                            throw new CompletionException(
                                    "SDK error getting control operation status: %s".formatted(cause.getMessage()),
                                    cause
                            );
                        }

                        throw new CompletionException("Failed to get control operation status", cause);
                    }
                })
                .thenApply(response -> response.controlOperation().status());
    }
    // snippet-end:[controltower.java2.get_control_operation.main]

    // snippet-start:[controltower.java2.list_controls.main]

    /**
     * Lists all controls in the Control Tower control catalog.
     *
     * @return a list of controls
     * @throws SdkException if a service-specific error occurs
     */
    public CompletableFuture<List<ControlSummary>> listControlsAsync() {
        logger.info("Starting list controls paginator…");

        ListControlsRequest request = ListControlsRequest.builder().build();
        ListControlsPublisher paginator = getAsyncCatClient().listControlsPaginator(request);
        List<ControlSummary> controls = new ArrayList<>();

        return paginator.subscribe(response -> {
                    if (response.controls() != null && !response.controls().isEmpty()) {
                        response.controls().forEach(control -> {
                            logger.info("Control name: {}", control.name());
                            controls.add(control);
                        });
                    } else {
                        logger.info("Page contained no controls.");
                    }
                })
                .thenRun(() -> logger.info("Successfully retrieved {} controls.", controls.size()))
                .thenApply(v -> controls)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof SdkException sdkEx) {
                        if (sdkEx.getMessage() != null && sdkEx.getMessage().contains("AccessDeniedException")) {
                            throw new CompletionException(
                                    "Access denied when listing controls. Please ensure you have the necessary permissions.",
                                    sdkEx
                            );
                        } else {
                            throw new CompletionException(
                                    "SDK error listing controls: %s".formatted(sdkEx.getMessage()),
                                    sdkEx
                            );
                        }
                    }

                    throw new CompletionException("Failed to list controls", cause);
                });
    }
    // snippet-end:[controltower.java2.list_controls.main]

    // snippet-start:[controltower.java2.reset_enabled_baseline.main]

    /**
     * Resets an enabled baseline for a specific target.
     *
     * @param enabledBaselineIdentifier the identifier of the enabled baseline to reset
     * @return the operation identifier
     * @throws ControlTowerException if a service-specific error occurs
     * @throws SdkException          if an SDK error occurs
     */
    public CompletableFuture<String> resetEnabledBaselineAsync(
            String enabledBaselineIdentifier) {

        logger.info("Starting reset of enabled baseline…");
        ResetEnabledBaselineRequest request = ResetEnabledBaselineRequest.builder()
                .enabledBaselineIdentifier(enabledBaselineIdentifier)
                .build();

        return getAsyncClient().resetEnabledBaseline(request)
                .thenApply(response -> {
                    String operationId = response.operationIdentifier();
                    logger.info("Reset enabled baseline with operation ID: {}", operationId);
                    return operationId;
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;

                    if (cause instanceof ControlTowerException e) {
                        String errorCode = e.awsErrorDetails().errorCode();
                        switch (errorCode) {
                            case "ResourceNotFoundException":
                                logger.error("Target not found: {}", e.getMessage());
                                break;
                            default:
                                logger.error("Couldn't reset enabled baseline. Here's why: {}", e.getMessage());
                        }
                        throw new CompletionException(e);
                    }

                    if (cause instanceof SdkException sdkEx) {
                        logger.error("SDK error resetting enabled baseline: {}", sdkEx.getMessage());
                        throw new CompletionException(sdkEx);
                    }

                    throw new CompletionException("Failed to reset enabled baseline", cause);
                });
    }
    // snippet-end:[controltower.java2.reset_enabled_baseline.main]
}
// snippet-end:[controltower.java2.controltower_actions.main]