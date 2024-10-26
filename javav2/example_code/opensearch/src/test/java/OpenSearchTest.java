// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.search.scenario.OpenSearchActions;
import com.example.search.scenario.OpenSearchScenario;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.example.search.*;
import software.amazon.awssdk.services.opensearch.model.AddTagsResponse;
import software.amazon.awssdk.services.opensearch.model.DeleteDomainResponse;
import software.amazon.awssdk.services.opensearch.model.DomainInfo;
import software.amazon.awssdk.services.opensearch.model.ListTagsResponse;
import software.amazon.awssdk.services.opensearch.model.UpdateDomainConfigResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenSearchTest {

    private static OpenSearchActions openSearchActions;
    private static String domainName = "";

    private static String arn;

    @BeforeAll
    public static void setUp() throws IOException {
         Random random = new Random();
        openSearchActions = new OpenSearchActions();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        domainName = "testdomain" + randomNum;
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void helloTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = HelloOpenSearch.listVersionsAsync();
            future.join();
        });
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void createDomain() {
        assertDoesNotThrow(() -> {
            CompletableFuture<String> future = openSearchActions.createNewDomainAsync(domainName);
            String domainId = future.join();
            System.out.println("Domain successfully created with ID: " + domainId);
        });
        System.out.println("Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void describeDomainTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<String> future = openSearchActions.describeDomainAsync(domainName);
            arn = future.join();
        });
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void listDomains() {
        assertDoesNotThrow(() -> {
            CompletableFuture<List<DomainInfo>> future = openSearchActions.listAllDomainsAsync();
            List<DomainInfo> domainInfoList = future.join();
            for (DomainInfo domain : domainInfoList) {
                System.out.println("Domain name is: " + domain.domainName());
            }
        });
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void domainChangeTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = openSearchActions.domainChangeProgressAsync(domainName);
            future.join();
            System.out.println("Domain change progress completed successfully.");
        });
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void domainModifyTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<UpdateDomainConfigResponse> future = openSearchActions.updateSpecificDomainAsync(domainName);
            UpdateDomainConfigResponse updateResponse = future.join();  // Wait for the task to complete
            System.out.println("Domain update response from Amazon OpenSearch Service: " + updateResponse.toString());
        });
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void domainChangeTest2() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = openSearchActions.domainChangeProgressAsync(domainName);
            future.join();
            System.out.println("Domain change progress completed successfully.");
        });
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void domainTagTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<AddTagsResponse> future = openSearchActions.addDomainTagsAsync(arn);
            future.join();
            System.out.println("Domain change progress completed successfully.");
        });
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void domainListTagsTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<ListTagsResponse> future = openSearchActions.listDomainTagsAsync(arn);
            future.join();
            System.out.println("Domain tags listed successfully.");
        });
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void domainDelTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<DeleteDomainResponse> future = openSearchActions.deleteSpecificDomainAsync(domainName);
            future.join();
            System.out.println(domainName + " was successfully deleted.");
        });
        System.out.println("Test 10 passed");
    }
}
