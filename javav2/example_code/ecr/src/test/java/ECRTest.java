// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ecr.HelloECR;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
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

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ECRTest {

    private static EcrClient ecrClient;
    private static String repoName = "test61";

    private static String newRepoName = "test101";
    private String iamRole = "arn:aws:iam::814548047983:role/Admin" ;
    private String localImageName = "hello-world-docker:latest";
    private String imageTag = "latest" ;
    @BeforeAll
    public static void setUp() {
        ecrClient = EcrClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testScenario() {
      //  assertDoesNotThrow(() -> ECRScenario.createECRRepository(ecrClient, newRepoName));
      //  assertDoesNotThrow(() -> ECRScenario.setRepoPolicy(ecrClient, newRepoName, iamRole));
      //  assertDoesNotThrow(() -> ECRScenario.getRepoPolicy(ecrClient, newRepoName));
      //  assertDoesNotThrow(() -> ECRScenario.getAuthToken(ecrClient));
      //  assertDoesNotThrow(() -> ECRScenario.getRepositoryURI(ecrClient, newRepoName));
      //  assertDoesNotThrow(() -> ECRScenario.setLifeCyclePolicy(ecrClient, newRepoName));
      //  assertDoesNotThrow(() -> ECRScenario.pushDockerImage(ecrClient,newRepoName, localImageName));
      //  assertDoesNotThrow(() -> ECRScenario.verifyImage(ecrClient, newRepoName, imageTag));
      //  assertDoesNotThrow(() -> ECRScenario.deleteECRRepository(ecrClient, newRepoName));
      //  System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testHello() {
        assertDoesNotThrow(() -> HelloECR.listImageTags(ecrClient, repoName));
        System.out.println("Test 2 passed");
    }
}
