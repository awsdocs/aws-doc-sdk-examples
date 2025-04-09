// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


import com.example.acm.AddTagsToCertificate;
import com.example.acm.DeleteCert;
import com.example.acm.DescribeCert;
import com.example.acm.ImportCert;
import com.example.acm.ListCertTags;
import com.example.acm.RemoveTagsFromCert;
import com.example.acm.RequestCert;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ACMTests {
    private static final Logger logger = LoggerFactory.getLogger(ACMTests.class);
    private static String certificatePath = "";
    private static String privateKeyPath = "";
    private static String bucketName = "";
    private static String certificateArn;

    @BeforeAll
    public static void setUp() throws IOException {
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        certificatePath = values.getCertificatePath();
        privateKeyPath = values.getPrivateKeyPath();
        bucketName = values.getBucketName();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testImportCert() {
        assertDoesNotThrow(() -> {
            certificateArn = ImportCert.importCertificate(bucketName, certificatePath, privateKeyPath);
            assertNotNull(certificateArn);
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testAddTags() {
        assertDoesNotThrow(() -> {
            AddTagsToCertificate.addTags(certificateArn);
        });
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeCert() {
        assertDoesNotThrow(() -> {
            DescribeCert.describeCertificate(certificateArn);
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testRemoveTagsFromCert() {
        assertDoesNotThrow(() -> {
            RemoveTagsFromCert.removeTags(certificateArn);
        });
        logger.info("Test 4 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testRequestCert() {
        assertDoesNotThrow(() -> {
            RequestCert.requestCertificate();
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDeleteCert() {
        assertDoesNotThrow(() -> {
            DeleteCert.deleteCertificate(certificateArn);
        });
        logger.info("Test 6 passed");
    }


    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/acm";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cognito (an AWS Secrets Manager secret)")
    class SecretValues {
        private String certificatePath;
        private String privateKeyPath;
        private String bucketName;

        // Getter for certificatePath
        public String getCertificatePath() {
            return certificatePath;
        }

        // Getter for privateKeyPath
        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        // Getter for bucketName
        public String getBucketName() {
            return bucketName;
        }
    }
}
