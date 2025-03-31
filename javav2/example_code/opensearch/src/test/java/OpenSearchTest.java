// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.search.scenario.OpenSearchActions;
import com.example.search.scenario.OpenSearchScenario;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchTest.class);

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
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void createDomain() {
        assertDoesNotThrow(() -> {
            CompletableFuture<String> future = openSearchActions.createNewDomainAsync(domainName);
            String domainId = future.join();
            logger.info("Domain successfully created with ID: " + domainId);
        });
        logger.info("Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void describeDomainTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<String> future = openSearchActions.describeDomainAsync(domainName);
            arn = future.join();
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void listDomains() {
        assertDoesNotThrow(() -> {
            CompletableFuture<List<DomainInfo>> future = openSearchActions.listAllDomainsAsync();
            List<DomainInfo> domainInfoList = future.join();
            for (DomainInfo domain : domainInfoList) {
                logger.info("Domain name is: " + domain.domainName());
            }
        });
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void domainTagTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<AddTagsResponse> future = openSearchActions.addDomainTagsAsync(arn);
            future.join();
            logger.info("Domain change progress completed successfully.");
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void domainListTagsTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<ListTagsResponse> future = openSearchActions.listDomainTagsAsync(arn);
            future.join();
            logger.info("Domain tags listed successfully.");
        });
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void domainDelTest() {
        assertDoesNotThrow(() -> {
            CompletableFuture<DeleteDomainResponse> future = openSearchActions.deleteSpecificDomainAsync(domainName);
            future.join();
            logger.info(domainName + " was successfully deleted.");
        });
        logger.info("Test 7 passed");
    }
}
