// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.iot.HelloIoT;
import com.example.iot.scenario.IotActions;
import com.example.iot.scenario.IotScenario;
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
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IoTTests {

    private static IotClient iotClient;
    private static String thingName = "foo" ;
    private static String queryString = "thingName:" ;

    private static String ruleName = "rule";

    private static String roleARN = "" ;

    private static String snsAction = "" ;

    @BeforeAll
    public static void setUp() throws IOException {
        iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        Random random = new Random();
        int randomNumber = random.nextInt(1001);
        ruleName = ruleName + randomNumber;
        thingName = thingName + randomNumber;
        queryString = queryString+thingName;


        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        roleARN = values.getRoleARN();
        snsAction = values.getSnsAction();
        // Uncomment this code block if you prefer using a config.properties file to
        // retrieve AWS values required for these tests.

        /*
        try (InputStream input = IoTTests.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            thingName = prop.getProperty("thingName");
            roleARN = prop.getProperty("roleARN");
            ruleName = prop.getProperty("ruleName");
            snsAction = prop.getProperty("snsAction");
            queryString = "thingName:"+thingName+"";

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHello() {
        assertDoesNotThrow(() -> HelloIoT.listAllThings(iotClient),
            "Failed to list your things.");
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testIotScenario() throws InterruptedException {
        IotActions iotActions = new IotActions();

        assertDoesNotThrow(() -> iotActions.createIoTThing(thingName),
            "Failed to create your thing in the scenario.");

        String certArn = assertDoesNotThrow(() -> iotActions.createCertificate(),
            "Failed to create a cert in the scenario.");

        assertDoesNotThrow(() ->  iotActions.attachCertificateToThing(thingName, certArn),
            "Failed to attach a cert in the scenario.");

        assertDoesNotThrow(() -> iotActions.updateShadowThing(thingName),
            "Failed to update the thing in the scenario.");

        String endpointUrl = String.valueOf(assertDoesNotThrow(() -> iotActions.describeEndpoint(),
            "Failed to update the thing in the scenario."));

        assertDoesNotThrow(() ->  iotActions.listCertificates(),
            "Failed to update the thing in the scenario.");

        assertDoesNotThrow(() ->  iotActions.updateShadowThing(thingName),
            "Failed to update shawdow in the scenario.");

        assertDoesNotThrow(() ->  iotActions.getPayload(thingName),
            "Failed to get payload in the scenario.");

        assertDoesNotThrow(() ->  iotActions.createIoTRule(roleARN, ruleName, snsAction),
            "Failed to get payload in the scenario.");

        assertDoesNotThrow(() ->  iotActions.listIoTRules(),
            "Failed to list rules in the scenario.");

        System.out.println("Wait 5 secs");
        Thread.sleep(5000);

        assertDoesNotThrow(() -> iotActions.searchThings(queryString),
            "Failed to search things in the scenario.");

        assertDoesNotThrow(() ->  iotActions.detachThingPrincipal(thingName, certArn),
            "Failed to detach cert in the scenario.");

        assertDoesNotThrow(() ->  iotActions.deleteCertificate(certArn),
            "Failed to delete cert in the scenario.");

        assertDoesNotThrow(() ->  iotActions.deleteIoTThing(thingName),
            "Failed to delete your thing in the scenario.");

        System.out.println("Scenario test passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/iot";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/firehose (an AWS Secrets Manager secret)")
    class SecretValues {
        private String roleARN;
        private String snsAction;

        public String getSnsAction() {
            return snsAction;
        }

        public String getRoleARN() {
            return roleARN;
        }
    }
}
