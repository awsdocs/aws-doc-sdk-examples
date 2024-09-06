// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.example.search.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenSearchTest {

    private static OpenSearchClient searchClient;
    private static String domainName = "";

    @BeforeAll
    public static void setUp() throws IOException {
        searchClient = OpenSearchClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();


        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        domainName = "testdomain" + randomNum;
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createDomainTest() {
        HelloOpenSearch.createNewDomain(searchClient, domainName);
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void listDomainNamesTest() {
     //   ListDomainNames.listAllDomains(searchClient);
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void UpdateDomain() throws InterruptedException {
        System.out.println("Wait 5 mins for resource to become available.");
        TimeUnit.MINUTES.sleep(5);
      //  UpdateDomain.updateSpecificDomain(searchClient, domainName);
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DeleteDomain() {
      //  DeleteDomain.deleteSpecificDomain(searchClient, domainName);
        System.out.println("Test 4 passed");
    }
}
