/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.ssm.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import static org.junit.jupiter.api.Assertions.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSSMTest {
    private static SsmClient ssmClient;
    private static String paraName="";
    private static String title="";
    private static String source="";
    private static String category="";
    private static String severity="";
    private static String opsItemId ="";

    @BeforeAll
    public static void setUp(){
        Region region = Region.US_EAST_1;
        ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        paraName = values.getParaName();
        title = values.getTitle();
        source = values.getSource();
        category = values.getCategory();
        severity = values.getSeverity();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AWSSSMTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            paraName = prop.getProperty("paraName");
            title = prop.getProperty("title");
            source = prop.getProperty("source");
            category = prop.getProperty("category");
            severity = prop.getProperty("severity");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateOpsItem(){
        opsItemId = CreateOpsItem.createNewOpsItem(ssmClient, title, source, category, severity) ;
        assertFalse(opsItemId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void GetOpsItem(){
        assertDoesNotThrow(() ->GetOpsItem.getOpsItem(ssmClient, opsItemId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeOpsItems() {
        assertDoesNotThrow(() ->DescribeOpsItems.describeItems(ssmClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DescribeParameters() {
        assertDoesNotThrow(() ->DescribeParameters.describeParams(ssmClient));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void GetParameter() {
        assertDoesNotThrow(() ->GetParameter.getParaValue(ssmClient, paraName));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ResolveOpsItem() {
        assertDoesNotThrow(() ->ResolveOpsItem.setOpsItemStatus(ssmClient, opsItemId));
        System.out.println("Test 6 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/ssm";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/ssm (an AWS Secrets Manager secret)")
    class SecretValues {
        private String paraName;
        private String source;
        private String category;

        private String severity;

        private String title;

        public String getParaName() {
            return paraName;
        }

        public String getSource() {
            return source;
        }

        public String getCategory() {
            return category;
        }

        public String getSeverity() {
            return severity;
        }

        public String getTitle() {
            return title;
        }
    }
}
