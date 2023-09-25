/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.workdocs.DownloadUserDoc;
import com.example.workdocs.ListUserDocs;
import com.example.workdocs.ListUsers;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import com.example.workdocs.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import java.io.*;
import  software.amazon.awssdk.regions.Region;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkdocsTest {
    private static WorkDocsClient workDocs;
    private static String orgId = "" ;
    private static String userEmail =  "" ;;
    private static String workdocsName =  "";
    private static String saveDocFullName = "";
    private static String docName= "";
    private static String docPath= "";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_WEST_2;
        workDocs = WorkDocsClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        orgId = values.getOrgId();
        userEmail = values.getUserEmail();
        workdocsName = values.getWorkdocsName();
        saveDocFullName = values.getSaveDocFullName();
        docName = values.getDocName();
        docPath = values.getDocPath();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = WorkdocsTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            orgId = prop.getProperty("orgId");
            userEmail = prop.getProperty("userEmail");
            workdocsName = prop.getProperty("workdocsName");
            saveDocFullName = prop.getProperty("saveDocFullName");
            docName = prop.getProperty("docName");
            docPath = prop.getProperty("docPath");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void UploadUserDoc() {
        assertDoesNotThrow(() ->UploadUserDocs.uploadDoc(workDocs, orgId, userEmail, docName, docPath));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DownloadUserDoc() {
        assertDoesNotThrow(() ->DownloadUserDoc.downloadDoc(workDocs, orgId, userEmail, workdocsName, saveDocFullName));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListUserDocs() {
        assertDoesNotThrow(() ->ListUserDocs.listDocs(workDocs, orgId, userEmail));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListUsers() {
        assertDoesNotThrow(() ->ListUsers.getAllUsers(workDocs, orgId));
        System.out.println("Test 4 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/workdocs";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/workdocs (an AWS Secrets Manager secret)")
    class SecretValues {
        private String orgId;
        private String userEmail;
        private String workdocsName;

        private String saveDocFullName;

        private String docName;
        private String docPath;


        public String getOrgId() {
            return orgId;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getWorkdocsName() {
            return workdocsName;
        }

        public String getSaveDocFullName() {
            return saveDocFullName;
        }

        public String getDocName() {
            return docName;
        }

        public String getDocPath() {
            return docPath;
        }
    }
}


