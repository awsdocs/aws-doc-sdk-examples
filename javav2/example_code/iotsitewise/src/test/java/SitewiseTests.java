// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.iotsitewise.HelloSitewise;
import com.example.iotsitewise.scenario.CloudFormationHelper;
import com.example.iotsitewise.scenario.SitewiseActions;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetModelResponse;
import software.amazon.awssdk.services.iotsitewise.model.CreateAssetResponse;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SitewiseTests {

    private static final String assetModelName = "MyAssetModel" + UUID.randomUUID();
    private static final String assetName = "MyAsset";

    private static String assetId = "";
    private static final String portalName = "MyPortal";
    private static String contactEmail = "";
    private static final String gatewayName = "myGateway" + UUID.randomUUID();
    private static final String myThing = "myThing" + UUID.randomUUID();

    private static String assetModelId = "";

    private static String iamRole = "";

    private static final SitewiseActions sitewiseActions = new SitewiseActions();

    private static Map<String, String> propertyIds;

    private static String humPropId = "";

    private static String tempPropId = "";

    private static String portalId = "";

    private static String gatewayId = "";
    private static final String ROLES_STACK = "RoleSitewise";

    @BeforeAll
    public static void setUp() {
        CloudFormationHelper.deployCloudFormationStack(ROLES_STACK);
        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputsAsync(ROLES_STACK).join();
        iamRole = stackOutputs.get("SitewiseRoleArn");

         /*
         The following values used in these integration tests are retrieved from AWS Secrets Manager.
         */
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        contactEmail = values.getContactEmail();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloService() {
        assertDoesNotThrow(HelloSitewise::fetchAssetModels);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateAssetModel() {
        assertDoesNotThrow(() -> {
            CompletableFuture<CreateAssetModelResponse> future = sitewiseActions.createAssetModelAsync(assetModelName);
            CreateAssetModelResponse response = future.join();

            if (response == null || response.assetModelId() == null) {
                throw new RuntimeException("Simulating failure: response or assetModelId is null");
            }
            assetModelId = response.assetModelId();
            assertNotNull(assetModelId);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateAsset() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            CompletableFuture<CreateAssetResponse> future = sitewiseActions.createAssetAsync(assetName, assetModelId);
            CreateAssetResponse response = future.join();
            assetId = response.assetId();
            assertNotNull(assetId);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetPropIds() {
        assertDoesNotThrow(() -> {
            propertyIds = sitewiseActions.getPropertyIds(assetModelId).join();
            humPropId = propertyIds.get("Humidity");
            System.out.println("The Humidity property Id is " + humPropId);
            tempPropId = propertyIds.get("Temperature");
            System.out.println("The Temperature property Id is " + tempPropId);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testSendProps() {
        assertDoesNotThrow(() -> {
            sitewiseActions.sendDataToSiteWiseAsync(assetId, tempPropId, humPropId).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testGETHumValue() {
        assertDoesNotThrow(() -> {
            sitewiseActions.getAssetPropValueAsync(humPropId, assetId);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testCreatePortal() {
        assertDoesNotThrow(() -> {
            portalId = sitewiseActions.createPortalAsync(portalName, iamRole, contactEmail).join();
            assertNotNull(portalId);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDescribePortal() {
        assertDoesNotThrow(() -> {
            String portalUrl = sitewiseActions.describePortalAsync(portalId).join();
            assertNotNull(portalUrl);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testCreateGateway() {
        assertDoesNotThrow(() -> {
            gatewayId = sitewiseActions.createGatewayAsync(gatewayName, myThing).join();
            assertNotNull(gatewayId);
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDescribeGateway() {
        assertDoesNotThrow(() -> {
            sitewiseActions.describeGatewayAsync(gatewayId).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testDeletePortal() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            sitewiseActions.deletePortalAsync(portalId).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testDeleteAsset() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            sitewiseActions.deleteAssetAsync(assetId).join();
        });
    }
    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testDeleteAssetModel() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            sitewiseActions.deleteAssetModelAsync(assetModelId).join();
        });
        CloudFormationHelper.destroyCloudFormationStack(ROLES_STACK);
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/sitewise";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/sitewise (an AWS Secrets Manager secret)")
    class SecretValues {
        private String contactEmail;

        private String assetModelHello;

        public String getContactEmail() {
            return this.contactEmail;
        }

        public String getAssetModelHello() {
            return this.assetModelHello;
        }
    }
}

