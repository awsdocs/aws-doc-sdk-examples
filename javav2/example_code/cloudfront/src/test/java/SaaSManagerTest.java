// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.cloudfront.CreateMultiTenantDistribution;
import com.example.cloudfront.CreateDistributionTenant;
import com.example.cloudfront.DeleteDistribution;
import com.example.cloudfront.DeleteDistributionTenant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaaSManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(SaaSManagerTest.class);
    private static CloudFrontClient cloudFrontClient;
    private static S3Client s3Client;
    private static Route53Client route53Client;
    private static Region region;
    private static String s3BucketName = "";
    private static String certificateArn = "";
    private static String distributionNoCertId = "";
    private static String distributionWithCertId = "";
    private static String distributionTenantWithCertId = "";
    private static String distributionTenantNoCertId = "";
    private static String distributionTenantSelfHostedId = "";
    private static String distributionTenantCfHostedId = "";
    private static String baseDomain = "";
    private static String hostedZoneId = "";

    @BeforeAll
    public static void setUp() {

        // Run tests on Real AWS resources.
        region = Region.AWS_GLOBAL;
        cloudFrontClient = CloudFrontClient.builder()
                .region(region)
                .build();
        s3Client = S3Client.builder()
                .region(Region.US_WEST_2)
                .build();
        route53Client = Route53Client.builder()
                .region(region)
                .build();

        try (InputStream input = SaaSManagerTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file from the classpath, inside static method.
            prop.load(input);

            // Populate the data members required for all tests.
            certificateArn = prop.getProperty("wildcardCertificateArn");
            s3BucketName = prop.getProperty("s3BucketName");
            baseDomain = prop.getProperty("baseDomain");
            hostedZoneId = prop.getProperty("hostedZoneId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @AfterAll
    static void tearDownScaffolding() {
        tearDownCloudFrontResources();
    }

    static void tearDownCloudFrontResources() {
        DeleteDistributionTenant.deleteDistributionTenant(cloudFrontClient, distributionTenantNoCertId);
        DeleteDistributionTenant.deleteDistributionTenant(cloudFrontClient, distributionTenantWithCertId);
        DeleteDistributionTenant.deleteDistributionTenant(cloudFrontClient, distributionTenantCfHostedId);
        DeleteDistributionTenant.deleteDistributionTenant(cloudFrontClient, distributionTenantSelfHostedId);
        DeleteDistribution.deleteDistribution(cloudFrontClient, distributionNoCertId);
        DeleteDistribution.deleteDistribution(cloudFrontClient, distributionWithCertId);
    }


    @Test
    @Order(1)
    public void CreateMultiTenantDistributionWithCert() {
        distributionWithCertId = CreateMultiTenantDistribution.CreateMultiTenantDistributionWithCert(
                cloudFrontClient,
                s3Client,
                s3BucketName,
                certificateArn).id();
        assertFalse(distributionWithCertId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateMultiTenantDistributionNoCert() {
        distributionNoCertId = CreateMultiTenantDistribution.CreateMultiTenantDistributionNoCert(
                cloudFrontClient,
                s3Client,
                s3BucketName).id();
        assertFalse(distributionNoCertId.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateDistributionTenantWithCert() {
        distributionTenantWithCertId = CreateDistributionTenant.createDistributionTenantWithCert(
                cloudFrontClient,
                route53Client,
                distributionNoCertId,
                "my-tenant-cert." + baseDomain,
                hostedZoneId,
                certificateArn
                ).id();
        assertFalse(distributionTenantWithCertId.isEmpty());
        logger.info("Test 3 passed");
    }

    @Test
    @Order(4)
    public void CreateDistributionTenantNoCert() {
        distributionTenantNoCertId = CreateDistributionTenant.createDistributionTenantNoCert(
                cloudFrontClient,
                route53Client,
                distributionWithCertId,
                "my-tenant-no-cert." + baseDomain,
                hostedZoneId
        ).id();
        assertFalse(distributionTenantNoCertId.isEmpty());
        logger.info("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CreateDistributionTenantCfHosted() throws InterruptedException {
        distributionTenantCfHostedId = CreateDistributionTenant.createDistributionTenantCfHosted(
                cloudFrontClient,
                route53Client,
                distributionNoCertId,
                "my-tenant-cf-hosted." + baseDomain,
                hostedZoneId
        ).id();
        assertFalse(distributionTenantCfHostedId.isEmpty());
        logger.info("Test 5 passed");
    }

    @Test
    @Order(6)
    public void CreateDistributionTenantSelfHosted() {
        distributionTenantSelfHostedId = CreateDistributionTenant.createDistributionTenantSelfHosted(
                cloudFrontClient,
                distributionNoCertId,
                "my-tenant-self-hosted." + baseDomain
        ).id();
        assertFalse(distributionTenantSelfHostedId.isEmpty());
        logger.info("Test 6 passed");
    }
}

