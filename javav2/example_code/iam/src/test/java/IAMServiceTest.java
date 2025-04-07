// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.iam.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AccessKey;
import software.amazon.awssdk.services.iam.model.User;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IAMServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(IAMServiceTest.class);
    private static IamClient iam;
    private static String userName = "";
    private static String policyName = "";
    private static String roleName = "";
    private static String policyARN = "";
    private static String keyId = "";
    private static String accountAlias = "";
    private static String usernameSc = "";
    private static String policyNameSc = "";
    private static String roleNameSc = "";
    private static String roleSessionName = "";
    private static String fileLocationSc = "";
    private static String bucketNameSc = "";

    @BeforeAll
    public static void setUp() {
        Region region = Region.AWS_GLOBAL;
        iam = IamClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        userName = values.getUserName()+ UUID.randomUUID();
        policyName = values.getPolicyName() + UUID.randomUUID();;
        roleName = values.getRoleName() + UUID.randomUUID();;
        accountAlias = values.getAccountAlias();
        usernameSc = values.getUsernameSc();
        policyNameSc = values.getPolicyNameSc();
        roleNameSc = values.getRoleNameSc();
        roleSessionName = values.getRoleName() + UUID.randomUUID();;
        fileLocationSc = values.getFileLocationSc();
        bucketNameSc = values.getBucketNameSc();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreatUser() {
        String result = CreateUser.createIAMUser(iam, userName);
        assertFalse(result.isEmpty());
        logger.info("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreatePolicy() {
        policyARN = CreatePolicy.createIAMPolicy(iam, policyName);
        assertFalse(policyARN.isEmpty());
        logger.info("\n Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testCreateAccessKey() {
        keyId = CreateAccessKey.createIAMAccessKey(iam, userName);
        assertFalse(keyId.isEmpty());
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetPolicy() {
        assertDoesNotThrow(() -> GetPolicy.getIAMPolicy(iam, policyARN));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListAccessKeys() {
        assertDoesNotThrow(() -> ListAccessKeys.listKeys(iam, userName));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListUsers() {
        assertDoesNotThrow(() -> ListUsers.listAllUsers(iam));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testCreateAccountAlias() {
        assertDoesNotThrow(() -> CreateAccountAlias.createIAMAccountAlias(iam, accountAlias));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDeleteAccountAlias() {
        assertDoesNotThrow(() -> DeleteAccountAlias.deleteIAMAccountAlias(iam, accountAlias));
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDeletePolicy() {
        assertDoesNotThrow(() -> DeletePolicy.deleteIAMPolicy(iam, policyARN));
        logger.info("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDeleteAccessKey() {
        assertDoesNotThrow(() -> DeleteAccessKey.deleteKey(iam, userName, keyId));
        logger.info("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testDeleteUser() {
        assertDoesNotThrow(() -> DeleteUser.deleteIAMUser(iam, userName));
        logger.info("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testIAMScenario() throws Exception {
        String DASHES = new String(new char[80]).replace("\0", "-");
        System.out.println(DASHES);
        System.out.println(" 1. Create the IAM user.");
        User createUser = IAMScenario.createIAMUser(iam, usernameSc);

        System.out.println(DASHES);
        String userArn = createUser.arn();
        AccessKey myKey = IAMScenario.createIAMAccessKey(iam, usernameSc);
        String accessKey = myKey.accessKeyId();
        String secretKey = myKey.secretAccessKey();
        String assumeRolePolicyDocument = "{" +
                "\"Version\": \"2012-10-17\"," +
                "\"Statement\": [{" +
                "\"Effect\": \"Allow\"," +
                "\"Principal\": {" +
                "	\"AWS\": \"" + userArn + "\"" +
                "}," +
                "\"Action\": \"sts:AssumeRole\"" +
                "}]" +
                "}";

        System.out.println(assumeRolePolicyDocument);
        System.out.println(usernameSc + " was successfully created.");
        System.out.println(DASHES);
        System.out.println("2. Creates a policy.");
        String polArn = IAMScenario.createIAMPolicy(iam, policyNameSc);
        System.out.println("The policy " + polArn + " was successfully created.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Creates a role.");
        TimeUnit.SECONDS.sleep(30);
        String roleArn = IAMScenario.createIAMRole(iam, roleNameSc, assumeRolePolicyDocument);
        System.out.println(roleArn + " was successfully created.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Grants the user permissions.");
        IAMScenario.attachIAMRolePolicy(iam, roleNameSc, polArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("*** Wait for 30 secs so the resource is available");
        TimeUnit.SECONDS.sleep(30);
        System.out.println("5. Gets temporary credentials by assuming the role.");
        System.out.println("Perform an Amazon S3 Service operation using the temporary credentials.");
        IAMScenario.assumeRole(roleArn, roleSessionName, bucketNameSc, accessKey, secretKey);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6 Getting ready to delete the AWS resources");
        IAMScenario.deleteKey(iam, usernameSc, accessKey);
        IAMScenario.deleteRole(iam, roleNameSc, polArn);
        IAMScenario.deleteIAMUser(iam, usernameSc);
        System.out.println(DASHES);
        logger.info("Test 12 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/iam";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/iam (an AWS Secrets Manager secret)")
    class SecretValues {
        private String userName;
        private String policyName;
        private String roleName;

        private String accountAlias;

        private String usernameSc;

        private String policyNameSc;

        private String roleNameSc;

        private String roleSessionName;

        private String fileLocationSc;

        private String bucketNameSc;

        public String getUserName() {
            return userName;
        }

        public String getPolicyName() {
            return policyName;
        }

        public String getRoleName() {
            return roleName;
        }

        public String getAccountAlias() {
            return accountAlias;
        }

        public String getUsernameSc() {
            return usernameSc;
        }

        public String getPolicyNameSc() {
            return policyNameSc;
        }

        public String getRoleNameSc() {
            return roleSessionName;
        }

        public String getFileLocationSc() {
            return fileLocationSc;
        }

        public String getBucketNameSc() {
            return bucketNameSc;
        }
    }
}
