/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.mediaconvert.CreateJob;
import com.example.mediaconvert.GetEndpointURL;
import com.example.mediaconvert.GetJob;
import com.example.mediaconvert.ListJobs;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;

import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonMediaConvertTest {

    private static MediaConvertClient mc ;
    private static Region region ;
    private static String mcRoleARN = "";
    private static String fileInput = "";
    private static String jobId = "";

    @BeforeAll
    public static void setUp() throws IOException {

        region = Region.US_WEST_2;
        mc = MediaConvertClient.builder()
                .region(region)
                .build();

        try (InputStream input = AmazonMediaConvertTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            mcRoleARN = prop.getProperty("mcRoleARN");
            fileInput = prop.getProperty("fileInput");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingMediaConvertService_thenNotNull() {
        assertNotNull(mc);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateJob() {
        jobId = CreateJob.createMediaJob(mc, mcRoleARN, fileInput);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetEndpointURL() {
        GetEndpointURL.getEndpoint(mc);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListJobs() {

        ListJobs.listCompleteJobs(mc);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetJob() {

        GetJob.getSpecificJob(mc, jobId);
        System.out.println("Test 5 passed");
    }
}
