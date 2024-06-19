// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ecr.HelloECR;
import com.example.ecr.scenario.ECRActions;
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
import software.amazon.awssdk.services.ecr.EcrClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import com.example.ecr.scenario.ECRScenario;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ECRTest {

    private static EcrClient ecrClient;

    private static String repoName = "";

    private static String newRepoName = "";
    private static String iamRole = "" ;

    private static ECRActions ecrActions;
    @BeforeAll
    public static void setUp() {
        ecrClient = EcrClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        ecrActions = new ECRActions();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        newRepoName = values.getRepoName();
        iamRole = values.getIamRole();
        repoName = values.getExistingRepo();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testScenario() {
        assertDoesNotThrow(() -> ecrActions.createECRRepository(newRepoName));
        assertDoesNotThrow(() -> ecrActions.setRepoPolicy(newRepoName, iamRole));
        assertDoesNotThrow(() -> ecrActions.getRepoPolicy(newRepoName));
        assertDoesNotThrow(() -> ecrActions.getAuthToken());
        assertDoesNotThrow(() -> ecrActions.getRepositoryURI(newRepoName));
        assertDoesNotThrow(() -> ecrActions.setLifeCyclePolicy(newRepoName));
        assertDoesNotThrow(() -> ecrActions.pushDockerImage(newRepoName, newRepoName));
        assertDoesNotThrow(() -> ecrActions.verifyImage(newRepoName, newRepoName));
        assertDoesNotThrow(() -> ecrActions.deleteECRRepository(newRepoName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testHello() {
        assertDoesNotThrow(() -> HelloECR.listImageTags(ecrClient, repoName));
        System.out.println("Test 2 passed");
    }


    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/ecr";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/ecr (an AWS Secrets Manager secret)")
    class SecretValues {
        private String repoName;
        private String iamRole;

        private String imageName;

        private String existingRepo;

        public String getExistingRepo() {
            return existingRepo;
        }

        public String getRepoName() {
            return repoName ;
        }

        public String getIamRole() {
            return iamRole;
        }

        public String getImageName() {
            return imageName
;       }
    }
}
