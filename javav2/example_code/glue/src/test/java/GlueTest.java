/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.glue.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.glue.GlueClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GlueTest {

    private static GlueClient glueClient;
    private static String crawlerName="";
    private static String cron="";
    private static String s3Path="";
    private static String IAM="";
    private static String databaseName="";
    private static String tableName="";
    private static String text="";
    private static String existingDatabaseName="";
    private static String existingCrawlerName="";
    private static String jobNameSc="";
    private static String s3PathSc="";
    private static String dbNameSc="";
    private static String crawlerNameSc="";
    private static String scriptLocationSc="";
    private static String locationUri="";
    private static String bucketNameSc="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {
        glueClient = GlueClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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
        text =  values.getText();
        existingDatabaseName = values.getExistingDatabaseName();
        existingCrawlerName = values.getExistingCrawlerName();
        jobNameSc =  values.getJobNameSc()+ java.util.UUID.randomUUID();;
        s3PathSc = values.getS3PathSc()+ java.util.UUID.randomUUID();;
        dbNameSc = values.getDbNameSc()+ java.util.UUID.randomUUID();
        crawlerNameSc = values.getCrawlerNameSc()+ java.util.UUID.randomUUID();
        scriptLocationSc = values.getScriptLocationSc();
        locationUri = values.getLocationUri();
        bucketNameSc = values.getBucketNameSc();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = GlueTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            crawlerName = prop.getProperty("crawlerName");
            s3Path = prop.getProperty("s3Path");
            cron = prop.getProperty("cron");
            IAM = prop.getProperty("IAM");
            databaseName = prop.getProperty("databaseName");
            tableName = prop.getProperty("tableName");
            text = prop.getProperty("text");
            existingDatabaseName = prop.getProperty("existingDatabaseName");
            existingCrawlerName = prop.getProperty("existingCrawlerName");
            jobNameSc = prop.getProperty("jobNameSc")+ java.util.UUID.randomUUID();;
            s3PathSc = prop.getProperty("s3PathSc")+ java.util.UUID.randomUUID();;
            dbNameSc = prop.getProperty("dbNameSc")+ java.util.UUID.randomUUID();
            crawlerNameSc = prop.getProperty("crawlerNameSc")+ java.util.UUID.randomUUID();
            scriptLocationSc = prop.getProperty("scriptLocationSc");
            locationUri = prop.getProperty("locationUri");
            bucketNameSc = prop.getProperty("bucketNameSc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void ScenarioTest() throws InterruptedException {
        GlueScenario.createDatabase(glueClient, dbNameSc, locationUri);
        GlueScenario.createGlueCrawler(glueClient, IAM, s3PathSc, cron, dbNameSc, crawlerNameSc);
        GlueScenario.getSpecificCrawler(glueClient, crawlerNameSc);
        GlueScenario.startSpecificCrawler(glueClient, crawlerNameSc);
        GlueScenario.getSpecificDatabase(glueClient, dbNameSc);

        System.out.println("Wait 5 min for the tables to become available");
        TimeUnit.MINUTES.sleep(5);// Sleep for 5 minute to get tables ready
        String myTableName = GlueScenario.getGlueTables(glueClient, dbNameSc);
        GlueScenario.createJob(glueClient, jobNameSc, IAM, scriptLocationSc);
        GlueScenario.startJob(glueClient, jobNameSc, dbNameSc, myTableName, bucketNameSc );
        GlueScenario.getAllJobs(glueClient);
        GlueScenario.getJobRuns(glueClient, jobNameSc);
        GlueScenario.deleteJob(glueClient, jobNameSc);
        System.out.println("*** Wait 5 MIN for the "+crawlerNameSc +" to stop");
        TimeUnit.MINUTES.sleep(5);
        GlueScenario.deleteDatabase(glueClient, dbNameSc);
        GlueScenario.deleteSpecificCrawler(glueClient, crawlerNameSc);
    }
    private static String getSecretValues() {
        // Get the Amazon RDS creds from Secrets Manager.
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

