// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.scenario.SSMActions;
import com.example.scenario.SSMScenario;
import com.example.ssm.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import static org.junit.jupiter.api.Assertions.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSSMTest {
    private static final Logger logger = LoggerFactory.getLogger(AWSSSMTest.class);
    private static SsmClient ssmClient;
    private static String paraName = "";
    private static String title = "";

    private static String instance = "";
    private static String source = "";
    private static String category = "";
    private static String severity = "";
    private static String opsItemId = "";

    private static String account = "";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        ssmClient = SsmClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        paraName = values.getParaName();
        paraName = values.getParaName();
        title = values.getTitle();
        source = values.getSource();
        category = values.getCategory();
        account = values.getAccount();
        instance = values.getInstanceId();
        severity = values.getSeverity();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloSSM() {
        assertDoesNotThrow(() -> HelloSSM.listDocuments(ssmClient, account));
        logger.info("Integration Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testGetParameter() {
        assertDoesNotThrow(() -> GetParameter.getParaValue(ssmClient, paraName));
        logger.info("Integration Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testInvokeScenario() {
        SSMActions actions = new SSMActions();
        String currentDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String maintenanceWindowName = "windowmain_" + currentDateTime;
        String title = "Disk Space Alert";
        String documentName = "doc_" + currentDateTime;

        // Assuming the createMaintenanceWindow method exists and is implemented correctly in SSMActions
        String maintenanceWindowId = assertDoesNotThrow(() -> actions.createMaintenanceWindow(maintenanceWindowName));
        assertDoesNotThrow(() -> actions.updateSSMMaintenanceWindow(maintenanceWindowId, maintenanceWindowName));
        assertDoesNotThrow(() -> actions.createSSMDoc(documentName));

        // Assuming 'instance' is defined and accessible
        String commandId = assertDoesNotThrow(() -> actions.sendSSMCommand(documentName, instance));
        assertDoesNotThrow(() -> actions.displayCommands(commandId));

        // Assuming 'source', 'category', and 'severity' are defined and accessible
        String opsItemId = assertDoesNotThrow(() -> actions.createSSMOpsItem(title, source, category, severity));
        String description = "An update to " + opsItemId;
        assertDoesNotThrow(() -> actions.updateOpsItem(opsItemId, title, description));
        assertDoesNotThrow(() -> actions.describeOpsItems(opsItemId));
        assertDoesNotThrow(() -> actions.resolveOpsItem(opsItemId));
        assertDoesNotThrow(() -> actions.deleteDoc(documentName));
        assertDoesNotThrow(() -> actions.deleteMaintenanceWindow(maintenanceWindowId));

        logger.info("Test 3 passed");
    }

   private static String getSecretValues() {
       SecretsManagerClient secretClient = SecretsManagerClient.builder()
           .region(Region.US_EAST_1)
           .build();
        String secretName = "test/ssm";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/ssm (an AWS Secrets Manager secret)")
    class SecretValues {
        private String paraName;
        private String source;
        private String category;

        private String severity;

        private String title;

        private String account ;

        private String instance ;

        public String getParaName() {
            return paraName;
        }

        public String getSource() {
            return source;
        }

        public String getCategory() {
            return category;
        }

        public String getSeverity() {
            return severity;
        }

        public String getTitle() {
            return title;
        }

        public String getAccount() {
            return account;
        }
        public String getInstanceId() {
            return instance;
        }


    }
}
