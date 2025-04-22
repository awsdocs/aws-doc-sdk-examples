// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.route.CreateHealthCheck;
import com.example.route.CreateHostedZone;
import com.example.route.DeleteHealthCheck;
import com.example.route.DeleteHostedZone;
import com.example.route.GetHealthCheckStatus;
import com.example.route.ListHealthChecks;
import com.example.route.ListHostedZones;
import com.example.route.UpdateHealthCheck;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.route53.Route53Client;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53domains.Route53DomainsClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Route53Test {
    private static final Logger logger = LoggerFactory.getLogger(Route53Test.class);
    private static String domainName = "";
    private static String healthCheckId = "";
    private static String hostedZoneId = "";
    private static Route53Client route53Client;
    private static Route53DomainsClient route53DomainsClient;
    private static String domainSuggestionSc = "";
    private static String domainTypeSc = "";
    private static String phoneNumerSc = "";
    private static String emailSc = "";
    private static String firstNameSc = "";
    private static String lastNameSc = "";
    private static String citySc = "";

    @BeforeAll
    public static void setUp() {
        route53Client = Route53Client.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        route53DomainsClient = Route53DomainsClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        domainName = values.getDomainName();
        domainSuggestionSc = values.getDomainSuggestionSc();
        domainTypeSc = values.getDomainTypeSc();
        phoneNumerSc = values.getPhoneNumerSc();
        emailSc = values.getEmailSc();
        firstNameSc = values.getFirstNameSc();
        lastNameSc = values.getLastNameSc();
        citySc = values.getCitySc();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateHealthCheck() {
        healthCheckId = CreateHealthCheck.createCheck(route53Client, domainName);
        assertFalse(healthCheckId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateHostedZone() {
        hostedZoneId = CreateHostedZone.createZone(route53Client, domainName);
        assertFalse(hostedZoneId.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testGetHealthCheckStatus() {
        try {
            TimeUnit.SECONDS.sleep(20); // wait for the new health check
            assertDoesNotThrow(() -> GetHealthCheckStatus.getHealthStatus(route53Client, healthCheckId));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListHealthChecks() {
        assertDoesNotThrow(() -> ListHealthChecks.listAllHealthChecks(route53Client));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testUpdateHealthCheck() {
        assertDoesNotThrow(() -> UpdateHealthCheck.updateSpecificHealthCheck(route53Client, healthCheckId));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListHostedZones() {
        assertDoesNotThrow(() -> ListHostedZones.listZones(route53Client));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testDeleteHealthCheck() {
        assertDoesNotThrow(() -> DeleteHealthCheck.delHealthCheck(route53Client, healthCheckId));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDeleteHostedZone() {
        assertDoesNotThrow(() -> DeleteHostedZone.delHostedZone(route53Client, hostedZoneId));
        logger.info("Test 8 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/route53";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/route53 (an AWS Secrets Manager secret)")
    class SecretValues {
        private String domainName;
        private String domainSuggestionSc;
        private String domainTypeSc;

        private String phoneNumerSc;

        private String emailSc;

        private String firstNameSc;

        private String lastNameSc;

        private String citySc;

        public String getDomainName() {
            return domainName;
        }

        public String getDomainSuggestionSc() {
            return domainSuggestionSc;
        }

        public String getDomainTypeSc() {
            return domainTypeSc;
        }

        public String getPhoneNumerSc() {
            return phoneNumerSc;
        }

        public String getEmailSc() {
            return emailSc;
        }

        public String getFirstNameSc() {
            return firstNameSc;
        }

        public String getLastNameSc() {
            return lastNameSc;
        }

        public String getCitySc() {
            return citySc;
        }
    }
}
