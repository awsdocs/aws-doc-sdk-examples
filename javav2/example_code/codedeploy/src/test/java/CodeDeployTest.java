/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.deploy.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CodeDeployTest {
    private static CodeDeployClient deployClient;
    private static String appName = "";
    private static String existingApp = "";
    private static String existingDeployment = "";
    private static String bucketName = "";
    private static String key = "";
    private static String bundleType = "";
    private static String newDeploymentGroupName = "";
    private static String deploymentId="" ;
    private static String serviceRoleArn="" ;
    private static String tagKey="";
    private static String tagValue="";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        deployClient = CodeDeployClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        appName = values.getAppName();
        existingApp = values.getExistingApp();
        existingDeployment = values.getExistingDeployment();
        bucketName = values.getBucketName();
        key = values.getKey();
        bundleType = values.getBundleType();
        newDeploymentGroupName  = values.getNewDeploymentGroupName();
        serviceRoleArn = values.getServiceRoleArn();
        tagKey = values.getTagKey();
        tagValue = values.getTagValue();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = CodeDeployTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            appName = prop.getProperty("appName");
            existingApp = prop.getProperty("existingApp");
            existingDeployment = prop.getProperty("existingDeployment");
            bucketName = prop.getProperty("bucketName");
            key = prop.getProperty("key");
            bundleType = prop.getProperty("bundleType");
            newDeploymentGroupName  = prop.getProperty("newDeploymentGroupName");
            serviceRoleArn = prop.getProperty("serviceRoleArn");
            tagKey = prop.getProperty("tagKey");
            tagValue = prop.getProperty("tagValue");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateApplication() {
        assertDoesNotThrow(() ->CreateApplication.createApp(deployClient, appName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void ListApplications() {
        assertDoesNotThrow(() ->ListApplications.listApps(deployClient));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DeployApplication() {
        deploymentId = DeployApplication.createAppDeployment(deployClient, existingApp, bucketName, bundleType, key, existingDeployment);
        assertFalse(deploymentId.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void CreateDeploymentGroup() {
        assertDoesNotThrow(() ->CreateDeploymentGroup.createNewDeploymentGroup(deployClient, newDeploymentGroupName, appName, serviceRoleArn, tagKey, tagValue));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListDeploymentGroups() {
        assertDoesNotThrow(() ->ListDeploymentGroups.listDeployGroups(deployClient, appName));
        System.out.println("Test 5 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
   public void GetDeployment(){
       assertDoesNotThrow(() ->GetDeployment.getSpecificDeployment(deployClient,deploymentId));
       System.out.println("Test 6 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
   public void DeleteDeploymentGroup() {
       assertDoesNotThrow(() ->DeleteDeploymentGroup.delDeploymentGroup(deployClient, appName, newDeploymentGroupName));
       System.out.println("Test 7 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
   public void DeleteApplication() {
       assertDoesNotThrow(() ->DeleteApplication.delApplication(deployClient, appName));
       System.out.println("Test 8 passed");
   }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/codedeploy";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/codedeploy (an AWS Secrets Manager secret)")
    class SecretValues {
        private String appName;
        private String existingApp;
        private String newDeploymentGroupName;

        private String existingDeployment;

        private String bucketName;

        private String key;

        private String bundleType;

        private String serviceRoleArn;

        private String tagKey;

        private String tagValue;

        public String getAppName() {
            return appName;
        }

        public String getExistingApp() {
            return existingApp;
        }

        public String getNewDeploymentGroupName() {
            return newDeploymentGroupName;
        }

        public String getExistingDeployment() {
            return existingDeployment;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getKey() {
            return key;
        }

        public String getBundleType() {
            return bundleType;
        }

        public String getServiceRoleArn() {
            return serviceRoleArn;
        }

        public String getTagKey() {
            return tagKey;
        }

        public String getTagValue() {
            return tagValue;
        }
    }
}