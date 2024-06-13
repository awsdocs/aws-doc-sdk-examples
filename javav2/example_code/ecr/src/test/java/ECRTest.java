// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ecr.HelloECR;
import com.example.ecr.scenario.ECRActions;
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

    private static String newRepoName = "test1081";
    private String iamRole = "arn:aws:iam::814548047983:role/Admin" ;
    private String localImageName = "hello-world";
    private String imageTag = "latest" ;

    private static ECRActions ecrActions;
    @BeforeAll
    public static void setUp() {
        ecrClient = EcrClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        ecrActions = new ECRActions();


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
        assertDoesNotThrow(() -> ecrActions.pushDockerImage(newRepoName, localImageName));
        assertDoesNotThrow(() -> ecrActions.verifyImage(newRepoName, localImageName));
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
}
