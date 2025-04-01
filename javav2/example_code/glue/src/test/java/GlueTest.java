// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.glue.scenario.GlueScenario;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.glue.GlueClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GlueTest {
    private static final Logger logger = LoggerFactory.getLogger(GlueTest.class);
    private static GlueClient glueClient;
    private static String crawlerName = "";
    private static String cron = "";
    private static String s3Path = "";
    private static String IAM = "";
    private static String databaseName = "";
    private static String tableName = "";
    private static String text = "";
    private static String existingDatabaseName = "";
    private static String existingCrawlerName = "";
    private static String jobNameSc = "";
    private static String s3PathSc = "";
    private static String dbNameSc = "";
    private static String crawlerNameSc = "";
    private static String scriptLocationSc = "";
    private static String locationUri = "";
    private static String bucketNameSc = "";

    @BeforeAll
    public static void setUp() {
        glueClient = GlueClient.builder()
            .region(Region.US_EAST_1)
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        crawlerName = values.getCrawlerName();
        s3Path = values.getS3Path();
        cron = values.getCron();
        IAM = values.getIAM();
        databaseName = values.getDatabaseName();
        tableName = values.getTableName();
        text = values.getText();
        existingDatabaseName = values.getExistingDatabaseName();
        existingCrawlerName = values.getExistingCrawlerName();
        jobNameSc = values.getJobNameSc() + java.util.UUID.randomUUID();
        s3PathSc = values.getS3PathSc() + java.util.UUID.randomUUID();
        dbNameSc = values.getDbNameSc() + java.util.UUID.randomUUID();
        crawlerNameSc = values.getCrawlerNameSc() + java.util.UUID.randomUUID();
        scriptLocationSc = values.getScriptLocationSc();
        locationUri = values.getLocationUri();
        bucketNameSc = values.getBucketNameSc();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    void testCreateDatabase() {
        assertDoesNotThrow(() -> {
            GlueScenario.createDatabase(glueClient, dbNameSc, locationUri);
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    void testCreateGlueCrawler() {
        assertDoesNotThrow(() -> {
            GlueScenario.createGlueCrawler(glueClient, IAM, s3PathSc, cron, dbNameSc, crawlerNameSc);
        });
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    void testGetSpecificCrawler() {
        assertDoesNotThrow(() -> {
            GlueScenario.getSpecificCrawler(glueClient, crawlerNameSc);
        });
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    void testStartSpecificCrawler() {
        assertDoesNotThrow(() -> {
            GlueScenario.startSpecificCrawler(glueClient, crawlerNameSc);
        });
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    void testGetSpecificDatabase() {
        assertDoesNotThrow(() -> {
            GlueScenario.getSpecificDatabase(glueClient, dbNameSc);
        });
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    void testGetTable() {
        assertDoesNotThrow(() -> {
            System.out.println("*** Wait 5 min for the tables to become available");
            TimeUnit.MINUTES.sleep(5);
            System.out.println("6. Get tables.");
            GlueScenario.getGlueTables(glueClient, dbNameSc);
        });
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    void testCreateJob() {
        assertDoesNotThrow(() -> {
            GlueScenario.createJob(glueClient, jobNameSc, IAM, scriptLocationSc);
        });
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    void testStartJob() {
        assertDoesNotThrow(() -> {
            GlueScenario.startJob(glueClient, jobNameSc, dbNameSc, tableName, bucketNameSc);
        });
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    void testGetJobs() {
        assertDoesNotThrow(() -> {
            GlueScenario.getAllJobs(glueClient);
        });
        logger.info("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    void testRunJobs() {
        assertDoesNotThrow(() -> {
            GlueScenario.getJobRuns(glueClient, jobNameSc);
        });
        logger.info("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    void testDeleteJob() {
        assertDoesNotThrow(() -> {
            GlueScenario.deleteJob(glueClient, jobNameSc);
        });
        logger.info("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    void testDeleteDB() {
        assertDoesNotThrow(() -> {
            System.out.println("*** Wait 5 MIN for the " + crawlerNameSc + " to stop");
            TimeUnit.MINUTES.sleep(5);
            GlueScenario.deleteDatabase(glueClient, dbNameSc);
        });
        logger.info("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    void testDelCrawler() {
        assertDoesNotThrow(() -> {
            System.out.println("*** Wait 5 MIN for the " + crawlerNameSc + " to stop");
            TimeUnit.MINUTES.sleep(5);
            GlueScenario.deleteSpecificCrawler(glueClient, crawlerNameSc);
        });
        logger.info("Test 13 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .build();
        String secretName = "test/glue";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/glue (an AWS Secrets Manager secret)")
    class SecretValues {
        private String IAM;
        private String s3Path;
        private String cron;

        private String crawlerName;

        private String existingCrawlerName;

        private String databaseName;

        private String existingDatabaseName;

        private String tableName;

        private String text;
        private String jobNameSc;

        private String dbNameSc;

        private String crawlerNameSc;

        private String s3PathSc;

        private String scriptLocationSc;
        private String locationUri;

        private String bucketNameSc;

        public String getIAM() {
            return IAM;
        }

        public String getS3Path() {
            return s3Path;
        }

        public String getCron() {
            return cron;
        }

        public String getCrawlerName() {
            return crawlerName;
        }

        public String getExistingCrawlerName() {
            return existingCrawlerName;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public String getExistingDatabaseName() {
            return existingDatabaseName;
        }

        public String getTableName() {
            return tableName;
        }

        public String getText() {
            return text;
        }

        public String getJobNameSc() {
            return jobNameSc;
        }

        public String getDbNameSc() {
            return dbNameSc;
        }

        public String getCrawlerNameSc() {
            return crawlerNameSc;
        }

        public String getS3PathSc() {
            return s3PathSc;
        }

        public String getScriptLocationSc() {
            return scriptLocationSc;
        }

        public String getLocationUri() {
            return locationUri;
        }

        public String getBucketNameSc() {
            return bucketNameSc;
        }
    }

}
