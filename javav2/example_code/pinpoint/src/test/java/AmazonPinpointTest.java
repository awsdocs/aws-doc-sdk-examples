// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
import java.io.*;
import com.example.pinpoint.*;
import software.amazon.awssdk.services.pinpointemail.PinpointEmailClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.pinpointsmsvoice.PinpointSmsVoiceClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonPinpointTest {
    private static PinpointClient pinpoint;
    private static final Logger logger = LoggerFactory.getLogger(AmazonPinpointTest.class);
    private static PinpointEmailClient pinpointEmailClient;
    private static PinpointSmsVoiceClient voiceClient;
    private static S3Client s3Client;
    private static String appName = "";
    private static String appId = "";
    private static String endpointId2 = "";
    private static String bucket = "";
    private static String path = "";
    private static String roleArn = "";
    private static String segmentId = "";
    private static String userId = "";
    private static String s3BucketName = "";
    private static String iamExportRoleArn = "";
    private static String existingApplicationId = "";
    private static String filePath = "";
    private static String subject = "";
    private static String senderAddress = "";
    private static String toAddress = "";
    private static String originationNumber = "";
    private static String destinationNumber = "";
    private static String destinationNumber1 = "";
    private static String message = "";

    @BeforeAll
    public static void setUp() throws IOException {
        pinpoint = PinpointClient.builder()
            .region(Region.US_EAST_1)
            .build();

        pinpointEmailClient = PinpointEmailClient.builder()
            .region(Region.US_EAST_1)
            .build();

        s3Client = S3Client.builder()
            .region(Region.US_EAST_1)
            .build();

        // Set the content type to application/json.
        List<String> listVal = new ArrayList<>();
        listVal.add("application/json");
        Map<String, List<String>> values = new HashMap<>();
        values.put("Content-Type", listVal);

        ClientOverrideConfiguration config2 = ClientOverrideConfiguration.builder()
                .headers(values)
                .build();

        voiceClient = PinpointSmsVoiceClient.builder()
                .overrideConfiguration(config2)
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues valuesOb = gson.fromJson(json, SecretValues.class);
        appName = valuesOb.getAppName();
        bucket = valuesOb.getBucket();
        path = valuesOb.getPath();
        roleArn = valuesOb.getRoleArn();
        userId = valuesOb.getUserId();
        s3BucketName = valuesOb.getS3BucketName();
        iamExportRoleArn = valuesOb.getIamExportRoleArn();
        existingApplicationId = valuesOb.getExistingApplicationId();
        subject = valuesOb.getSubject();
        senderAddress = valuesOb.getSenderAddress();
        toAddress = valuesOb.getToAddress();
        originationNumber = valuesOb.getOriginationNumber();
        destinationNumber = valuesOb.getDestinationNumber();
        destinationNumber1 = valuesOb.getDestinationNumber1();
        message = valuesOb.getMessage();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateApp() {
        appId = CreateApp.createApplication(pinpoint, appName);
        assertFalse(appId.isEmpty());
        logger.info("testCreateApp test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testUpdateEndpoint() {
        EndpointResponse response = UpdateEndpoint.createEndpoint(pinpoint, appId);
        endpointId2 = response.id();
        assertFalse(endpointId2.isEmpty());
        logger.info("UpdateEndpoint test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testLookUpEndpoint() {
        assertDoesNotThrow(() -> LookUpEndpoint.lookupPinpointEndpoint(pinpoint, appId, endpointId2));
        logger.info("LookUpEndpoint test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testAddExampleUser() {
        assertDoesNotThrow(() -> AddExampleUser.updatePinpointEndpoint(pinpoint, appId, endpointId2));
        logger.info("AddExampleUser test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testAddExampleEndpoints() {
        assertDoesNotThrow(() -> AddExampleEndpoints.updateEndpointsViaBatch(pinpoint, appId));
        logger.info("AddExampleEndpoints test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testDeleteEndpoint() {
        assertDoesNotThrow(() -> DeleteEndpoint.deletePinEncpoint(pinpoint, appId, endpointId2));
        logger.info("DeleteEndpoint test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testSendMessage() {
        assertDoesNotThrow(() -> SendMessage.sendSMSMessage(pinpoint, message, existingApplicationId, originationNumber,
                destinationNumber));
        logger.info("SendMessage test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testImportSegments() {
        assertDoesNotThrow(() -> SendMessageBatch.sendSMSMessage(pinpoint, message, "2fdc4442c6a2483f85eaf7a943054815",
                originationNumber, destinationNumber, destinationNumber));
        logger.info("ImportSegments test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testListSegments() {
        assertDoesNotThrow(() -> ListSegments.listSegs(pinpoint, appId));
        logger.info("ListSegments test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testCreateSegment() {
        SegmentResponse createSegmentResult = CreateSegment.createSegment(pinpoint, existingApplicationId);
        segmentId = createSegmentResult.id();
        assertFalse(segmentId.isEmpty());
        logger.info("CreateSegment test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testCreateCampaign() {
        assertDoesNotThrow(() -> CreateCampaign.createPinCampaign(pinpoint, existingApplicationId, segmentId));
        logger.info("CreateCampaign test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void testSendEmailMessage() {
        assertDoesNotThrow(() -> SendEmailMessage.sendEmail(pinpointEmailClient, subject,  senderAddress, toAddress));
        logger.info("SendEmailMessage test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void testSendVoiceMessage() {
        assertDoesNotThrow(() -> SendVoiceMessage.sendVoiceMsg(voiceClient, originationNumber, destinationNumber));
        logger.info("SendVoiceMessage test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void testListEndpointIds() {
        assertDoesNotThrow(() -> ListEndpointIds.listAllEndpoints(pinpoint, existingApplicationId, userId));
        logger.info("ListEndpointIds test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(17)
    public void testDeleteApp() {
        assertDoesNotThrow(() -> DeleteApp.deletePinApp(pinpoint, appId));
        logger.info("DeleteApp test passed");
    }

    public static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .build();
        String secretName = "test/pinpoint";
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/pinpoint (an AWS Secrets Manager secret)")
    class SecretValues {
        private String appName;
        private String bucket;
        private String path;

        private String roleArn;

        private String existingApplicationId;

        private String userId;

        private String s3BucketName;

        private String iamExportRoleArn;

        private String subject;

        private String senderAddress;

        private String toAddress;

        private String originationNumber;

        private String destinationNumber;

        private String destinationNumber1;

        private String message;

        public String getAppName() {
            return appName;
        }

        public String getBucket() {
            return bucket;
        }

        public String getPath() {
            return path;
        }

        public String getRoleArn() {
            return roleArn;
        }

        public String getExistingApplicationId() {
            return existingApplicationId;
        }

        public String getUserId() {
            return userId;
        }

        public String getS3BucketName() {
            return s3BucketName;
        }

        public String getIamExportRoleArn() {
            return iamExportRoleArn;
        }

        public String getSubject() {
            return subject;
        }

        public String getSenderAddress() {
            return senderAddress;
        }

        public String getToAddress() {
            return toAddress;
        }

        public String getOriginationNumber() {
            return originationNumber;
        }

        public String getDestinationNumber() {
            return destinationNumber;
        }

        public String getDestinationNumber1() {
            return destinationNumber1;
        }

        public String getMessage() {
            return message;
        }

    }
}
