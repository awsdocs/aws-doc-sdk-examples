/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.glacier.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GlacierTest {
    private static GlacierClient glacier;
    private static String vaultName="";
    private static String strPath ="";
    private static String downloadVault="";
    private static String accountId="";
    private static String archiveId="";
    private static String emptyVault="";

    @BeforeAll
    public static void setUp() {
        glacier = GlacierClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        vaultName = values.getVaultName();
        strPath = values.getStrPath();
        downloadVault= values.getDownloadVault();
        accountId= values.getAccountId();
        emptyVault= values.getEmptyVault();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = GlacierTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            vaultName = prop.getProperty("vaultName");
            strPath = prop.getProperty("strPath");
            downloadVault= prop.getProperty("downloadVault");
            accountId= prop.getProperty("accountId");
            emptyVault= prop.getProperty("emptyVault");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateVault() {
        assertDoesNotThrow(() ->CreateVault.createGlacierVault(glacier, vaultName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeVault() {
        assertDoesNotThrow(() ->DescribeVault.describeGlacierVault(glacier, vaultName));
        System.out.println("Test 2 passed");
     }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListVaults() {
        assertDoesNotThrow(() ->ListVaults.listAllVault(glacier));
        System.out.println("Test 3 passed");
     }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void UploadArchive() {
        File myFile = new File(strPath);
        Path path = Paths.get(strPath);
        archiveId =  UploadArchive.uploadContent(glacier,path,vaultName,myFile);
        assertFalse(archiveId.isEmpty());
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ArchiveDownload() {
        assertDoesNotThrow(() ->ArchiveDownload.createJob( glacier, downloadVault, accountId));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void DeleteArchive() {
        assertDoesNotThrow(() ->DeleteArchive.deleteGlacierArchive(glacier, vaultName, accountId, archiveId));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteVault() {
        assertDoesNotThrow(() ->DeleteVault.deleteGlacierVault(glacier, emptyVault));
       System.out.println("Test 7 passed");
     }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/glacier";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/glacier (an AWS Secrets Manager secret)")
    class SecretValues {
        private String vaultName;
        private String strPath;
        private String downloadVault;

        private String emptyVault;

        private String accountId;

        public String getVaultName() {
            return vaultName;
        }

        public String getStrPath() {
            return strPath;
        }

        public String getDownloadVault() {
            return downloadVault;
        }

        public String getEmptyVault() {
            return emptyVault;
        }

        public String getAccountId() {
            return accountId;
        }
    }
}
