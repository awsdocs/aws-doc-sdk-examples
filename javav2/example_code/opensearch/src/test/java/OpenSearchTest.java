/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import java.io.*;
import java.util.*;
import com.example.search.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenSearchTest {

    private static OpenSearchClient searchClient;
    private static String domainName="";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        searchClient = OpenSearchClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();


        try (InputStream input = OpenSearchTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            domainName = prop.getProperty("domainName");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(searchClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createDomainTest() {
        CreateDomain. createNewDomain(searchClient, domainName);
        System.out.println("Test 2 passed");
    }


    @Test
    @Order(3)
    public void listDomainNamesTest() {
        ListDomainNames.listAllDomains(searchClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void UpdateDomain() {
        UpdateDomain.updateSpecificDomain(searchClient, domainName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DeleteDomain() {
        DeleteDomain.deleteSpecificDomain(searchClient, domainName);
        System.out.println("Test 5 passed");
    }
}
