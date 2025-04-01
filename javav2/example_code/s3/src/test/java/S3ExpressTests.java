// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.s3.express.S3DirectoriesActions;
import com.example.s3.express.S3DirectoriesScenario;
import com.example.s3.express.UserNames;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.example.s3.express.S3DirectoriesScenario.createS3ClientWithAccessKeyAsync;
import static com.example.s3.express.S3DirectoriesScenario.createUsersUsingCDK;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3ExpressTests {

    private static S3AsyncClient mS3RegularClient;
    private static S3AsyncClient mS3ExpressClient;

    private static String regularUser = "";

    private static String expressUser = "";

    private static String regularBucketName = "";
    private static String directoryBucketName = "";

    private static String bucketObject = "basic-text-object.txt";
    private static final S3DirectoriesActions s3DirectoriesActions = new S3DirectoriesActions();

    private static String stackName = "cfn-stack-s3-express-basics--" + UUID.randomUUID();

    private static final Logger logger = LoggerFactory.getLogger(S3ExpressTests.class);

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testSetUp() throws IOException {
        assertDoesNotThrow(() -> {
            // Retrieve user names from CDK stack outputs
            Map<String, String> stackOutputs = createUsersUsingCDK(stackName);
            regularUser = stackOutputs.get("RegularUser");
            expressUser = stackOutputs.get("ExpressUser");

            assertNotNull(regularUser, "Regular user should not be null");
            assertNotNull(expressUser, "Express user should not be null");

            // Store the user names in a UserNames object
            UserNames names = new UserNames();
            names.setRegularUserName(regularUser);
            names.setExpressUserName(expressUser);

            // Create access keys for both users asynchronously
            CreateAccessKeyResponse keyResponseRegular = s3DirectoriesActions.createAccessKeyAsync(regularUser).join();
            CreateAccessKeyResponse keyResponseExpress = s3DirectoriesActions.createAccessKeyAsync(expressUser).join();

            assertNotNull(keyResponseRegular.accessKey(), "Access key for Regular User should not be null");
            assertNotNull(keyResponseExpress.accessKey(), "Access key for Express User should not be null");

            // Extract access keys
            String accessKeyIdForRegUser = keyResponseRegular.accessKey().accessKeyId();
            String secretAccessForRegUser = keyResponseRegular.accessKey().secretAccessKey();

            String accessKeyIdForExpressUser = keyResponseExpress.accessKey().accessKeyId();
            String secretAccessForExpressUser = keyResponseExpress.accessKey().secretAccessKey();

            // Ensure keys are valid
            assertNotNull(accessKeyIdForRegUser, "Access Key ID for Regular User should not be null");
            assertNotNull(secretAccessForRegUser, "Secret Access Key for Regular User should not be null");
            assertNotNull(accessKeyIdForExpressUser, "Access Key ID for Express User should not be null");
            assertNotNull(secretAccessForExpressUser, "Secret Access Key for Express User should not be null");

            // Create S3 clients asynchronously
            mS3RegularClient = createS3ClientWithAccessKeyAsync(accessKeyIdForRegUser, secretAccessForRegUser).join();
            mS3ExpressClient = createS3ClientWithAccessKeyAsync(accessKeyIdForExpressUser, secretAccessForExpressUser).join();

            assertNotNull(mS3RegularClient, "S3 client for Regular User should not be null");
            assertNotNull(mS3ExpressClient, "S3 client for Express User should not be null");
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void createBuckets() throws InterruptedException {
        Thread.sleep(30000);
        assertDoesNotThrow(() -> {
            String zoneId = "usw2-az1";

            // Generate bucket names
            regularBucketName = "reg-bucket-" + System.currentTimeMillis();
            directoryBucketName = "test-bucket-" + System.currentTimeMillis() + "--" + zoneId + "--x-s3";

            // Validate bucket names
            assertNotNull(regularBucketName, "Regular bucket name should not be null");
            assertNotNull(directoryBucketName, "Directory bucket name should not be null");

            // Create the regular bucket asynchronously
            CompletableFuture<WaiterResponse<HeadBucketResponse>> regularBucketFuture = s3DirectoriesActions.createBucketAsync(mS3RegularClient, regularBucketName);

            // Create the directory bucket asynchronously
            CompletableFuture<CreateBucketResponse> directoryBucketFuture = s3DirectoriesActions.createDirectoryBucketAsync(mS3ExpressClient, directoryBucketName, zoneId);

            // Wait for both operations to complete
            CompletableFuture.allOf(regularBucketFuture, directoryBucketFuture).join();

        });
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void createSessionAddObjectTest() {
        assertDoesNotThrow(() -> {
            s3DirectoriesActions.putObjectAsync(mS3RegularClient, regularBucketName, bucketObject, "Look Ma, I'm a bucket!").join();
            s3DirectoriesActions.createSessionAsync(mS3ExpressClient, directoryBucketName).join();
            s3DirectoriesActions.copyObjectAsync(mS3ExpressClient, regularBucketName, bucketObject, directoryBucketName, bucketObject).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void demonstratePerformance() {
        assertDoesNotThrow(() -> {
            int downloads = 300;
            long directoryTimeStart = System.nanoTime();
            for (int index = 0; index < downloads; index++) {
                if (index % 50 == 0) {
                    System.out.println("Download " + index + " of " + downloads);
                }


                // Get the object from the directory bucket.
                s3DirectoriesActions.getObjectAsync(mS3ExpressClient, directoryBucketName, bucketObject).join();
            }

            long directoryTimeDifference = System.nanoTime() - directoryTimeStart;

            // Download from the regular bucket.
            System.out.println("Downloading from the regular bucket.");
            long normalTimeStart = System.nanoTime();
            for (int index = 0; index < downloads; index++) {
                if (index % 50 == 0) {
                    System.out.println("Download " + index + " of " + downloads);
                }

                s3DirectoriesActions.getObjectAsync(mS3RegularClient, regularBucketName, bucketObject).join();

            }

            long normalTimeDifference = System.nanoTime() - normalTimeStart;
            System.out.println("The directory bucket took " + directoryTimeDifference + " nanoseconds, while the regular bucket took " + normalTimeDifference + " nanoseconds.");
            long difference = normalTimeDifference - directoryTimeDifference;
            System.out.println("That's a difference of " + difference + " nanoseconds, or");
            System.out.println(difference / 1_000_000_000.0 + " seconds.");

            if (difference < 0) {
                System.out.println("The directory buckets were slower. This can happen if you are not running on the cloud within a VPC.");
            }
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testCleanup() {
        assertDoesNotThrow(() -> {
            s3DirectoriesActions.deleteBucketAndObjectsAsync(mS3ExpressClient, directoryBucketName).join();
            s3DirectoriesActions.deleteBucketAndObjectsAsync(mS3RegularClient, regularBucketName).join();
            S3DirectoriesScenario.cleanUp(stackName);
        });
    }
}

