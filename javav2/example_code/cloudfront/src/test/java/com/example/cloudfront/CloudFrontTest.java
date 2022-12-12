package com.example.cloudfront;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudFrontTest {

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
                .credentialsProvider(ProfileCredentialsProvider.create())
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
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(cloudFrontClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateFunction() {
        functionName = "FunctionUploadedByJava" + UUID.randomUUID();
        funcARN =  CreateFunction.createNewFunction(cloudFrontClient, functionName, functionFileName);
        assertTrue(!funcARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeFunction() {
        eTagVal = DescribeFunction.describeFunction(cloudFrontClient, functionName);
        assertTrue(!eTagVal.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListFunctions(){
        ListFunctions.listAllFunctions(cloudFrontClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
   public void GetDistribution() {
        GetDistributions.getCFDistributions(cloudFrontClient);
        System.out.println("Test 5 passed");
   }


    @Test
    @Order(6)
   public void ModifyDistribution() {

        ModifyDistribution.modDistribution(cloudFrontClient, distributionId);
        System.out.println("Test 6 passed");
   }

    @Test
    @Order(7)
   public void DeleteFunction(){

       DeleteFunction.deleteSpecificFunction(cloudFrontClient, functionName, eTagVal);
       System.out.println("Test 7 passed");
    }
}

