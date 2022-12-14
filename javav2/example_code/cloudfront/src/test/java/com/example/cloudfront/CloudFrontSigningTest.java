package com.example.cloudfront;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCannedPolicy;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;
import software.amazon.awssdk.services.cloudfront.model.*;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudFrontSigningTest {
    private static Logger logger = LoggerFactory.getLogger(CloudFrontSigningTest.class);

    private static CloudFrontClient cloudFrontClient;
    private static S3Client s3Client;
    private static String bucketName;
    private static String fileNameToUpload;
    private static String distributionId;
    private static String originAccessControlId;
    private static String distributionDomainName;
    private static String publicKeyFileName;
    private static String publicKeyId;
    private static String keyGroupId;
    private static String bucketAccessPolicy;
    private static ApacheHttpClient apacheHttpClient = (ApacheHttpClient) ApacheHttpClient.create();
    private static String privateKeyFullPath;

    @BeforeAll
    static void setUp() throws IOException {

        // Run tests on Real AWS resources.
        cloudFrontClient = CloudFrontClient.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = CloudFrontSigningTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file from the classpath, inside static method.
            prop.load(input);

            // Populate the data members required for all tests.
            bucketName = ("CF-test-bucket" + UUID.randomUUID()).toLowerCase();
            fileNameToUpload = "index.html";
            publicKeyFileName = prop.getProperty("publicKeyFileName");
            privateKeyFullPath = prop.getProperty("privateKeyFullPath");

            // Set up resources needed for testing.
            setUpS3Resources();
            setUpCloudFrontResources();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @AfterAll
    static void tearDownScaffolding() {
        tearDownS3Resources();
        tearDownCloudFrontResources();
    }

    static void setUpS3Resources() {
        try {
            S3TestUtils.createBucket(s3Client, bucketName);
            S3TestUtils.lockdownBucket(s3Client, bucketName);
            S3TestUtils.uploadFileToBucket(s3Client, bucketName, fileNameToUpload);
        } catch (Throwable e) {
            logger.error("Exception: " + e);
        }
    }

    static void setUpCloudFrontResources() {
        publicKeyId = CreatePublicKey.createPublicKey(cloudFrontClient, publicKeyFileName);
        keyGroupId = CreateKeyGroup.createKeyGroup(cloudFrontClient, publicKeyId);
        originAccessControlId = CreateOriginAccessControl.createOriginAccessControl(cloudFrontClient);

        Distribution distribution = CreateDistribution.createDistribution(cloudFrontClient, s3Client, bucketName, keyGroupId, originAccessControlId);
        distributionId = distribution.id();
        distributionDomainName = distribution.domainName();

        bucketAccessPolicy = CreateBucketPolicy.createBucketPolicy(bucketName, S3TestUtils.getAccountId(), distributionId);
        CreateBucketPolicy.uploadBucketPolicy(s3Client, bucketName, bucketAccessPolicy);
    }

    static void tearDownS3Resources() {
        S3TestUtils.deleteObjectFromBucket(s3Client, bucketName, fileNameToUpload);
        S3TestUtils.deleteBucket(s3Client, bucketName);
    }

   static void tearDownCloudFrontResources() {
        DeleteDistribution.deleteDistribution(cloudFrontClient, distributionId);
        DeleteSigningResources.deleteOriginAccessControl(cloudFrontClient, originAccessControlId);
        DeleteSigningResources.deleteKeyGroup(cloudFrontClient, keyGroupId);
        DeleteSigningResources.deletePublicKey(cloudFrontClient, publicKeyId);
    }

    @Test
    @Order(1)
    void whenInitializingAWSService_thenNotNull() {
        assertNotNull(cloudFrontClient);
        assertNotNull(s3Client);
        logger.info("Test 1 passed");
    }

    @Test
    @Order(2)
    void callFailsWithoutSignedUrlTest() {
        SdkHttpRequest request = SdkHttpRequest.builder()
                .encodedPath("/" + fileNameToUpload)
                .host(distributionDomainName)
                .method(SdkHttpMethod.GET)
                .protocol("https")
                .build();

        ExecutableHttpRequest executableHttpRequest = apacheHttpClient.prepareRequest(HttpExecuteRequest.builder().request(request).build());
        try {
            HttpExecuteResponse httpExecuteResponse = executableHttpRequest.call();
            assertEquals(HttpStatusCode.FORBIDDEN, httpExecuteResponse.httpResponse().statusCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Test 2 passed");
    }

    @Test
    @Order(3)
    void signedUrlWithCannedPolicyWorksTest() throws Exception{
        CannedSignerRequest cannedRequest = CreateCannedPolicyRequest
                .createRequestForCannedPolicy(distributionDomainName, fileNameToUpload, privateKeyFullPath, publicKeyId);
        SignedUrl signedUrlWithCannedPolicy = SigningUtilities.signUrlForCannedPolicy(cannedRequest);
        SdkHttpRequest httpGetRequest = signedUrlWithCannedPolicy.createHttpGetRequest();
        assertEquals(HttpStatusCode.OK, useSignedResource(httpGetRequest));
        logger.info("Test 3 passed");
    }

    @Test
    @Order(4)
    void signedUrlWithCustomPolicy_forbidden_whenActiveDateNotReachedTest() throws Exception {

        CustomSignerRequest customRequest = CreateCustomPolicyRequest
                .createRequestForCustomPolicy(distributionDomainName, fileNameToUpload, privateKeyFullPath, publicKeyId);
        SignedUrl signedUrlWithCustomPolicy = SigningUtilities.signUrlForCustomPolicy(customRequest);
        SdkHttpRequest httpGetRequest = signedUrlWithCustomPolicy.createHttpGetRequest();
        assertEquals(HttpStatusCode.FORBIDDEN, useSignedResource(httpGetRequest));
        logger.info("Test 4 passed");
    }

    @Test
    @Order(5)
    void signedUrlWithCustomPolicy_ok_whenActiveDateReachedTest() throws Exception {
        CustomSignerRequest customRequest = CreateCustomPolicyRequest
                .createRequestForCustomPolicy(distributionDomainName, fileNameToUpload, privateKeyFullPath, publicKeyId);
        // Set a new activeDate to make it active.
        customRequest = customRequest.toBuilder()
                .activeDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        SignedUrl signedUrlWithCustomPolicy = SigningUtilities.signUrlForCustomPolicy(customRequest);
        SdkHttpRequest httpGetRequest = signedUrlWithCustomPolicy.createHttpGetRequest();
        assertEquals(HttpStatusCode.OK, useSignedResource(httpGetRequest));
        logger.info("Test 5 passed");
    }

    @Test
    @Order(6)
    void signedCookiesWithCannedPolicyWorksTest() throws Exception {
        CannedSignerRequest cannedRequest = CreateCannedPolicyRequest
                .createRequestForCannedPolicy(distributionDomainName, fileNameToUpload, privateKeyFullPath, publicKeyId);
        CookiesForCannedPolicy cookiesForCannedPolicy = SigningUtilities.getCookiesForCannedPolicy(cannedRequest);
        SdkHttpRequest httpGetRequest = cookiesForCannedPolicy.createHttpGetRequest();
        assertEquals(HttpStatusCode.OK, useSignedResource(httpGetRequest));
        logger.info("Test 6 passed");
    }

    @Test
    @Order(7)
    void signedCookiesWithCustomPolicy_forbidden_whenActiveDateNotReachedTest() throws Exception {
        CustomSignerRequest customRequest = CreateCustomPolicyRequest
                .createRequestForCustomPolicy(distributionDomainName, fileNameToUpload, privateKeyFullPath, publicKeyId);
        CookiesForCustomPolicy cookiesForCustomPolicy = SigningUtilities.getCookiesForCustomPolicy(customRequest);
        SdkHttpRequest httpGetRequest = cookiesForCustomPolicy.createHttpGetRequest();
        assertEquals(HttpStatusCode.FORBIDDEN, useSignedResource(httpGetRequest));
        logger.info("Test 7 passed");
    }

    @Test
    @Order(8)
    void signedCookiesWithCustomPolicy_ok_whenActiveDateReachedTest() throws Exception {
        CustomSignerRequest customRequest = CreateCustomPolicyRequest
                .createRequestForCustomPolicy(distributionDomainName, fileNameToUpload, privateKeyFullPath, publicKeyId);
        // Set a new activeDate to make it active.
        customRequest = customRequest.toBuilder()
                .activeDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();
        CookiesForCustomPolicy cookiesForCustomPolicy = SigningUtilities.getCookiesForCustomPolicy(customRequest);
        SdkHttpRequest httpGetRequest = cookiesForCustomPolicy.createHttpGetRequest();
        assertEquals(HttpStatusCode.OK, useSignedResource(httpGetRequest));
        logger.info("Test 8 passed");
    }

    private int useSignedResource(SdkHttpRequest httpGetRequest){
        HttpExecuteResponse response = null;
        try {
            response = apacheHttpClient
                    .prepareRequest(HttpExecuteRequest.builder()
                            .request(httpGetRequest)
                            .build())
                    .call();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.httpResponse().statusCode();
    }
}

