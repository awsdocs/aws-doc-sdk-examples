/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


import com.example.sts.AssumeRole;
import com.example.sts.GetAccessKeyInfo;
import com.example.sts.GetCallerIdentity;
import com.example.sts.GetSessionToken;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class STSServiceTest {

    private static StsClient stsClient;
    private static String roleArn = "";
    private static String accessKeyId = "";
    private static String roleSessionName = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        stsClient = StsClient.builder()
                .region(region)
                .build();

        try (InputStream input = STSServiceTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            roleArn = prop.getProperty("roleArn");
            accessKeyId = prop.getProperty("accessKeyId");
            roleSessionName = prop.getProperty("roleSessionName");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(stsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void AssumeRole() {

        AssumeRole.assumeGivenRole(stsClient, roleArn, roleSessionName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetSessionToken() {
        GetSessionToken.getToken(stsClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetCallerIdentity() {
        GetCallerIdentity.getCallerId(stsClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetAccessKeyInfo() {
        GetAccessKeyInfo.getKeyInfo(stsClient, accessKeyId);
        System.out.println("Test 5 passed");
    }

}
