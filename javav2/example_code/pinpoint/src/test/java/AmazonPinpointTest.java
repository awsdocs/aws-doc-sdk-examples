/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
import java.io.*;
import com.example.pinpoint.*;
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
    private static PinpointSmsVoiceClient voiceClient;
    private static S3Client s3Client;
    private static String appName = "";
    private static String appId = "";
    private static String endpointId2 = "";
    private static String bucket = "";
    private static String path= "";
    private static String roleArn= "";
    private static String segmentId= "";
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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        s3Client = S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues valuesOb = gson.fromJson(json, SecretValues.class);
        appName = valuesOb.getAppName();
        bucket= valuesOb.getBucket();
        path = valuesOb.getPath();
        roleArn= valuesOb.getRoleArn();
        userId = valuesOb.getUserId();
        s3BucketName = valuesOb.getS3BucketName();
        iamExportRoleArn = valuesOb.getIamExportRoleArn();
        existingApplicationId= valuesOb.getExistingApplicationId();
        subject = valuesOb.getSubject();
        senderAddress = valuesOb.getSenderAddress();
        toAddress = valuesOb.getToAddress();
        originationNumber= valuesOb.getOriginationNumber();
        destinationNumber= valuesOb.getDestinationNumber();
        destinationNumber1 = valuesOb.getDestinationNumber1();
        message= valuesOb.getMessage();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = AmazonPinpointTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
             if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests
            prop.load(input);
            appName = prop.getProperty("appName");
            bucket= prop.getProperty("bucket");
            path= prop.getProperty("path");
            roleArn= prop.getProperty("roleArn");
            userId = prop.getProperty("userId");
            s3BucketName = prop.getProperty("s3BucketName");
            s3BucketName = prop.getProperty("s3BucketName");
            iamExportRoleArn = prop.getProperty("iamExportRoleArn");
            existingApplicationId= prop.getProperty("existingApplicationId");
            subject = prop.getProperty("subject");
            senderAddress = prop.getProperty("senderAddress");
            toAddress = prop.getProperty("toAddress");
            originationNumber= prop.getProperty("originationNumber");
            destinationNumber= prop.getProperty("destinationNumber");
            destinationNumber1 = prop.getProperty("destinationNumber1");
            message= prop.getProperty("message");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateApp() {
        appId = CreateApp.createApplication(pinpoint, appName);
        assertFalse(appId.isEmpty());
        System.out.println("CreateApp test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void UpdateEndpoint() {
        EndpointResponse response = UpdateEndpoint.createEndpoint(pinpoint, appId);
        endpointId2 = response.id() ;
        assertFalse(endpointId2.isEmpty());
        System.out.println("UpdateEndpoint test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void LookUpEndpoint() {
        assertDoesNotThrow(() ->LookUpEndpoint.lookupPinpointEndpoint(pinpoint, appId, endpointId2));
        System.out.println("LookUpEndpoint test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void AddExampleUser() {
        assertDoesNotThrow(() ->AddExampleUser.updatePinpointEndpoint(pinpoint,appId,endpointId2));
        System.out.println("AddExampleUser test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void AddExampleEndpoints() {
        assertDoesNotThrow(() -> AddExampleEndpoints.updateEndpointsViaBatch(pinpoint,appId));
        System.out.println("AddExampleEndpoints test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteEndpoint() {
        assertDoesNotThrow(() ->DeleteEndpoint.deletePinEncpoint(pinpoint, appId, endpointId2));
        System.out.println("DeleteEndpoint test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void SendMessage() {
       assertDoesNotThrow(() ->SendMessage.sendSMSMessage(pinpoint, message, existingApplicationId, originationNumber, destinationNumber));
       System.out.println("SendMessage test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
   public void ImportSegments() {
        assertDoesNotThrow(() -> SendMessageBatch.sendSMSMessage(pinpoint, message, "2fdc4442c6a2483f85eaf7a943054815", originationNumber, destinationNumber, destinationNumber));
        System.out.println("ImportSegments test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void ListSegments() {
        assertDoesNotThrow(() -> ListSegments.listSegs(pinpoint, appId));
        System.out.println("ListSegments test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void CreateSegment() {
        SegmentResponse createSegmentResult =  CreateSegment.createSegment(pinpoint, existingApplicationId);
        segmentId =  createSegmentResult.id();
        assertFalse(segmentId.isEmpty());
        System.out.println("CreateSegment test passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void CreateCampaign() {
       assertDoesNotThrow(() -> CreateCampaign.createPinCampaign(pinpoint, existingApplicationId, segmentId));
       System.out.println("CreateCampaign test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void ExportEndpoints() {
        assertDoesNotThrow(() ->  ExportEndpoints.exportAllEndpoints(pinpoint, s3Client, existingApplicationId, s3BucketName, filePath, iamExportRoleArn));
        System.out.println("ExportEndpoints test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void SendEmailMessage() {
        assertDoesNotThrow(() -> SendEmailMessage.sendEmail(pinpoint, subject, existingApplicationId,  senderAddress, toAddress));
        System.out.println("SendEmailMessage test passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
   public void SendVoiceMessage() {
        assertDoesNotThrow(() ->SendVoiceMessage.sendVoiceMsg(voiceClient, originationNumber, destinationNumber));
        System.out.println("SendVoiceMessage test passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
   public void ListEndpointIds() {
        assertDoesNotThrow(() ->ListEndpointIds.listAllEndpoints(pinpoint, existingApplicationId, userId));
        System.out.println("ListEndpointIds test passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(17)
    public void DeleteApp() {
       assertDoesNotThrow(() ->DeleteApp.deletePinApp(pinpoint, appId));
       System.out.println("DeleteApp test passed");
    }
    public static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

