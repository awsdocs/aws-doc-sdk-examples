// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.cloudfront.CreateFunction;
import com.example.cloudfront.DeleteFunction;
import com.example.cloudfront.DescribeFunction;
import com.example.cloudfront.GetDistributions;
import com.example.cloudfront.ListFunctions;
import com.example.cloudfront.ModifyDistribution;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudFrontTest {
    private static final Logger logger = LoggerFactory.getLogger(CloudFrontTest.class);
    private static CloudFrontClient cloudFrontClient ;
    private static Region region;
    private static String functionName = "";
    private static String functionFileName = "CF_function.js";
    private static String funcARN = "";
    private static String eTagVal = "";
    private static String distributionId = "";


    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS resources.
        region = Region.AWS_GLOBAL;
        cloudFrontClient = CloudFrontClient.builder()
                .region(region)
                .build();
        try (InputStream input = CloudFrontTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file from the classpath, inside static method.
            prop.load(input);

            // Populate the data members required for all tests.
            distributionId = prop.getProperty("distributionId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    @Order(1)
    public void CreateFunction() {
        functionName = "FunctionUploadedByJava" + UUID.randomUUID();
        funcARN =  CreateFunction.createNewFunction(cloudFrontClient, functionName, functionFileName);
        assertTrue(!funcARN.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Order(2)
    public void DescribeFunction() {
        eTagVal = DescribeFunction.describeFunction(cloudFrontClient, functionName);
        assertTrue(!eTagVal.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Order(3)
    public void testListFunctions(){
        assertDoesNotThrow(() -> {
            ListFunctions.listAllFunctions(cloudFrontClient);
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Order(4)
   public void testGetDistribution() {
       assertDoesNotThrow(() -> {
            GetDistributions.getCFDistributions(cloudFrontClient);
        });
        logger.info("Test 4 passed");
   }

    @Test
    @Order(5)
   public void testDeleteFunction(){
       assertDoesNotThrow(() -> {
           DeleteFunction.deleteSpecificFunction(cloudFrontClient, functionName, eTagVal);
       });
       logger.info("Test 5 passed");
    }
}

